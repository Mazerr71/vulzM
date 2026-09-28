package com.vulzm.vulzm.client.gui;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.module.ModuleManager;
import com.vulzm.vulzm.client.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * HUD Editor drag-and-drop.
 *
 * Drag dideteksi dengan POLLING status tombol kiri mouse via GLFW tiap frame
 * (sama seperti pendekatan Xoldium), bukan override mouseDragged. Jadi tidak
 * bergantung pada signature input Minecraft yang berubah antar versi.
 */
public class HudEditorScreen extends Screen {
    private final Screen parent;

    private HudModule dragging = null;
    private float dragOffX, dragOffY;
    private boolean wasDown = false;
    private HudModule selected = null;

    public HudEditorScreen(Screen parent) {
        super(Text.translatable("gui.vulzm.hud_editor.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.hud_editor.done"), b -> close())
                .dimensions(this.width / 2 - 100, this.height - 28, 60, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.hud_editor.smaller"),
                b -> scaleSelected(-0.1f)).dimensions(this.width / 2 - 34, this.height - 28, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.hud_editor.bigger"),
                b -> scaleSelected(+0.1f)).dimensions(this.width / 2 + 4, this.height - 28, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.hud_editor.reset"),
                b -> resetSelected()).dimensions(this.width / 2 + 40, this.height - 28, 60, 20).build());
    }

    private void scaleSelected(float delta) {
        if (selected != null) selected.setScale(selected.scale() + delta);
    }

    private void resetSelected() {
        if (selected != null) selected.setScale(1.0f);
    }

    // ---------- polling pointer ----------

    private static boolean leftDown() {
        long h = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    private static boolean overButtonBar(int my, int screenH) {
        return my >= screenH - 34;
    }

    private void pollPointer(int mx, int my) {
        boolean down = leftDown();
        if (down && !wasDown) onPress(mx, my);
        else if (down && dragging != null) onDrag(mx, my);
        else if (!down) dragging = null;
        wasDown = down;
    }

    private void onPress(int mx, int my) {
        if (overButtonBar(my, this.height)) return;
        List<HudModule> huds = ModuleManager.hudModules();
        // Dari belakang: modul yang digambar terakhir berada di atas.
        for (int i = huds.size() - 1; i >= 0; i--) {
            HudModule h = huds.get(i);
            if (!h.isEnabled()) continue;
            if (isHovering(h, mx, my)) {
                dragging = h;
                selected = h;
                dragOffX = mx - h.x();
                dragOffY = my - h.y();
                return;
            }
        }
        selected = null;
    }

    private void onDrag(int mx, int my) {
        if (dragging == null) return;
        dragging.setPosition(mx - dragOffX, my - dragOffY);
        clampInside(dragging);
    }

    private boolean isHovering(HudModule h, double mx, double my) {
        return mx >= h.x() && mx <= h.x() + h.width()
            && my >= h.y() && my <= h.y() + h.height();
    }

    /** Jaga agar HUD tidak keluar layar. */
    private void clampInside(HudModule h) {
        float maxX = Math.max(0, this.width - h.width());
        float maxY = Math.max(0, this.height - h.height());
        h.setPosition(Math.max(0, Math.min(maxX, h.x())),
                      Math.max(0, Math.min(maxY, h.y())));
    }

    // ---------- render ----------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x90080404);

        pollPointer(mouseX, mouseY);
        renderGuides(ctx);

        var tick = MinecraftClient.getInstance().getRenderTickCounter();
        for (HudModule h : ModuleManager.hudModules()) {
            if (!h.isEnabled()) continue;
            try {
                h.renderPreview(ctx, tick);
            } catch (Throwable t) {
                h.markCrashed(t);
                continue;
            }
            boolean sel = h == selected;
            boolean hov = isHovering(h, mouseX, mouseY);
            if (sel || hov) outline(ctx, h, sel ? Theme.LAVA_BRIGHT : Theme.withAlpha(Theme.LAVA, 140));
        }

        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("gui.vulzm.hud_editor.help"),
                this.width / 2, 8, Theme.EMBER);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderGuides(DrawContext ctx) {
        int c = Theme.withAlpha(Theme.LAVA, 60);
        ctx.fill(this.width / 2, 0, this.width / 2 + 1, this.height, c);
        ctx.fill(0, this.height / 2, this.width, this.height / 2 + 1, c);
    }

    private void outline(DrawContext ctx, HudModule h, int color) {
        int x1 = (int) h.x() - 1, y1 = (int) h.y() - 1;
        int x2 = (int) (h.x() + h.width()) + 1, y2 = (int) (h.y() + h.height()) + 1;
        ctx.fill(x1, y1, x2, y1 + 1, color);
        ctx.fill(x1, y2 - 1, x2, y2, color);
        ctx.fill(x1, y1, x1 + 1, y2, color);
        ctx.fill(x2 - 1, y1, x2, y2, color);
    }

    @Override
    public void close() {
        dragging = null;
        ModuleManager.saveState();
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() { return false; }
}

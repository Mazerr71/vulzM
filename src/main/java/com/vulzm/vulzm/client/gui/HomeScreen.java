package com.vulzm.vulzm.client.gui;

import com.vulzm.vulzm.client.util.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Layar awal: pintu ke Mod Menu dan HUD Editor. */
public class HomeScreen extends Screen {
    private final Screen parent;

    public HomeScreen(Screen parent) {
        super(Text.translatable("gui.vulzm.home.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 10;
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.home.mod_menu"),
                b -> this.client.setScreen(new ModListScreen(this)))
                .dimensions(cx - 80, y, 160, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.home.hud_editor"),
                b -> this.client.setScreen(new HudEditorScreen(this)))
                .dimensions(cx - 80, y + 28, 160, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.settings.back"),
                b -> close())
                .dimensions(cx - 80, y + 62, 160, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0080404);

        float t = (System.currentTimeMillis() % 100000L) / 1000f;
        int cx = this.width / 2;
        int top = this.height / 2 - 70;

        // Logo teks: "vulzM" berdenyut seperti magma
        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(cx, top);
        ctx.getMatrices().scale(4.0f, 4.0f);
        int w = this.textRenderer.getWidth("vulzM");
        ctx.drawText(this.textRenderer, "vulzM", -w / 2, 0, Theme.pulse(t), true);
        ctx.getMatrices().popMatrix();

        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("gui.vulzm.home.tagline"), cx, top + 40, Theme.TEXT_DIM);

        // Garis lava di bawah logo
        ctx.fill(cx - 90, top + 34, cx + 90, top + 35, Theme.withAlpha(Theme.LAVA, 180));

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void close() { this.client.setScreen(parent); }

    @Override
    public boolean shouldPause() { return false; }
}

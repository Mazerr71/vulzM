package com.vulzm.vulzm.client.gui;

import com.vulzm.vulzm.client.module.Module;
import com.vulzm.vulzm.client.module.ModuleCategory;
import com.vulzm.vulzm.client.module.ModuleManager;
import com.vulzm.vulzm.client.util.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Mod Menu vulzM: tab kategori di atas, grid kartu modul di bawah.
 * Klik kartu = toggle. Tombol "..." di kartu = buka pengaturan.
 *
 * Input ditangani lewat ButtonWidget (hit-area transparan), bukan override
 * mouseClicked/keyPressed, sehingga tidak bergantung pada signature input
 * yang berubah antar versi Minecraft.
 */
public class ModListScreen extends Screen {
    private static final int CARD_W = 118;
    private static final int CARD_H = 44;
    private static final int GAP = 8;
    private static final int TAB_H = 20;

    private final Screen parent;
    private ModuleCategory tab = ModuleCategory.ALL;
    private int panelX, panelY, panelW, panelH;

    public ModListScreen(Screen parent) {
        super(Text.translatable("gui.vulzm.mod_menu.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        panelW = Math.min(this.width - 40, 4 * CARD_W + 5 * GAP);
        panelH = Math.min(this.height - 40, 300);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        rebuild();
    }

    /** Membangun ulang tombol hit-area sesuai tab aktif. */
    private void rebuild() {
        this.clearChildren();

        // Tab kategori
        int x = panelX + 10;
        int y = panelY + 32;
        for (ModuleCategory cat : ModuleCategory.values()) {
            int w = this.textRenderer.getWidth(Text.translatable(cat.key())) + 16;
            addDrawableChild(hit(x, y, w, TAB_H, () -> {
                tab = cat;
                rebuild();
            }));
            x += w + 4;
        }

        // Kartu modul
        List<Module> mods = ModuleManager.byCategory(tab);
        int cols = Math.max(1, (panelW - GAP) / (CARD_W + GAP));
        int startX = panelX + GAP;
        int startY = panelY + 32 + TAB_H + 10;
        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int cx = startX + (i % cols) * (CARD_W + GAP);
            int cy = startY + (i / cols) * (CARD_H + GAP);
            if (cy + CARD_H > panelY + panelH - 18) break;

            addDrawableChild(hit(cx, cy, CARD_W, CARD_H, () -> {
                if (!m.isCrashed()) m.toggle();
            }));
            if (m.hasSettings()) {
                // Tombol "..." kecil; dipasang setelah kartu agar berada di atasnya.
                addDrawableChild(hit(cx + CARD_W - 24, cy + CARD_H - 18, 22, 16, () ->
                        this.client.setScreen(new ModuleSettingsScreen(this, m))));
            }
        }
    }

    /**
     * ButtonWidget sebagai area klik. Memakai builder() (API publik yang stabil).
     * Tampilan tombol bawaan ditutup kembali oleh kartu yang digambar setelahnya.
     */
    private ButtonWidget hit(int x, int y, int w, int h, Runnable action) {
        return ButtonWidget.builder(Text.empty(), b -> action.run())
                .dimensions(x, y, w, h).build();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xB0080404);

        // Widget hit-area digambar PERTAMA (tekstur bawaannya akan tertutup panel di bawah).
        // Input tidak bergantung pada urutan gambar, jadi klik tetap sampai ke tombol.
        super.render(ctx, mouseX, mouseY, delta);

        ctx.fill(panelX - 1, panelY - 1, panelX + panelW + 1, panelY + panelH + 1, Theme.LAVA);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, Theme.OBSIDIAN);

        float t = (System.currentTimeMillis() % 100000L) / 1000f;
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 26, Theme.OBSIDIAN_LIGHT);
        ctx.fill(panelX, panelY + 25, panelX + panelW, panelY + 26, Theme.pulse(t));
        ctx.drawText(this.textRenderer, "vulzM", panelX + 10, panelY + 9, Theme.LAVA_BRIGHT, true);
        ctx.drawText(this.textRenderer, this.title, panelX + 50, panelY + 9, Theme.TEXT_DIM, false);

        drawTabs(ctx, mouseX, mouseY);
        drawCards(ctx, mouseX, mouseY);

        ctx.drawText(this.textRenderer, Text.translatable("gui.vulzm.mod_menu.hint"),
                panelX + 10, panelY + panelH - 12, Theme.TEXT_DIM, false);
    }

    private void drawTabs(DrawContext ctx, int mx, int my) {
        int x = panelX + 10;
        int y = panelY + 32;
        for (ModuleCategory cat : ModuleCategory.values()) {
            Text label = Text.translatable(cat.key());
            int w = this.textRenderer.getWidth(label) + 16;
            boolean active = cat == tab;
            boolean hover = mx >= x && mx < x + w && my >= y && my < y + TAB_H;
            int bg = active ? Theme.LAVA : (hover ? Theme.PANEL_HOVER : Theme.PANEL);
            ctx.fill(x, y, x + w, y + TAB_H, bg);
            ctx.drawText(this.textRenderer, label, x + 8, y + 6,
                    active ? 0xFF1A0A05 : Theme.TEXT, false);
            x += w + 4;
        }
    }

    private void drawCards(DrawContext ctx, int mx, int my) {
        List<Module> mods = ModuleManager.byCategory(tab);
        int cols = Math.max(1, (panelW - GAP) / (CARD_W + GAP));
        int startX = panelX + GAP;
        int startY = panelY + 32 + TAB_H + 10;

        if (mods.isEmpty()) {
            ctx.drawText(this.textRenderer, Text.translatable("gui.vulzm.mod_menu.empty"),
                    startX, startY, Theme.TEXT_DIM, false);
            return;
        }

        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int cx = startX + (i % cols) * (CARD_W + GAP);
            int cy = startY + (i / cols) * (CARD_H + GAP);
            if (cy + CARD_H > panelY + panelH - 18) break;

            boolean hover = mx >= cx && mx < cx + CARD_W && my >= cy && my < cy + CARD_H;
            boolean on = m.isEnabled();

            ctx.fill(cx, cy, cx + CARD_W, cy + CARD_H, hover ? Theme.PANEL_HOVER : Theme.PANEL);
            ctx.fill(cx, cy, cx + 3, cy + CARD_H, on ? Theme.LAVA : 0xFF3A2A24);

            ctx.drawText(this.textRenderer, m.displayName(), cx + 10, cy + 8, Theme.TEXT, true);

            Text status = m.isCrashed()
                    ? Text.translatable("gui.vulzm.mod_menu.crashed")
                    : Text.translatable(on ? "gui.vulzm.state.on" : "gui.vulzm.state.off");
            int sc = m.isCrashed() ? Theme.MAGMA_RED : (on ? Theme.TEXT_ON : Theme.TEXT_OFF);
            ctx.drawText(this.textRenderer, status, cx + 10, cy + 22, sc, false);

            if (m.hasSettings()) {
                ctx.drawText(this.textRenderer, "...", cx + CARD_W - 16, cy + CARD_H - 14,
                        Theme.TEXT_DIM, false);
            }
        }
    }

    @Override
    public void close() {
        ModuleManager.saveState();
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() { return false; }
}

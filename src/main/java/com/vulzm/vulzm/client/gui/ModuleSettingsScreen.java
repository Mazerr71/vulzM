package com.vulzm.vulzm.client.gui;

import com.vulzm.vulzm.client.module.Module;
import com.vulzm.vulzm.client.module.ModuleManager;
import com.vulzm.vulzm.client.module.setting.*;
import com.vulzm.vulzm.client.util.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Pengaturan satu modul. Semua kontrol adalah ButtonWidget:
 *  - Boolean : klik untuk toggle
 *  - Mode    : klik untuk ganti mode
 *  - Double  : tombol [-] dan [+] (langkah = step setting)
 */
public class ModuleSettingsScreen extends Screen {
    private static final int ROW_H = 24;
    private static final int PANEL_W = 300;

    private final Screen parent;
    private final Module module;
    private int panelX, panelY, panelH;

    private record Row(ModuleSetting<?> setting, int y) {}
    private final List<Row> rows = new ArrayList<>();

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(Text.translatable("gui.vulzm.settings.title"));
        this.parent = parent;
        this.module = module;
    }

    @Override
    protected void init() {
        this.clearChildren();
        rows.clear();

        List<ModuleSetting<?>> visible = new ArrayList<>();
        for (ModuleSetting<?> s : module.settings()) if (s.isVisible()) visible.add(s);

        panelH = 50 + visible.size() * ROW_H + 40;
        panelX = (this.width - PANEL_W) / 2;
        panelY = Math.max(10, (this.height - panelH) / 2);

        int y = panelY + 40;
        for (ModuleSetting<?> s : visible) {
            rows.add(new Row(s, y));
            buildControl(s, panelX + PANEL_W - 110, y);
            y += ROW_H;
        }

        // Tombol Kembali
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.vulzm.settings.back"), b -> close())
                .dimensions(panelX + PANEL_W / 2 - 40, panelY + panelH - 28, 80, 20).build());
    }

    private void buildControl(ModuleSetting<?> s, int x, int y) {
        if (s instanceof BooleanSetting b) {
            addDrawableChild(ButtonWidget.builder(stateText(b.isOn()), btn -> {
                b.toggle();
                persist();
                refresh();
            }).dimensions(x, y, 100, 18).build());

        } else if (s instanceof ModeSetting m) {
            addDrawableChild(ButtonWidget.builder(Text.literal(m.get()), btn -> {
                m.cycle();
                persist();
                refresh();
            }).dimensions(x, y, 100, 18).build());

        } else if (s instanceof DoubleSetting d) {
            addDrawableChild(ButtonWidget.builder(Text.literal("-"), btn -> {
                d.set(d.get() - stepOf(d));
                persist();
            }).dimensions(x, y, 22, 18).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("+"), btn -> {
                d.set(d.get() + stepOf(d));
                persist();
            }).dimensions(x + 78, y, 22, 18).build());
        }
    }

    private static double stepOf(DoubleSetting d) {
        return d.step() > 0 ? d.step() : (d.max() - d.min()) / 20.0;
    }

    private static Text stateText(boolean on) {
        return Text.translatable(on ? "gui.vulzm.state.on" : "gui.vulzm.state.off");
    }

    private void persist() { ModuleManager.saveState(); }

    /** Bangun ulang tombol (label ON/OFF/mode berubah). */
    private void refresh() { this.clearAndInit(); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xB0080404);
        ctx.fill(panelX - 1, panelY - 1, panelX + PANEL_W + 1, panelY + panelH + 1, Theme.LAVA);
        ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + panelH, Theme.OBSIDIAN);

        float t = (System.currentTimeMillis() % 100000L) / 1000f;
        ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + 28, Theme.OBSIDIAN_LIGHT);
        ctx.fill(panelX, panelY + 27, panelX + PANEL_W, panelY + 28, Theme.pulse(t));
        ctx.drawText(this.textRenderer, module.displayName(), panelX + 10, panelY + 10,
                Theme.LAVA_BRIGHT, true);

        for (Row r : rows) {
            ModuleSetting<?> s = r.setting();
            ctx.drawText(this.textRenderer, Text.translatable(s.nameKey()),
                    panelX + 12, r.y() + 5, Theme.TEXT, false);

            if (s instanceof DoubleSetting d) {
                // Nilai + bar progres di antara tombol - dan +
                int bx = panelX + PANEL_W - 110 + 24;
                int bw = 52;
                ctx.fill(bx, r.y() + 7, bx + bw, r.y() + 11, 0xFF2A1A14);
                ctx.fill(bx, r.y() + 7, bx + (int) (bw * d.progress()), r.y() + 11, Theme.LAVA);
                String v = d.step() >= 1.0
                        ? String.valueOf(d.get().intValue())
                        : String.format("%.2f", d.get());
                ctx.drawText(this.textRenderer, v, bx + bw / 2 - this.textRenderer.getWidth(v) / 2,
                        r.y() - 4, Theme.EMBER, false);
            }
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        module.saveSettings();
        ModuleManager.saveState();
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() { return false; }
}

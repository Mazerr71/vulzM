package com.vulzm.vulzm.client.module;

import com.vulzm.vulzm.client.module.setting.DoubleSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/** Modul HUD yang punya posisi, skala, dan opasitas latar, dan bisa digeser di HUD Editor. */
public abstract class HudModule extends Module {
    public static final float MIN_SCALE = 0.5f;
    public static final float MAX_SCALE = 3.0f;

    private float x, y;
    private float scale = 1.0f;
    private float measuredWidth = 60, measuredHeight = 12;
    protected final DoubleSetting backgroundOpacity;

    protected HudModule(String id, String nameKey, boolean defaultEnabled, float defaultX, float defaultY) {
        super(id, nameKey, ModuleCategory.HUD, defaultEnabled);
        this.x = defaultX;
        this.y = defaultY;
        this.backgroundOpacity = addSetting(new DoubleSetting(
                "background_opacity", "setting.vulzm.hud.background_opacity", 0.55, 0.0, 1.0, 0.05));
    }

    /** Dipanggil tiap frame HUD bila modul aktif. */
    public abstract void render(DrawContext context, RenderTickCounter tickCounter);

    /** Untuk pratinjau di HUD Editor (default: sama dengan render). */
    public void renderPreview(DrawContext context, RenderTickCounter tickCounter) {
        render(context, tickCounter);
    }

    public float x() { return x; }
    public float y() { return y; }
    public float scale() { return scale; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setScale(float s) { this.scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, s)); }

    public void setMeasuredSize(float rawWidth, float rawHeight) {
        this.measuredWidth = rawWidth;
        this.measuredHeight = rawHeight;
    }
    public float width() { return measuredWidth * scale; }
    public float height() { return measuredHeight * scale; }
    public double backgroundOpacityValue() { return backgroundOpacity.get(); }
}

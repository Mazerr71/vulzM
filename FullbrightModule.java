package com.vulzm.vulzm.client.module.visual;

import com.vulzm.vulzm.client.module.Module;
import com.vulzm.vulzm.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;

/**
 * Terang penuh dengan menaikkan opsi Gamma.
 * Tanpa mixin dan tanpa menyentuh renderer, jadi aman dengan Sodium/VulkanMod.
 * Nilai gamma asli disimpan lalu dipulihkan saat modul dimatikan.
 */
public class FullbrightModule extends Module {
    private static final double BRIGHT = 16.0;
    private double originalGamma = 1.0;
    private boolean applied = false;

    public FullbrightModule() {
        super("fullbright", "module.vulzm.fullbright", ModuleCategory.VISUAL, false);
    }

    @Override
    protected void onToggle(boolean enabled) {
        if (!enabled) restore();
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (client.options == null) return;
        var gamma = client.options.getGamma();
        if (!applied) {
            originalGamma = gamma.getValue();
            applied = true;
        }
        if (gamma.getValue() < BRIGHT) {
            // setValue mengabaikan batas slider UI; ini disengaja untuk efek fullbright.
            gamma.setValue(BRIGHT);
        }
    }

    private void restore() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (applied && mc != null && mc.options != null) {
            mc.options.getGamma().setValue(originalGamma);
        }
        applied = false;
    }
}

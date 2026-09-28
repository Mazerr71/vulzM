package com.vulzm.vulzm.client.module.movement;

import com.vulzm.vulzm.client.module.Module;
import com.vulzm.vulzm.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;

/** Otomatis sprint saat berjalan maju. */
public class SprintModule extends Module {
    public SprintModule() {
        super("auto_sprint", "module.vulzm.auto_sprint", ModuleCategory.MOVEMENT, false);
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        var p = client.player;
        if (p == null) return;
        if (client.options.forwardKey.isPressed()
                && !p.isSneaking()
                && !p.isUsingItem()
                && !p.horizontalCollision
                && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}

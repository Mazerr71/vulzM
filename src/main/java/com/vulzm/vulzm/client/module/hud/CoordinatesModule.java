package com.vulzm.vulzm.client.module.hud;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.module.setting.BooleanSetting;
import com.vulzm.vulzm.client.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class CoordinatesModule extends HudModule {
    private final BooleanSetting showDecimals =
            addSetting(new BooleanSetting("decimals", "setting.vulzm.coords.decimals", false));

    public CoordinatesModule() {
        super("coordinates", "module.vulzm.coordinates", true, 6, 24);
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            HudRenderUtil.drawLine(ctx, this, "XYZ: -");
            return;
        }
        String x, y, z;
        if (showDecimals.isOn()) {
            x = String.format("%.1f", player.getX());
            y = String.format("%.1f", player.getY());
            z = String.format("%.1f", player.getZ());
        } else {
            x = String.valueOf((int) Math.floor(player.getX()));
            y = String.valueOf((int) Math.floor(player.getY()));
            z = String.valueOf((int) Math.floor(player.getZ()));
        }
        HudRenderUtil.drawLines(ctx, this,
                "X: " + x, "Y: " + y, "Z: " + z);
    }
}

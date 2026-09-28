package com.vulzm.vulzm.client.module.hud;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;

public class DirectionModule extends HudModule {
    private static final String[] NAMES = {
            "S", "SW", "W", "NW", "N", "NE", "E", "SE"
    };

    public DirectionModule() {
        super("direction", "module.vulzm.direction", true, 6, 102);
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            HudRenderUtil.drawLine(ctx, this, "Arah: -");
            return;
        }
        float yaw = MathHelper.wrapDegrees(player.getYaw());
        // 0 deg = selatan(+Z). Bagi jadi 8 sektor @45 deg.
        int idx = Math.floorMod(Math.round(yaw / 45f), 8);
        HudRenderUtil.drawLine(ctx, this, "Arah: " + NAMES[idx] + " (" + Math.round(yaw) + ")");
    }
}

package com.vulzm.vulzm.client.module.hud;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;

public class PingModule extends HudModule {
    public PingModule() {
        super("ping", "module.vulzm.ping", true, 6, 84);
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String text = "Ping: -";
        if (mc.player != null && mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) text = "Ping: " + e.getLatency() + " ms";
            else text = "Ping: singleplayer";
        }
        HudRenderUtil.drawLine(ctx, this, text);
    }
}

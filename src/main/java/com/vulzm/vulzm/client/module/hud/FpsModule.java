package com.vulzm.vulzm.client.module.hud;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class FpsModule extends HudModule {
    public FpsModule() {
        super("fps", "module.vulzm.fps", true, 6, 6);
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        int fps = MinecraftClient.getInstance().getCurrentFps();
        HudRenderUtil.drawLine(ctx, this, "FPS: " + fps);
    }
}

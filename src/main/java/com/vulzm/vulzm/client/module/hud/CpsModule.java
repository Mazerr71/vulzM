package com.vulzm.vulzm.client.module.hud;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.util.HudRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * CPS counter dengan polling tombol attack/use tiap tick (deteksi tepi naik).
 * Tanpa mixin: aman lintas versi Minecraft.
 */
public class CpsModule extends HudModule {
    private final Deque<Long> leftClicks = new ArrayDeque<>();
    private final Deque<Long> rightClicks = new ArrayDeque<>();
    private boolean wasLeft, wasRight;

    public CpsModule() {
        super("cps", "module.vulzm.cps", true, 6, 66);
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (client.options == null) return;
        long now = System.currentTimeMillis();

        boolean left = client.options.attackKey.isPressed();
        boolean right = client.options.useKey.isPressed();

        if (left && !wasLeft) leftClicks.addLast(now);
        if (right && !wasRight) rightClicks.addLast(now);
        wasLeft = left;
        wasRight = right;

        trim(leftClicks, now);
        trim(rightClicks, now);
    }

    private static void trim(Deque<Long> q, long now) {
        while (!q.isEmpty() && q.peekFirst() < now - 1000L) q.pollFirst();
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        HudRenderUtil.drawLine(ctx, this, "CPS: " + leftClicks.size() + " | " + rightClicks.size());
    }
}

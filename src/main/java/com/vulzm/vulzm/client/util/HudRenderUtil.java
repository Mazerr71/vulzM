package com.vulzm.vulzm.client.util;

import com.vulzm.vulzm.client.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Helper menggambar kotak HUD bertema magma.
 * 1.21.11 memakai Matrix3x2fStack (GUI 2D), bukan MatrixStack.
 */
public final class HudRenderUtil {
    private HudRenderUtil() {}

    private static final int PAD_X = 5;
    private static final int PAD_Y = 3;
    private static final int LINE_GAP = 2;

    private static int backgroundColor(HudModule m) {
        int alpha = (int) Math.round(m.backgroundOpacityValue() * 255.0);
        return Theme.withAlpha(0x140C0A, alpha);
    }

    public static void drawLine(DrawContext ctx, HudModule module, String text) {
        drawLines(ctx, module, new String[]{text});
    }

    public static void drawLines(DrawContext ctx, HudModule module, String... lines) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;

        int maxW = 0;
        for (String l : lines) maxW = Math.max(maxW, font.getWidth(l));
        int rawW = maxW + PAD_X * 2 + 2;               // +2 untuk garis aksen kiri
        int rawH = lines.length * font.fontHeight + (lines.length - 1) * LINE_GAP + PAD_Y * 2;
        module.setMeasuredSize(rawW, rawH);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(module.x(), module.y());
        ctx.getMatrices().scale(module.scale(), module.scale());

        // Latar obsidian
        ctx.fill(0, 0, rawW, rawH, backgroundColor(module));
        // Garis aksen lava di sisi kiri (ciri khas tema vulzM)
        ctx.fill(0, 0, 2, rawH, Theme.LAVA);
        // Garis tipis magma di bawah
        ctx.fill(2, rawH - 1, rawW, rawH, Theme.withAlpha(Theme.MAGMA_RED, 200));

        int ty = PAD_Y;
        for (String l : lines) {
            ctx.drawText(font, l, PAD_X + 2, ty, Theme.TEXT, true);
            ty += font.fontHeight + LINE_GAP;
        }

        ctx.getMatrices().popMatrix();
    }
}

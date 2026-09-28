package com.vulzm.vulzm.client.module.hud;

import com.vulzm.vulzm.client.module.HudModule;
import com.vulzm.vulzm.client.util.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Menampilkan armor + durability sebagai persen. */
public class ArmorModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final int ROW_H = 18;

    public ArmorModule() {
        super("armor", "module.vulzm.armor", true, 6, 122);
    }

    @Override
    public void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer font = mc.textRenderer;
        if (mc.player == null) return;

        int rawW = 62;
        int rawH = SLOTS.length * ROW_H + 4;
        setMeasuredSize(rawW, rawH);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(x(), y());
        ctx.getMatrices().scale(scale(), scale());

        int alpha = (int) Math.round(backgroundOpacityValue() * 255.0);
        ctx.fill(0, 0, rawW, rawH, Theme.withAlpha(0x140C0A, alpha));
        ctx.fill(0, 0, 2, rawH, Theme.LAVA);

        int cy = 2;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, 6, cy);
                if (stack.isDamageable()) {
                    int max = stack.getMaxDamage();
                    int left = max - stack.getDamage();
                    int pct = Math.round(100f * left / max);
                    int color = pct > 50 ? Theme.TEXT_ON : (pct > 20 ? Theme.EMBER : Theme.TEXT_OFF);
                    ctx.drawText(font, pct + "%", 26, cy + 4, color, true);
                }
            }
            cy += ROW_H;
        }
        ctx.getMatrices().popMatrix();
    }
}

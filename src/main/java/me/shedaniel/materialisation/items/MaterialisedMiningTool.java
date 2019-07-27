package me.shedaniel.materialisation.items;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public interface MaterialisedMiningTool {
    boolean canEffectivelyBreak(ItemStack itemStack, BlockState state);

    int getEnchantability(ItemStack stack);

    float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state);

    double getAttackSpeed();
}

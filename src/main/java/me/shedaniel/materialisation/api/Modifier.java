package me.shedaniel.materialisation.api;

import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class Modifier {
    public boolean applies(ItemStack stack) {
        return getMaximumLevel(stack) > 0;
    }

    public abstract int getMaximumLevel(ItemStack stack, MaterialisedMiningTool tool);

    public final int getMaximumLevel(ItemStack stack) {
        if (stack.getItem() instanceof MaterialisedMiningTool)
            return getMaximumLevel(stack, (MaterialisedMiningTool) stack.getItem());
        return 0;
    }

    public int getMaximumDurability(ItemStack stack, MaterialisedMiningTool tool, int level, int currentMax) {
        int modifierLevel = tool.getModifierLevel(stack, this);
        if (modifierLevel <= 0)
            return currentMax;
        return MathHelper.ceil(((double) currentMax) * Math.pow(.91, modifierLevel));
    }

    public final float getExtraMiningSpeed(ItemStack stack, int level, float base, float currentSpeed) {
        return getExtraMiningSpeed(stack, (MaterialisedMiningTool) stack.getItem(), level, base, currentSpeed);
    }

    public float getExtraMiningSpeed(ItemStack stack, MaterialisedMiningTool tool, int level, float base, float currentSpeed) {
        return 0f;
    }

    public final int getExtraMiningLevel(ItemStack stack, int level, int base, int currentLevel) {
        return getExtraMiningLevel(stack, (MaterialisedMiningTool) stack.getItem(), level, base, currentLevel);
    }

    public int getExtraMiningLevel(ItemStack stack, MaterialisedMiningTool tool, int level, int base, int currentLevel) {
        return 0;
    }

    public final int getExtraEnchantability(ItemStack stack, int level, int base, int currentEnchantability) {
        return getExtraEnchantability(stack, (MaterialisedMiningTool) stack.getItem(), level, base, currentEnchantability);
    }

    public int getExtraEnchantability(ItemStack stack, MaterialisedMiningTool tool, int level, int base, int currentEnchantability) {
        return 0;
    }
}

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
            return getMaximumLevel(stack, ((MaterialisedMiningTool) stack.getItem()));
        return 0;
    }

    public int getMaximumDurability(ItemStack stack, MaterialisedMiningTool tool, int currentMax) {
        int modifierLevel = tool.getModifierLevel(stack, this);
        if (modifierLevel <= 0)
            return currentMax;
        return MathHelper.ceil(((double) currentMax) * Math.pow(.91, modifierLevel));
    }
}

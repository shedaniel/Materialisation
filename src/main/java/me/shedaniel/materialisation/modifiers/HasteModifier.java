package me.shedaniel.materialisation.modifiers;

import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class HasteModifier extends Modifier {
    @Override
    public int getMaximumLevel(ItemStack stack, MaterialisedMiningTool tool) {
        switch (tool.getToolType()) {
            case AXE:
            case HAMMER:
            case MEGA_AXE:
            case PICKAXE:
            case SHOVEL:
                return 4;
            default:
                return 0;
        }
    }

    @Override
    public int getMaximumDurability(ItemStack stack, MaterialisedMiningTool tool, int currentMax) {
        int modifierLevel = tool.getModifierLevel(stack, this);
        if (modifierLevel <= 0)
            return currentMax;
        if (modifierLevel == 1) return MathHelper.ceil(((double) currentMax) * .93);
        if (modifierLevel == 2) return MathHelper.ceil(((double) currentMax) * .93 * .91);
        if (modifierLevel == 3) return MathHelper.ceil(((double) currentMax) * .93 * .91 * .89);
        if (modifierLevel == 4) return MathHelper.ceil(((double) currentMax) * .93 * .91 * .89 * .83);
        return MathHelper.ceil(((double) currentMax) * .93 * .91 * .89 * .87 * Math.pow(.91, modifierLevel - 4));
    }
}

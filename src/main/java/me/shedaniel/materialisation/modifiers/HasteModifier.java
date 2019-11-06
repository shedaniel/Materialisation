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
        }
        return 0;
    }

    @Override
    public int getMaximumDurability(ItemStack stack, MaterialisedMiningTool tool, int level, int currentMax) {
        if (level <= 0)
            return currentMax;
        double c = currentMax;
        if (level >= 1) c *= .93;
        if (level >= 2) c *= .87;
        if (level >= 3) c *= .84;
        if (level >= 4) c *= .79;
        if (level >= 5) c *= Math.pow(.7, level - 4);
        return MathHelper.ceil(c);
    }

    @Override
    public float getExtraMiningSpeed(ItemStack stack, MaterialisedMiningTool tool, int level, float base, float currentSpeed) {
        if (level <= 0)
            return 0f;
        return level * .5f * base;
    }
}

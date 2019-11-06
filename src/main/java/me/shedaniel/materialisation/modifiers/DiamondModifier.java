package me.shedaniel.materialisation.modifiers;

import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.item.ItemStack;

public class DiamondModifier extends Modifier {
    @Override
    public int getMaximumLevel(ItemStack stack, MaterialisedMiningTool tool) {
        switch (tool.getToolType()) {
            case AXE:
            case HAMMER:
            case MEGA_AXE:
            case PICKAXE:
            case SHOVEL:
                return 3;
        }
        return 0;
    }

    @Override
    public int getMaximumDurability(ItemStack stack, MaterialisedMiningTool tool, int level, int currentMax) {
        return currentMax + 50 * level;
    }
}

package me.shedaniel.materialisation.items;

import com.sun.istack.internal.NotNull;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ToolType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public interface MaterialisedMiningTool {
    boolean canEffectivelyBreak(ItemStack itemStack, BlockState state);

    default int getEnchantability(ItemStack stack) {
        return MaterialisationUtils.getToolEnchantability(stack);
    }

    float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state);

    double getAttackSpeed();

    @NotNull
    default ToolType getToolType() {
        return ToolType.UNKNOWN;
    }

    default void setModifierLevel(ItemStack stack, Modifier modifier, int level) {
        Identifier id = Materialisation.modifiers.getId(modifier);
        if (id == null)
            return;
        setModifierLevel(stack, id, level);
    }

    default int getModifierLevel(ItemStack stack, Modifier modifier) {
        Identifier id = Materialisation.modifiers.getId(modifier);
        if (id == null)
            return 0;
        return getModifierLevel(stack, id);
    }

    default int getModifierLevel(ItemStack stack, Identifier modifier) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.containsKey("modifiers")) {
            CompoundTag modifiers = tag.getCompound("modifiers");
            if (modifiers.containsKey(modifier.toString()))
                return modifiers.getInt(modifier.toString());
        }
        return 0;
    }

    default void setModifierLevel(ItemStack stack, Identifier modifier, int level) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.containsKey("modifiers"))
            tag.put("modifiers", new CompoundTag());
        CompoundTag modifiers = tag.getCompound("modifiers");
        modifiers.putInt(modifier.toString(), level);
    }
}

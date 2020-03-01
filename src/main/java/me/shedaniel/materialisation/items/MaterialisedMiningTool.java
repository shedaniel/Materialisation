package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ToolType;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;
import static me.shedaniel.materialisation.MaterialisationUtils.isHeadBright;

public interface MaterialisedMiningTool {
    static float getExtraDamage(ToolType toolType) {
        if (toolType == ToolType.SWORD)
            return 4f;
        if (toolType == ToolType.PICKAXE)
            return 2f;
        if (toolType == ToolType.AXE)
            return 7f;
        if (toolType == ToolType.MEGA_AXE)
            return 10f;
        if (toolType == ToolType.HAMMER)
            return 9f;
        if (toolType == ToolType.SHOVEL)
            return 2.5f;
        return 0f;
    }
    
    static float getExtraDamageFromItem(Item item) {
        if (item instanceof MaterialisedMiningTool)
            return getExtraDamage(((MaterialisedMiningTool) item).getToolType());
        return 0f;
    }
    
    boolean canEffectivelyBreak(ItemStack itemStack, BlockState state);
    
    default int getEnchantability(ItemStack stack) {
        return MaterialisationUtils.getToolEnchantability(stack);
    }
    
    float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state);
    
    double getAttackSpeed();
    
    default void initProperty() {
        ((Item) this).addPropertyGetter(new Identifier(ModReference.MOD_ID, "handle_isbright"),
                (itemStack, world, livingEntity) -> isHandleBright(itemStack) ? 1f : 0f);
        ((Item) this).addPropertyGetter(new Identifier(ModReference.MOD_ID, "tool_head_isbright"),
                (itemStack, world, livingEntity) -> isHeadBright(itemStack) ? 1f : 0f);
    }
    
    @Nonnull
    default ToolType getToolType() {
        return ToolType.UNKNOWN;
    }
    
    default void setModifierLevel(ItemStack stack, Modifier modifier, int level) {
        Identifier id = Materialisation.MODIFIERS.getId(modifier);
        if (id == null)
            return;
        setModifierLevel(stack, id, level);
    }
    
    default int getModifierLevel(ItemStack stack, Modifier modifier) {
        Identifier id = Materialisation.MODIFIERS.getId(modifier);
        if (id == null)
            return 0;
        return getModifierLevel(stack, id);
    }
    
    default int getModifierLevel(ItemStack stack, Identifier modifier) {
        return getModifierLevel(stack, modifier.toString());
    }
    
    default int getModifierLevel(ItemStack stack, String modifier) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("modifiers")) {
            CompoundTag modifiers = tag.getCompound("modifiers");
            if (modifiers.contains(modifier))
                return modifiers.getInt(modifier);
        }
        return 0;
    }
    
    default void setModifierLevel(ItemStack stack, Identifier modifier, int level) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("modifiers"))
            tag.put("modifiers", new CompoundTag());
        CompoundTag modifiers = tag.getCompound("modifiers");
        modifiers.putInt(modifier.toString(), level);
    }
}

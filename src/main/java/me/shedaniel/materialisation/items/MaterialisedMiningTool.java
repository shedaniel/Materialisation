package me.shedaniel.materialisation.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ToolType;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MaterialisedMiningTool extends FabricItem {

    Multimap<EntityAttribute, EntityAttributeModifier> EMPTY = ImmutableSetMultimap.of();

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

    @Nullable
    default TagKey<Block> getEffectiveBlocks() {
        TagKey<Block> minableBlock;
        if (this instanceof AxeItem)
            minableBlock = BlockTags.AXE_MINEABLE;
        else if (this instanceof PickaxeItem)
            minableBlock = BlockTags.PICKAXE_MINEABLE;
        else if (this instanceof ShovelItem)
            minableBlock = BlockTags.SHOVEL_MINEABLE;
        else if (this instanceof HoeItem)
            minableBlock = BlockTags.HOE_MINEABLE;
        else
            return null;
        return minableBlock;
    }

    @Override
    default boolean isSuitableFor(ItemStack stack, BlockState state) {
        int i = getMiningLevel(stack);
        if (i < 3 && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        } else if (i < 2 && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        } else {
            TagKey<Block> minableBlock = getEffectiveBlocks();
            if (minableBlock == null)
                return FabricItem.super.isSuitableFor(stack, state);
            return i < 1 && state.isIn(BlockTags.NEEDS_STONE_TOOL) ? false : state.isIn(minableBlock);
        }
    }

    static float getExtraDamageFromItem(Item item) {
        if (item instanceof MaterialisedMiningTool)
            return getExtraDamage(((MaterialisedMiningTool) item).getToolType());
        return 0f;
    }

    default float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        TagKey<Block> minableBlock = getEffectiveBlocks();
        if (minableBlock == null)
            return MaterialisationUtils.getToolBreakingSpeed(stack);
        if (state.isIn(minableBlock))
            return MaterialisationUtils.getToolBreakingSpeed(stack);
        else
            return 1.0F;
    }

    default int getMiningLevel(ItemStack stack) {
        return MaterialisationUtils.getToolMiningLevel(stack);
    }

    default float postProcessMiningSpeed(BlockState state, ItemStack stack, float currentSpeed, boolean isEffective) {
        return MaterialisationUtils.getToolDurability(stack) <= 0 ? -1 : currentSpeed;
    }

    default int getEnchantability(ItemStack stack) {
        return MaterialisationUtils.getToolEnchantability(stack);
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
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains("modifiers")) {
            NbtCompound modifiers = tag.getCompound("modifiers");
            if (modifiers.contains(modifier))
                return modifiers.getInt(modifier);
        }
        return 0;
    }

    default void setModifierLevel(ItemStack stack, Identifier modifier, int level) {
        NbtCompound tag = stack.getOrCreateNbt();
        if (!tag.contains("modifiers"))
            tag.put("modifiers", new NbtCompound());
        NbtCompound modifiers = tag.getCompound("modifiers");
        modifiers.putInt(modifier.toString(), level);
    }

    default Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND) return EMPTY;
        double attackDamage = MaterialisationUtils.getToolDurability(stack) > 0 ? MaterialisationUtils.getToolAttackDamage(stack) : -10000;
        if (attackDamage <= 0) return EMPTY;
        return ImmutableMultimap.of(
                EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(MaterialisationUtils.getItemModifierDamage(), "Tool modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION)
        );
    }
}

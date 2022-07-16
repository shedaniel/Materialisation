package me.shedaniel.materialisation.utils;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface DynamicAttributeTool {
    public Multimap<EntityAttribute, EntityAttributeModifier> EMPTY = ImmutableSetMultimap.of();

    @Deprecated
    default int getMiningLevel(ItemStack stack, @Nullable LivingEntity user) {
        return 0;
    }

    default int getMiningLevel(BlockState state, ItemStack stack, @Nullable LivingEntity user) {
        return getMiningLevel(stack, user);
    }

    default float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 1.0F;
    }
    default float getMiningSpeedMultiplier(ItemStack stack, BlockState state, @Nullable LivingEntity user) {
        return getMiningSpeedMultiplier(stack, state);
    }

    default float postProcessMiningSpeed(BlockState state, ItemStack stack, @Nullable LivingEntity user, float currentSpeed, boolean isEffective) {
        return currentSpeed;
    }

    default Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
        return EMPTY;
    }
}

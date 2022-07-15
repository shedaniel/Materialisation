package me.shedaniel.materialisation.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ToolType;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

public interface MaterialisedMiningTool extends DynamicAttributeTool {
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
    
    @Override
    default float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
        return MaterialisationUtils.getToolBreakingSpeed(stack);
    }
    
    @Override
    default int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
        return MaterialisationUtils.getToolMiningLevel(stack);
    }
    
    @Override
    default float postProcessMiningSpeed(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user, float currentSpeed, boolean isEffective) {
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

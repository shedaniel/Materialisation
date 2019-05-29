package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.KnownMaterial;
import me.shedaniel.materialisation.api.KnownMaterials;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class MaterialisationUtils {
    
    public static final NumberFormat TWO_DECIMAL_FORMATTER = new DecimalFormat("#.##");
    public static final ToolMaterial DUMMY_MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 0;
        }
        
        @Override
        public float getBlockBreakingSpeed() {
            return 1;
        }
        
        @Override
        public float getAttackDamage() {
            return 0;
        }
        
        @Override
        public int getMiningLevel() {
            return 0;
        }
        
        @Override
        public int getEnchantability() {
            return 0;
        }
        
        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };
    
    public static float getToolBreakingSpeed(ItemStack stack) {
        return stack.getOrCreateTag().containsKey("materialisedtools_breakingspeed") ? stack.getOrCreateTag().getFloat("materialisedtools_breakingspeed") : 1f;
    }
    
    public static int getToolMiningLevel(ItemStack stack) {
        return stack.getOrCreateTag().containsKey("materialisedtools_mininglevel") ? stack.getOrCreateTag().getInt("materialisedtools_mininglevel") : 0;
    }
    
    public static int getToolDurability(ItemStack stack) {
        return stack.getOrCreateTag().containsKey("materialisedtools_durability") ? stack.getOrCreateTag().getInt("materialisedtools_durability") : getToolMaxDurability(stack);
    }
    
    public static int getToolMaxDurability(ItemStack stack) {
        return stack.getOrCreateTag().containsKey("materialisedtools_maxdurability") ? stack.getOrCreateTag().getInt("materialisedtools_maxdurability") : 1;
    }
    
    public static int getItemLayerColor(ItemStack stack, int layer) {
        return stack.getOrCreateTag().containsKey("mt_color_" + layer) ? stack.getOrCreateTag().getInt("mt_color_" + layer) : 0;
    }
    
    public static void setToolDurability(ItemStack stack, int i) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("materialisedtools_durability", Math.min(i, getToolMaxDurability(stack)));
        stack.setTag(tag);
    }
    
    public static boolean applyDamage(ItemStack stack, int int_1, Random random_1) {
        if (getToolDurability(stack) <= 0) {
            return false;
        } else {
            int int_2;
            if (int_1 > 0) {
                int_2 = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
                int int_3 = 0;
                for(int int_4 = 0; int_2 > 0 && int_4 < int_1; ++int_4)
                    if (UnbreakingEnchantment.shouldPreventDamage(stack, int_2, random_1))
                        ++int_3;
                int_1 -= int_3;
                if (int_1 <= 0)
                    return false;
            }
            int_2 = getToolDurability(stack) - int_1;
            setToolDurability(stack, int_2);
            return int_2 < getToolDurability(stack);
        }
    }
    
    public static ItemStack createToolHandle(KnownMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.HANDLE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", material.getToolHandleColor());
        tag.putString("mt_material", material.getName());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createAxeHead(KnownMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.AXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", material.getToolHeadColor());
        tag.putString("mt_material", material.getName());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createPickaxeHead(KnownMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.PICKAXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", material.getToolHeadColor());
        tag.putString("mt_material", material.getName());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createShovelHead(KnownMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.SHOVEL_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", material.getToolHeadColor());
        tag.putString("mt_material", material.getName());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static KnownMaterial getMaterialFromPart(ItemStack stack) {
        if (stack.getOrCreateTag().containsKey("mt_material"))
            return getMaterialFromString(stack.getOrCreateTag().getString("mt_material"));
        else
            return null;
    }
    
    public static KnownMaterial getMaterialFromString(String s) {
        return KnownMaterials.getKnownMaterials().filter(mat -> mat.getName().equalsIgnoreCase(s)).findAny().orElse(null);
    }
    
    public static ItemStack createPickaxe(KnownMaterial handle, KnownMaterial pickaxeHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_PICKAXE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", handle.getToolHandleColor());
        tag.putInt("mt_color_1", pickaxeHead.getToolHeadColor());
        tag.putInt("materialisedtools_maxdurability", MathHelper.floor(pickaxeHead.getPickaxeHeadDurability() * handle.getHandleDurabilityMultiplier()));
        tag.putInt("materialisedtools_mininglevel", MathHelper.ceil((handle.getMiningLevel() + pickaxeHead.getMiningLevel()) / 2f));
        tag.putFloat("materialisedtools_breakingspeed", pickaxeHead.getPickaxeHeadSpeed() * handle.getHandleBreakingSpeedMultiplier());
        tag.putBoolean("mt_done_tool", true);
        if (handle.isBright())
            tag.putBoolean("mt_handle_bright", true);
        if (pickaxeHead.isBright())
            tag.putBoolean("mt_pickaxe_head_bright", true);
        tag.putString("mt_handle_material", handle.getName());
        tag.putString("mt_pickaxe_head_material", pickaxeHead.getName());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createAxe(KnownMaterial handle, KnownMaterial axeHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_AXE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", handle.getToolHandleColor());
        tag.putInt("mt_color_1", axeHead.getToolHeadColor());
        tag.putInt("materialisedtools_maxdurability", MathHelper.floor(axeHead.getPickaxeHeadDurability() * handle.getHandleDurabilityMultiplier()));
        tag.putInt("materialisedtools_mininglevel", MathHelper.ceil((handle.getMiningLevel() + axeHead.getMiningLevel()) / 2f));
        tag.putFloat("materialisedtools_breakingspeed", axeHead.getPickaxeHeadSpeed() * handle.getHandleBreakingSpeedMultiplier());
        tag.putBoolean("mt_done_tool", true);
        if (handle.isBright())
            tag.putBoolean("mt_handle_bright", true);
        if (axeHead.isBright())
            tag.putBoolean("mt_axe_head_bright", true);
        tag.putString("mt_handle_material", handle.getName());
        tag.putString("mt_axe_head_material", axeHead.getName());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createShovel(KnownMaterial handle, KnownMaterial shovelHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_SHOVEL);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_color_0", handle.getToolHandleColor());
        tag.putInt("mt_color_1", shovelHead.getToolHeadColor());
        tag.putInt("materialisedtools_maxdurability", MathHelper.floor(shovelHead.getPickaxeHeadDurability() * handle.getHandleDurabilityMultiplier()));
        tag.putInt("materialisedtools_mininglevel", MathHelper.ceil((handle.getMiningLevel() + shovelHead.getMiningLevel()) / 2f));
        tag.putFloat("materialisedtools_breakingspeed", shovelHead.getPickaxeHeadSpeed() * handle.getHandleBreakingSpeedMultiplier());
        tag.putBoolean("mt_done_tool", true);
        if (handle.isBright())
            tag.putBoolean("mt_handle_bright", true);
        if (shovelHead.isBright())
            tag.putBoolean("mt_shovel_head_bright", true);
        tag.putString("mt_handle_material", handle.getName());
        tag.putString("mt_shovel_head_material", shovelHead.getName());
        stack.setTag(tag);
        return stack;
    }
    
}

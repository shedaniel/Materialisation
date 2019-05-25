package me.shedaniel.materialisedtools.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class MaterialisedToolUtils {
    
    // Wooden Handle: 33529892
    
    public static final NumberFormat TWO_DECIMAL_FORMATTER = new DecimalFormat("#.##");
    public static final ToolMaterial DUMMY_MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 1;
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
    
    public static int getColor(ItemStack stack, int layer) {
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
    
}

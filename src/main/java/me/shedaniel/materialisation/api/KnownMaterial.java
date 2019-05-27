package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Set;

public interface KnownMaterial extends KnownMaterials.RepairAmountGetter {
    
    int getToolHandleColor();
    
    int getPickaxeHeadColor();
    
    String getMaterialTranslateKey();
    
    Set<Ingredient> getIngredients();
    
    String getName();
    
    boolean isBright();
    
    float getHandleDurabilityMultiplier();
    
    float getHandleBreakingSpeedMultiplier();
    
    int getPickaxeHeadDurability();
    
    float getPickaxeHeadSpeed();
    
    int getMiningLevel();
    
    int getFullAmount();
    
    float getRepairMultiplier(ItemStack stack);
    
    public static interface AmountGetter {
        float getFrom(Ingredient ingredient);
    }
    
}

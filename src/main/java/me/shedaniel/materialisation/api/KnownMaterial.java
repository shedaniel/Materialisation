package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Set;

public interface KnownMaterial extends KnownMaterials.RepairAmountGetter {
    
    int getToolColor();
    
    String getMaterialTranslateKey();
    
    Set<BetterIngredient> getIngredients();
    
    default int getEnchantability() {
        return 0;
    }
    
    @Deprecated
    Map<BetterIngredient, Float> getIngredientMap();
    
    String getName();
    
    boolean isBright();
    
    float getDurabilityMultiplier();
    
    float getBreakingSpeedMultiplier();
    
    float getAttackDamage();
    
    int getToolDurability();
    
    float getToolSpeed();
    
    int getMiningLevel();
    
    int getFullAmount();
    
    float getRepairMultiplier(ItemStack stack);
    
    public static interface AmountGetter {
        float getFrom(BetterIngredient ingredient);
    }
    
}

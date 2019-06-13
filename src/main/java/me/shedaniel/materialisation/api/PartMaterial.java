package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Set;

public interface PartMaterial extends RepairAmountGetter {
    
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
    
    double getDurabilityMultiplier();
    
    double getBreakingSpeedMultiplier();
    
    double getAttackDamage();
    
    int getToolDurability();
    
    double getToolSpeed();
    
    int getMiningLevel();
    
    int getFullAmount();
    
    float getRepairMultiplier(ItemStack stack);
    
    public static interface AmountGetter {
        float getFrom(BetterIngredient ingredient);
    }
    
}

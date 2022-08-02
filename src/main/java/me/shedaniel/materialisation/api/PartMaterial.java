package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PartMaterial extends RepairAmountGetter {
    
    int getToolColor();
    
    String getMaterialTranslateKey();
    
    Set<BetterIngredient> getIngredients();
    
    default int getEnchantability() {
        return 0;
    }
    
    Map<BetterIngredient, Float> getIngredientMap();
    
    @Deprecated
    default String getName() {
        return getIdentifier().toString();
    }
    
    Identifier getIdentifier();
    
    Map<ToolType, Identifier> getTexturedHeadIdentifiers();
    Map<ToolType, Identifier> getTexturedHandleIdentifiers();
    
    Optional<Identifier> getTexturedHeadIdentifier(ToolType toolType);
    
    Optional<Identifier> getTexturedHandleIdentifier(ToolType toolType);
    
    boolean isBright();
    
    double getDurabilityMultiplier();
    
    double getBreakingSpeedMultiplier();
    
    double getAttackDamage();
    
    int getToolDurability();
    
    double getToolSpeed();
    
    int getMiningLevel();
    
    int getFullAmount();
    
    float getRepairMultiplier(ItemStack stack);
    
    interface AmountGetter {
        float getFrom(BetterIngredient ingredient);
    }
    
}

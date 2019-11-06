package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public interface PartMaterial extends RepairAmountGetter {
    int getModifierSlotsCount();

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

    boolean isBright();

    double getDurabilityMultiplier();

    double getMiningSpeedMultiplier();

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

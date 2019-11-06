package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public class Material implements PartMaterial {
    @Override
    public int getModifierSlotsCount() {
        return 0;
    }

    @Override
    public int getToolColor() {
        return 0;
    }

    @Override
    public String getMaterialTranslateKey() {
        return null;
    }

    @Override
    public Set<BetterIngredient> getIngredients() {
        return null;
    }

    @Override
    public Map<BetterIngredient, Float> getIngredientMap() {
        return null;
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public boolean isBright() {
        return false;
    }

    @Override
    public double getDurabilityMultiplier() {
        return 0;
    }

    @Override
    public double getMiningSpeedMultiplier() {
        return 0;
    }

    @Override
    public double getAttackDamage() {
        return 0;
    }

    @Override
    public int getToolDurability() {
        return 0;
    }

    @Override
    public double getToolSpeed() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 0;
    }

    @Override
    public int getFullAmount() {
        return 0;
    }

    @Override
    public float getRepairMultiplier(ItemStack stack) {
        return 0;
    }

    @Override
    public int getRepairAmount(ItemStack stack) {
        return 0;
    }
}

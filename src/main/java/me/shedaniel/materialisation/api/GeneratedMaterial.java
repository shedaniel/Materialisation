package me.shedaniel.materialisation.api;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GeneratedMaterial implements PartMaterial {
    
    private int toolColor = -1, toolDurability = 1, miningLevel = 0, enchantability = 0;
    private float durabilityMultiplier = 1f, breakingSpeedMultiplier = 1f, toolSpeed = -1f, attackDamage = 0f;
    private String name;
    private boolean bright = false;
    private Map<BetterIngredient, Float> amountMultiplierMap = Maps.newHashMap();
    private AmountGetter amountGetter = ingredient -> {
        Optional<Map.Entry<BetterIngredient, Float>> any = amountMultiplierMap.entrySet().stream().filter(entry -> entry.getKey().equals(ingredient)).findAny();
        return any.map(Map.Entry::getValue).orElse(-1f);
    };
    private int fullAmount = -1;
    
    GeneratedMaterial(String name) {
        this.name = name;
    }
    
    @Override
    public double getAttackDamage() {
        return attackDamage;
    }
    
    public GeneratedMaterial setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }
    
    @Override
    public int getEnchantability() {
        return enchantability;
    }
    
    public GeneratedMaterial setEnchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }
    
    public GeneratedMaterial addIngredient(BetterIngredient ingredient, float multiplier) {
        amountMultiplierMap.put(ingredient, multiplier);
        return this;
    }
    
    @Override
    public boolean isBright() {
        return bright;
    }
    
    public GeneratedMaterial setBright(boolean bright) {
        this.bright = bright;
        return this;
    }
    
    @Override
    public double getToolSpeed() {
        return toolSpeed;
    }
    
    public GeneratedMaterial setToolSpeed(float toolSpeed) {
        this.toolSpeed = toolSpeed;
        return this;
    }
    
    @Override
    public int getMiningLevel() {
        return miningLevel;
    }
    
    public GeneratedMaterial setMiningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
        return this;
    }
    
    @Override
    public double getBreakingSpeedMultiplier() {
        return breakingSpeedMultiplier;
    }
    
    public GeneratedMaterial setBreakingSpeedMultiplier(float breakingSpeedMultiplier) {
        this.breakingSpeedMultiplier = breakingSpeedMultiplier;
        return this;
    }
    
    @Override
    public int getToolDurability() {
        return toolDurability;
    }
    
    public GeneratedMaterial setToolDurability(int toolDurability) {
        this.toolDurability = toolDurability;
        return this;
    }
    
    @Override
    public double getDurabilityMultiplier() {
        return durabilityMultiplier;
    }
    
    public GeneratedMaterial setDurabilityMultiplier(float durabilityMultiplier) {
        this.durabilityMultiplier = durabilityMultiplier;
        return this;
    }
    
    @Override
    public int getToolColor() {
        return toolColor;
    }
    
    public GeneratedMaterial setToolColor(int toolColor) {
        this.toolColor = toolColor;
        return this;
    }
    
    @Override
    public String getMaterialTranslateKey() {
        return "material.materialisation." + name;
    }
    
    @Override
    public Set<BetterIngredient> getIngredients() {
        return amountMultiplierMap.keySet();
    }
    
    @Override
    public Map<BetterIngredient, Float> getIngredientMap() {
        return amountMultiplierMap;
    }
    
    @Override
    public int getRepairAmount(ItemStack stack) {
        BetterIngredient ingredient = null;
        for(BetterIngredient ingredient1 : getIngredients()) {
            if (ingredient1.isIncluded(stack)) {
                ingredient = ingredient1;
                break;
            }
        }
        if (ingredient != null)
            return MathHelper.ceil(fullAmount * amountGetter.getFrom(ingredient));
        else
            return -1;
    }
    
    @Override
    public int getFullAmount() {
        return fullAmount;
    }
    
    public GeneratedMaterial setFullAmount(int i) {
        this.fullAmount = i;
        return this;
    }
    
    @Override
    public float getRepairMultiplier(ItemStack stack) {
        BetterIngredient ingredient = null;
        for(BetterIngredient ingredient1 : getIngredients()) {
            if (ingredient1.isIncluded(stack)) {
                ingredient = ingredient1;
                break;
            }
        }
        if (ingredient != null)
            return amountGetter.getFrom(ingredient);
        else
            return -1;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
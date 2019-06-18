package me.shedaniel.materialisation.api;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GeneratedMaterial implements PartMaterial {
    
    private int toolColor = -1, toolDurability = 1, miningLevel = 0, enchantability = 0;
    private float durabilityMultiplier = 1f, breakingSpeedMultiplier = 1f, toolSpeed = -1f, attackDamage = 0f;
    private Identifier name;
    private boolean bright = false;
    private Map<BetterIngredient, Float> amountMultiplierMap = Maps.newHashMap();
    private AmountGetter amountGetter = ingredient -> {
        Optional<Map.Entry<BetterIngredient, Float>> any = amountMultiplierMap.entrySet().stream().filter(entry -> entry.getKey().equals(ingredient)).findAny();
        return any.map(Map.Entry::getValue).orElse(-1f);
    };
    private int fullAmount = -1;
    
    GeneratedMaterial(Identifier name) {
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
    
    public GeneratedMaterial wAtta(float attackDamage) {
        return setAttackDamage(attackDamage);
    }
    
    @Override
    public int getEnchantability() {
        return enchantability;
    }
    
    public GeneratedMaterial setEnchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }
    
    public GeneratedMaterial wEnch(int enchantability) {
        return setEnchantability(enchantability);
    }
    
    public GeneratedMaterial addIngredient(BetterIngredient ingredient, float multiplier) {
        amountMultiplierMap.put(ingredient, multiplier);
        return this;
    }
    
    public GeneratedMaterial aIngr(BetterIngredient ingredient, float multiplier) {
        return addIngredient(ingredient, multiplier);
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
    
    public GeneratedMaterial wSpeed(float toolSpeed) {
        return setToolSpeed(toolSpeed);
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
    
    public GeneratedMaterial wSpeedMulti(float breakingSpeedMultiplier) {
        return setBreakingSpeedMultiplier(breakingSpeedMultiplier);
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
    
    public GeneratedMaterial wDuraMulti(float durabilityMultiplier) {
        return setDurabilityMultiplier(durabilityMultiplier);
    }
    
    @Override
    public int getToolColor() {
        return toolColor;
    }
    
    public GeneratedMaterial setToolColor(int toolColor) {
        this.toolColor = toolColor;
        return this;
    }
    
    public GeneratedMaterial wColor(int toolColor) {
        return setToolColor(toolColor);
    }
    
    @Override
    public String getMaterialTranslateKey() {
        return "material.materialisation." + name.toString().replace(':', '.');
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
    
    public GeneratedMaterial wFull(int i) {
        return setFullAmount(i);
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
    public Identifier getIdentifier() {
        return name;
    }
}
package me.shedaniel.materialisation.api;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class KnownMaterials extends ArrayList<KnownMaterial> {
    
    public static final KnownMaterial WOOD;
    public static final KnownMaterial STONE;
    public static final KnownMaterial IRON;
    public static final KnownMaterial GOLD;
    private static final KnownMaterials MATERIALS = new KnownMaterials();
    
    static {
        Color white = new Color(255, 255, 255);
        Color gold = new Color(255, 239, 61);
        WOOD = registerMaterial(getNewMaterial("wood").addIngredient(Ingredient.fromTag(ItemTags.PLANKS), 1).addIngredient(Ingredient.ofItems(Items.STICK), .5f).addIngredient(Ingredient.fromTag(ItemTags.LOGS), 4).setFullAmount(100).setPickaxeHeadSpeed(2f).setHandleDurabilityMultiplier(1.1f).setToolHandleColor(33529892).setToolHeadColor(33530399).setHeadDurability(59));
        STONE = registerMaterial(getNewMaterial("stone").addIngredient(Ingredient.ofItems(Items.COBBLESTONE), 1).setAttackDamage(1).setFullAmount(100).setPickaxeHeadSpeed(4f).setHandleDurabilityMultiplier(0.4f).setHandleBreakingSpeedMultiplier(0.9f).setToolHandleColor(27962026).setToolHeadColor(-2960686).setHeadDurability(131).setMiningLevel(1));
        IRON = registerMaterial(getNewMaterial("iron").addIngredient(Ingredient.ofItems(Items.IRON_INGOT), 2).setAttackDamage(2).addIngredient(Ingredient.ofItems(Items.IRON_BLOCK), 18).setFullAmount(100).setBright(true).setPickaxeHeadSpeed(6f).setHandleDurabilityMultiplier(0.9f).setHandleBreakingSpeedMultiplier(1f).setToolHandleColor(white.getRGB()).setToolHeadColor(white.getRGB()).setHeadDurability(250).setMiningLevel(2));
        GOLD = registerMaterial(getNewMaterial("gold").addIngredient(Ingredient.ofItems(Items.GOLD_INGOT), 2).addIngredient(Ingredient.ofItems(Items.GOLD_BLOCK), 18).setFullAmount(10).setBright(true).setPickaxeHeadSpeed(12f).setHandleDurabilityMultiplier(0.2f).setHandleBreakingSpeedMultiplier(0.4f).setToolHandleColor(gold.getRGB()).setToolHeadColor(gold.getRGB()).setHeadDurability(32));
    }
    
    public static Material getNewMaterial(String name) {
        return new Material(name).setMaterialTranslateKey("material.materialisation." + name);
    }
    
    public static Material registerMaterial(Material material) {
        MATERIALS.add(material);
        return material;
    }
    
    public static Stream<KnownMaterial> getKnownMaterials() {
        return MATERIALS.stream();
    }
    
    public static interface RepairAmountGetter {
        int getRepairAmount(ItemStack stack);
    }
    
    public static class Material implements KnownMaterial {
        
        private int toolHandleColor = -1, toolHeadColor = -1, headDurability = 1, miningLevel = 0;
        private float handleDurabilityMultiplier = 1f, handleBreakingSpeedMultiplier = 1f, pickaxeHeadSpeed = -1f, attackDamage = 0f;
        private String materialTranslateKey = "", name;
        private boolean bright = false;
        private Map<Ingredient, Float> amountMultiplierMap = Maps.newHashMap();
        private AmountGetter amountGetter = ingredient -> {
            Optional<Map.Entry<Ingredient, Float>> any = amountMultiplierMap.entrySet().stream().filter(entry -> entry.getKey().equals(ingredient)).findAny();
            return any.map(Map.Entry::getValue).orElse(-1f);
        };
        private int fullAmount = -1;
        
        private Material(String name) {
            this.name = name;
        }
        
        @Override
        public float getAttackDamage() {
            return attackDamage;
        }
        
        public Material setAttackDamage(float attackDamage) {
            this.attackDamage = attackDamage;
            return this;
        }
        
        public Material addIngredient(Ingredient ingredient, float multiplier) {
            amountMultiplierMap.put(ingredient, multiplier);
            return this;
        }
        
        @Override
        public boolean isBright() {
            return bright;
        }
        
        public Material setBright(boolean bright) {
            this.bright = bright;
            return this;
        }
        
        @Override
        public float getPickaxeHeadSpeed() {
            return pickaxeHeadSpeed;
        }
        
        public Material setPickaxeHeadSpeed(float pickaxeHeadSpeed) {
            this.pickaxeHeadSpeed = pickaxeHeadSpeed;
            return this;
        }
        
        @Override
        public int getMiningLevel() {
            return miningLevel;
        }
        
        public Material setMiningLevel(int miningLevel) {
            this.miningLevel = miningLevel;
            return this;
        }
        
        @Override
        public float getHandleBreakingSpeedMultiplier() {
            return handleBreakingSpeedMultiplier;
        }
        
        public Material setHandleBreakingSpeedMultiplier(float handleBreakingSpeedMultiplier) {
            this.handleBreakingSpeedMultiplier = handleBreakingSpeedMultiplier;
            return this;
        }
        
        @Override
        public int getHeadDurability() {
            return headDurability;
        }
        
        public Material setHeadDurability(int headDurability) {
            this.headDurability = headDurability;
            return this;
        }
        
        @Override
        public float getHandleDurabilityMultiplier() {
            return handleDurabilityMultiplier;
        }
        
        public Material setHandleDurabilityMultiplier(float handleDurabilityMultiplier) {
            this.handleDurabilityMultiplier = handleDurabilityMultiplier;
            return this;
        }
        
        @Override
        public int getToolHandleColor() {
            return toolHandleColor;
        }
        
        public Material setToolHandleColor(int toolHandleColor) {
            this.toolHandleColor = toolHandleColor;
            return this;
        }
        
        @Override
        public int getToolHeadColor() {
            return toolHeadColor;
        }
        
        public Material setToolHeadColor(int toolHeadColor) {
            this.toolHeadColor = toolHeadColor;
            return this;
        }
        
        @Override
        public String getMaterialTranslateKey() {
            return materialTranslateKey;
        }
        
        public Material setMaterialTranslateKey(String materialTranslateKey) {
            this.materialTranslateKey = materialTranslateKey;
            return this;
        }
        
        @Override
        public Set<Ingredient> getIngredients() {
            return amountMultiplierMap.keySet();
        }
        
        @Override
        public int getRepairAmount(ItemStack stack) {
            Ingredient ingredient = null;
            for(Ingredient ingredient1 : getIngredients()) {
                if (ingredient1.method_8093(stack)) {
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
        
        public Material setFullAmount(int i) {
            this.fullAmount = i;
            return this;
        }
        
        @Override
        public float getRepairMultiplier(ItemStack stack) {
            Ingredient ingredient = null;
            for(Ingredient ingredient1 : getIngredients()) {
                if (ingredient1.method_8093(stack)) {
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
    
}

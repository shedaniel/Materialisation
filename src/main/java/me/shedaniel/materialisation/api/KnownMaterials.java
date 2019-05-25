package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;

import java.awt.*;
import java.util.ArrayList;
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
        WOOD = registerMaterial(getNewMaterial("wood").setIngredient(Ingredient.fromTag(ItemTags.PLANKS)).setRepairAmountGetter(stack -> 100).setPickaxeHeadSpeed(2f).setHandleDurabilityMultiplier(1.1f).setToolHandleColor(33529892).setPickaxeHeadColor(33530399).setPickaxeHeadDurability(59));
        STONE = registerMaterial(getNewMaterial("stone").setIngredient(Ingredient.ofItems(Items.STONE)).setRepairAmountGetter(stack -> 100).setPickaxeHeadSpeed(4f).setHandleDurabilityMultiplier(0.4f).setHandleBreakingSpeedMultiplier(0.9f).setToolHandleColor(27962026).setPickaxeHeadColor(-2960686).setPickaxeHeadDurability(131).setMiningLevel(1));
        IRON = registerMaterial(getNewMaterial("iron").setIngredient(Ingredient.ofItems(Items.IRON_INGOT)).setRepairAmountGetter(stack -> 200).setBright(true).setPickaxeHeadSpeed(6f).setHandleDurabilityMultiplier(0.9f).setHandleBreakingSpeedMultiplier(1f).setToolHandleColor(white.getRGB()).setPickaxeHeadColor(white.getRGB()).setPickaxeHeadDurability(250).setMiningLevel(2));
        GOLD = registerMaterial(getNewMaterial("gold").setIngredient(Ingredient.ofItems(Items.GOLD_INGOT)).setRepairAmountGetter(stack -> 20).setBright(true).setPickaxeHeadSpeed(12f).setHandleDurabilityMultiplier(0.2f).setHandleBreakingSpeedMultiplier(0.4f).setToolHandleColor(gold.getRGB()).setPickaxeHeadColor(gold.getRGB()).setPickaxeHeadDurability(32));
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
        
        private int toolHandleColor = -1, pickaxeHeadColor = -1, pickaxeHeadDurability = 1, miningLevel = 0;
        private float handleDurabilityMultiplier = 1f, handleBreakingSpeedMultiplier = 1f, pickaxeHeadSpeed = -1f;
        private String materialTranslateKey = "", name;
        private Ingredient ingredient = Ingredient.EMPTY;
        private boolean bright = false;
        private RepairAmountGetter repairAmountGetter = stack -> -1;
        
        private Material(String name) {
            this.name = name;
        }
        
        public Material setRepairAmountGetter(RepairAmountGetter repairAmountGetter) {
            this.repairAmountGetter = repairAmountGetter;
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
        public int getPickaxeHeadDurability() {
            return pickaxeHeadDurability;
        }
        
        public Material setPickaxeHeadDurability(int pickaxeHeadDurability) {
            this.pickaxeHeadDurability = pickaxeHeadDurability;
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
        public int getPickaxeHeadColor() {
            return pickaxeHeadColor;
        }
        
        public Material setPickaxeHeadColor(int pickaxeHeadColor) {
            this.pickaxeHeadColor = pickaxeHeadColor;
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
        public Ingredient getIngredient() {
            return ingredient;
        }
        
        public Material setIngredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }
        
        @Override
        public int getRepairAmount(ItemStack stack) {
            return repairAmountGetter.getRepairAmount(stack);
        }
        
        @Override
        public String getName() {
            return name;
        }
    }
    
}

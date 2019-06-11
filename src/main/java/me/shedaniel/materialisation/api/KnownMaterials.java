package me.shedaniel.materialisation.api;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.MathHelper;

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
        WOOD = getNewMaterial("wood").setEnchantability(15).addIngredient(BetterIngredient.fromTag(ItemTags.PLANKS), 1).addIngredient(BetterIngredient.fromItem(Items.STICK), .5f).addIngredient(BetterIngredient.fromTag(ItemTags.LOGS), 4).setFullAmount(100).setToolSpeed(2f).setDurabilityMultiplier(1.1f).setToolColor(33530399).setToolDurability(59);
        STONE = getNewMaterial("stone").setEnchantability(5).addIngredient(BetterIngredient.fromItem(Items.COBBLESTONE), 1).setAttackDamage(1).setFullAmount(100).setToolSpeed(4f).setDurabilityMultiplier(0.4f).setBreakingSpeedMultiplier(0.9f).setToolColor(-2960686).setToolDurability(131).setMiningLevel(1);
        IRON = getNewMaterial("iron").setEnchantability(14).addIngredient(BetterIngredient.fromItem(Items.IRON_INGOT), 2).setAttackDamage(2).addIngredient(BetterIngredient.fromItem(Items.IRON_BLOCK), 18).setFullAmount(100).setBright(true).setToolSpeed(6f).setDurabilityMultiplier(0.9f).setBreakingSpeedMultiplier(1f).setToolColor(0xFFFFFFFF).setToolDurability(250).setMiningLevel(2);
        GOLD = getNewMaterial("gold").setEnchantability(22).addIngredient(BetterIngredient.fromItem(Items.GOLD_INGOT), 2).addIngredient(BetterIngredient.fromItem(Items.GOLD_BLOCK), 18).setFullAmount(10).setBright(true).setToolSpeed(12f).setDurabilityMultiplier(0.2f).setBreakingSpeedMultiplier(0.4f).setToolColor(0xffffef3d).setToolDurability(32);
    }
    
    public static Material getNewMaterial(String name) {
        return new Material(name);
    }
    
    public static KnownMaterial registerMaterial(KnownMaterial material) {
        MATERIALS.add(material);
        return material;
    }
    
    public static Stream<KnownMaterial> getKnownMaterials() {
        return MATERIALS.stream();
    }
    
    public static void clearMaterials() {
        MATERIALS.clear();
    }
    
    public static interface RepairAmountGetter {
        int getRepairAmount(ItemStack stack);
    }
    
    public static class Material implements KnownMaterial {
        
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
        
        @Override
        public int getEnchantability() {
            return enchantability;
        }
        
        public Material setEnchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }
        
        public Material addIngredient(BetterIngredient ingredient, float multiplier) {
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
        public float getToolSpeed() {
            return toolSpeed;
        }
        
        public Material setToolSpeed(float toolSpeed) {
            this.toolSpeed = toolSpeed;
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
        public float getBreakingSpeedMultiplier() {
            return breakingSpeedMultiplier;
        }
        
        public Material setBreakingSpeedMultiplier(float breakingSpeedMultiplier) {
            this.breakingSpeedMultiplier = breakingSpeedMultiplier;
            return this;
        }
        
        @Override
        public int getToolDurability() {
            return toolDurability;
        }
        
        public Material setToolDurability(int toolDurability) {
            this.toolDurability = toolDurability;
            return this;
        }
        
        @Override
        public float getDurabilityMultiplier() {
            return durabilityMultiplier;
        }
        
        public Material setDurabilityMultiplier(float durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }
        
        @Override
        public int getToolColor() {
            return toolColor;
        }
        
        public Material setToolColor(int toolColor) {
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
        
        public Material setFullAmount(int i) {
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
    
}

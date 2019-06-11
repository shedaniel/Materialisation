package me.shedaniel.materialisation.config;

import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.KnownMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MaterialisationConfig {
    
    public static class ConfigIngredients {
        public BetterIngredient ingredient;
        public float multiplier;
        
        public ConfigIngredients(BetterIngredient ingredient, float multiplier) {
            this.ingredient = ingredient;
            this.multiplier = multiplier;
        }
    }
    
    public static class ConfigMaterial implements KnownMaterial {
        public String toolColor;
        public int toolDurability;
        public int miningLevel;
        public int enchantability;
        public float durabilityMultiplier;
        public float breakingSpeedMultiplier;
        public float toolSpeed;
        public float attackDamage;
        public String name;
        public String materialTranslationKey;
        public boolean bright;
        public List<ConfigIngredients> ingredients;
        public int fullAmount;
        private transient Integer color = -1;
        private transient Map<BetterIngredient, Float> ingredientFloatMap = null;
        
        public ConfigMaterial(KnownMaterial knownMaterial) {
            this.toolColor = knownMaterial.getToolColor() + "";
            this.color = knownMaterial.getToolColor();
            this.toolDurability = knownMaterial.getToolDurability();
            this.miningLevel = knownMaterial.getMiningLevel();
            this.enchantability = knownMaterial.getEnchantability();
            this.durabilityMultiplier = knownMaterial.getDurabilityMultiplier();
            this.breakingSpeedMultiplier = knownMaterial.getBreakingSpeedMultiplier();
            this.toolSpeed = knownMaterial.getToolSpeed();
            this.attackDamage = knownMaterial.getAttackDamage();
            this.name = knownMaterial.getName();
            this.materialTranslationKey = knownMaterial.getMaterialTranslateKey();
            this.bright = knownMaterial.isBright();
            this.ingredients = ConfigHelper.fromMap(knownMaterial.getIngredientMap());
            this.fullAmount = knownMaterial.getFullAmount();
        }
        
        @Override
        public int getToolColor() {
            if (color != null)
                return color;
            if (toolColor.startsWith("#")) {
                int a = Integer.valueOf(toolColor.substring(1, 3), 16);
                int r = Integer.valueOf(toolColor.substring(3, 5), 16);
                int g = Integer.valueOf(toolColor.substring(5, 7), 16);
                int b = Integer.valueOf(toolColor.substring(7, 9), 16);
                return color = new Color(r, g, b, a).getRGB();
            }
            return color = Color.decode(toolColor).getRGB();
        }
        
        @Override
        public String getMaterialTranslateKey() {
            if (materialTranslationKey != null)
                return materialTranslationKey;
            return materialTranslationKey = "material.materialisation." + getName();
        }
        
        @Override
        public Set<BetterIngredient> getIngredients() {
            return getIngredientMap().keySet();
        }
        
        @Override
        public Map<BetterIngredient, Float> getIngredientMap() {
            if (ingredientFloatMap != null)
                return ingredientFloatMap;
            return ingredientFloatMap = ConfigHelper.fromJson(ingredients);
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public boolean isBright() {
            return bright;
        }
        
        @Override
        public float getDurabilityMultiplier() {
            return durabilityMultiplier;
        }
        
        @Override
        public float getBreakingSpeedMultiplier() {
            return breakingSpeedMultiplier;
        }
        
        @Override
        public float getAttackDamage() {
            return attackDamage;
        }
        
        @Override
        public int getToolDurability() {
            return toolDurability;
        }
        
        @Override
        public float getToolSpeed() {
            return toolSpeed;
        }
        
        @Override
        public int getMiningLevel() {
            return miningLevel;
        }
        
        @Override
        public int getFullAmount() {
            return fullAmount;
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
            if (ingredient != null) {
                BetterIngredient finalIngredient = ingredient;
                Optional<Map.Entry<BetterIngredient, Float>> any = getIngredientMap().entrySet().stream().filter(entry -> entry.getKey().equals(finalIngredient)).findAny();
                return any.map(Map.Entry::getValue).orElse(-1f);
            } else
                return -1;
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
            if (ingredient != null) {
                float multiplier = Float.MAX_VALUE / 2;
                BetterIngredient finalIngredient = ingredient;
                Optional<Map.Entry<BetterIngredient, Float>> any = getIngredientMap().entrySet().stream().filter(entry -> entry.getKey().equals(finalIngredient)).findAny();
                multiplier = any.map(Map.Entry::getValue).orElse(Float.MAX_VALUE / 2);
                return MathHelper.ceil(fullAmount * multiplier);
            } else
                return -1;
        }
    }
    
}



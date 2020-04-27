package me.shedaniel.materialisation;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.api.*;
import me.shedaniel.materialisation.config.ConfigHelper;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tool.attribute.ToolManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class MaterialisationUtils {
    
    public static final NumberFormat TWO_DECIMAL_FORMATTER = new DecimalFormat("#.##");
    public static final ToolMaterial DUMMY_MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 0;
        }
        
        @Override
        public float getMiningSpeed() {
            return 1;
        }
        
        @Override
        public float getAttackDamage() {
            return 0;
        }
        
        @Override
        public int getMiningLevel() {
            return 0;
        }
        
        @Override
        public int getEnchantability() {
            return 0;
        }
        
        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };
    
    public static Formatting getColoring(double f) {
        if (f == 1d)
            return Formatting.GOLD;
        else if (f > 1d)
            return Formatting.GREEN;
        return Formatting.RED;
    }
    
    public static Formatting getColoringPercentage(double f) {
        if (f >= 70d)
            return Formatting.GREEN;
        else if (f >= 40d)
            return Formatting.GOLD;
        return Formatting.RED;
    }
    
    public static int getToolEnchantability(ItemStack stack) {
        return getToolEnchantability(stack, true);
    }
    
    public static int getToolEnchantability(ItemStack stack, boolean modifiers) {
        int enchantability = 0;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("mt_0_material") && tag.contains("mt_1_material")) {
            PartMaterial handle = MaterialisationUtils.getMaterialFromString(tag.getString("mt_0_material"));
            PartMaterial head = MaterialisationUtils.getMaterialFromString(tag.getString("mt_1_material"));
            enchantability = ((handle == null ? 0 : handle.getEnchantability()) + (head == null ? 0 : head.getEnchantability())) / 2;
        }
        if (!modifiers) return enchantability;
        return enchantability + getToolExtraEnchantability(stack, enchantability);
    }
    
    @Deprecated
    private static int getToolExtraEnchantability(ItemStack stack, int base) {
        int extraEnchantability = 0;
        
        for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
            if (entry.getValue() > 0)
                extraEnchantability += entry.getKey().getExtraEnchantability(stack, entry.getValue());
        }
        
        // We are not going to allow enchantability higher than 100
        return Math.min(extraEnchantability, 100 - base);
    }
    
    public static float getToolBreakingSpeed(ItemStack stack) {
        return getToolBreakingSpeed(stack, true);
    }
    
    public static float getToolBreakingSpeed(ItemStack stack, boolean modifiers) {
        float speed = getBaseToolBreakingSpeed(stack);
        if (speed <= 0) return 0;
        if (!modifiers) return speed;
        return getToolAfterExtraBreakingSpeed(stack, speed);
    }
    
    @Deprecated
    private static float getToolAfterExtraBreakingSpeed(ItemStack stack, float base) {
        float speed = base;
        
        for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
            if (entry.getValue() > 0)
                speed *= entry.getKey().getMiningSpeedMultiplier(stack, entry.getValue());
        }
        for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
            if (entry.getValue() > 0)
                speed += entry.getKey().getExtraMiningSpeed(stack, entry.getValue());
        }
        
        // We are not going to allow speed higher than 50
        return Math.min(speed, 50);
    }
    
    public static float getBaseToolBreakingSpeed(ItemStack stack) {
        float speed = 0;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("mt_0_material") && tag.contains("mt_1_material"))
                speed = getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        }
        if (stack.getItem() == Materialisation.MATERIALISED_HAMMER) speed /= 6f;
        if (stack.getItem() == Materialisation.MATERIALISED_MEGAAXE) speed /= 6.5f;
        return speed;
    }
    
    public static int getToolMiningLevel(ItemStack stack) {
        return getToolMiningLevel(stack, true);
    }
    
    public static int getToolMiningLevel(ItemStack stack, boolean modifiers) {
        int miningLevel = 0;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("mt_1_material"))
                miningLevel = getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getMiningLevel).orElse(0);
        }
        if (miningLevel <= 0) miningLevel = 0;
        if (!modifiers) return miningLevel;
        return miningLevel + getToolExtraMiningLevel(stack, miningLevel);
    }
    
    @Deprecated
    private static int getToolExtraMiningLevel(ItemStack stack, int base) {
        int extraMiningLevel = 0;
        
        for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
            if (entry.getValue() > 0)
                extraMiningLevel += entry.getKey().getExtraMiningLevel(stack, entry.getValue());
        }
        
        // We are not going to allow level higher than 10
        return Math.min(extraMiningLevel, 10 - base);
    }
    
    public static int getToolDurability(ItemStack stack) {
        if (!stack.hasTag())
            return 1;
        CompoundTag tag = stack.getTag();
        if (tag.contains("mt_durability"))
            return Math.min(tag.getInt("mt_durability"), getToolMaxDurability(stack));
        return getToolMaxDurability(stack);
    }
    
    public static int getToolMaxDurability(ItemStack stack) {
        return getToolMaxDurability(stack, true);
    }
    
    public static int getToolMaxDurability(ItemStack stack, boolean modifiers) {
        if (!stack.hasTag())
            return 1;
        CompoundTag tag = stack.getTag();
        if (tag.contains("mt_0_material") && tag.contains("mt_1_material"))
            if (tag.contains("mt_0_material") && tag.contains("mt_1_material")) {
                double multiplier = getMatFromString(tag.getString("mt_0_material"))
                        .map(PartMaterial::getDurabilityMultiplier).orElse(0d);
                int durability = getMatFromString(tag.getString("mt_1_material"))
                        .map(PartMaterial::getToolDurability).orElse(0);
                durability = MathHelper.floor(multiplier * durability);
                
                if (modifiers) {
                    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
                        durability *= entry.getKey().getDurabilityMultiplier(stack, entry.getValue());
                    }
                    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
                        durability -= entry.getKey().getDurabilityCost(stack, entry.getValue());
                    }
                }
                
                return durability;
            }
        return 1;
    }
    
    public static Map<Modifier, Integer> getToolModifiers(ItemStack stack) {
        if (!stack.hasTag())
            return Collections.emptyMap();
        Map<Modifier, Integer> map = new HashMap<>();
        CompoundTag tag = stack.getTag();
        if (tag.contains("modifiers")) {
            CompoundTag modifiersTag = tag.getCompound("modifiers");
            if (!modifiersTag.isEmpty()) {
                for (String key : modifiersTag.getKeys()) {
                    Identifier identifier = new Identifier(key);
                    Optional<Modifier> modifiersOrEmpty = Materialisation.MODIFIERS.getOrEmpty(identifier);
                    if (modifiersOrEmpty.isPresent()) {
                        int level = modifiersTag.getInt(key);
                        if (level > 0)
                            map.put(modifiersOrEmpty.get(), level);
                    }
                }
            }
        }
        return map;
    }
    
    public static float getToolAttackDamage(ItemStack stack) {
        return getToolAttackDamage(stack, true);
    }
    
    public static float getToolAttackDamage(ItemStack stack, boolean modifiers) {
        if (!stack.hasTag())
            return 0;
        CompoundTag tag = stack.getTag();
        PartMaterial material = tag.contains("mt_1_material") ? getMatFromString(tag.getString("mt_1_material")).orElse(null) : null;
        float attackDamage = material == null ? 0 : (float) material.getAttackDamage() + MaterialisedMiningTool.getExtraDamageFromItem(stack.getItem());
        if (!modifiers) return attackDamage;
        return getToolAfterExtraAttackDamage(stack, attackDamage);
    }
    
    @Deprecated
    private static float getToolAfterExtraAttackDamage(ItemStack stack, float base) {
        float attackDamage = base;
        
        for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
            if (entry.getValue() > 0)
                attackDamage *= entry.getKey().getAttackDamageMultiplier(stack, entry.getValue());
        }
        for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
            if (entry.getValue() > 0)
                attackDamage += entry.getKey().getExtraAttackDamage(stack, entry.getValue());
        }
        
        // We are not going to allow attack damage higher than 100
        return Math.min(attackDamage, 100);
    }
    
    public static int getItemLayerColor(ItemStack stack, int layer) {
        if (!stack.hasTag())
            return -1;
        CompoundTag tag = stack.getTag();
        if (layer == 0)
            if (tag.contains("mt_0_material"))
                return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getToolColor).orElse(-1);
        if (layer == 1)
            if (tag.contains("mt_1_material"))
                return getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolColor).orElse(-1);
        return -1;
    }
    
    public static void setToolDurability(ItemStack stack, int i) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("mt_durability", Math.min(i, getToolMaxDurability(stack)));
        stack.setTag(tag);
    }
    
    public static boolean applyDamage(ItemStack stack, int int_1, Random random_1) {
        if (getToolDurability(stack) <= 0) {
            return false;
        } else {
            int int_2;
            if (int_1 > 0) {
                int_2 = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
                int int_3 = 0;
                for (int int_4 = 0; int_2 > 0 && int_4 < int_1; ++int_4)
                    if (UnbreakingEnchantment.shouldPreventDamage(stack, int_2, random_1))
                        ++int_3;
                int_1 -= int_3;
                if (int_1 <= 0)
                    return false;
            }
            int_2 = getToolDurability(stack) - int_1;
            setToolDurability(stack, int_2);
            return int_2 < getToolDurability(stack);
        }
    }
    
    public static ItemStack createToolHandle(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.HANDLE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createAxeHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.AXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createPickaxeHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.PICKAXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createShovelHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.SHOVEL_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createSwordBlade(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.SWORD_BLADE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static PartMaterial getMaterialFromPart(ItemStack stack) {
        if (stack.getOrCreateTag().contains("mt_0_material"))
            return getMaterialFromString(stack.getOrCreateTag().getString("mt_0_material"));
        else
            return null;
    }
    
    public static PartMaterial getMaterialFromString(String s) {
        return getMatFromString(s).orElse(null);
    }
    
    @SuppressWarnings("deprecation")
    public static Optional<PartMaterial> getMatFromString(String s) {
        {
            PartMaterial cache = ConfigHelper.MATERIAL_CACHE.get(s);
            if (cache != null)
                return Optional.of(cache);
        }
        String ss = s.contains(":") ? s : "minecraft:" + s;
        for (Map.Entry<String, MaterialsPack> entry : PartMaterials.getMaterialsMap().entrySet()) {
            for (Map.Entry<String, PartMaterial> materialEntry : entry.getValue().getKnownMaterialMap().entrySet()) {
                PartMaterial value = materialEntry.getValue();
                if (value.getIdentifier().toString().equals(ss))
                    return Optional.of(cacheMaterialFromString(s, value));
            }
        }
        return Optional.empty();
    }
    
    private static PartMaterial cacheMaterialFromString(String s, PartMaterial material) {
        ConfigHelper.MATERIAL_CACHE.put(s, material);
        return material;
    }
    
    public static boolean isHandleBright(ItemStack itemStack) {
        return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_0_material")).map(PartMaterial::isBright).orElse(false);
    }
    
    public static boolean isHeadBright(ItemStack itemStack) {
        return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::isBright).orElse(false);
    }
    
    public static ItemStack createPickaxe(PartMaterial handle, PartMaterial pickaxeHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_PICKAXE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("mt_done_tool", true);
        tag.putString("mt_0_material", handle.getIdentifier().toString());
        tag.putString("mt_1_material", pickaxeHead.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createAxe(PartMaterial handle, PartMaterial axeHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_AXE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("mt_done_tool", true);
        tag.putString("mt_0_material", handle.getIdentifier().toString());
        tag.putString("mt_1_material", axeHead.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createShovel(PartMaterial handle, PartMaterial shovelHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_SHOVEL);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("mt_done_tool", true);
        tag.putString("mt_0_material", handle.getIdentifier().toString());
        tag.putString("mt_1_material", shovelHead.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createSword(PartMaterial handle, PartMaterial swordBlade) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_SWORD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("mt_done_tool", true);
        tag.putString("mt_0_material", handle.getIdentifier().toString());
        tag.putString("mt_1_material", swordBlade.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createHammer(PartMaterial handle, PartMaterial hammerHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_HAMMER);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("mt_done_tool", true);
        tag.putString("mt_0_material", handle.getIdentifier().toString());
        tag.putString("mt_1_material", hammerHead.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createMegaAxe(PartMaterial handle, PartMaterial megaAxeHead) {
        ItemStack stack = new ItemStack(Materialisation.MATERIALISED_MEGAAXE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("mt_done_tool", true);
        tag.putString("mt_0_material", handle.getIdentifier().toString());
        tag.putString("mt_1_material", megaAxeHead.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createMegaAxeHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.MEGAAXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static TriState mt_handleIsEffectiveOn(ItemStack stack, BlockState state) {
        ToolManager.Entry entry = ToolManager.entry(state.getBlock());
        Tag<Item>[] tags = Materialisation.getReflectionField(entry, Tag[].class, 0).orElse(new Tag[0]);
        int[] tagLevels = Materialisation.getReflectionField(entry, int[].class, 1).orElse(new int[tags.length]);
        Item item = stack.getItem();
        for (int i = 0; i < tags.length; ++i)
            if (tags[i].contains(item))
                return TriState.of(MaterialisationUtils.getToolMiningLevel(stack) >= tagLevels[i]);
        return Materialisation.getReflectionField(entry, TriState.class, 2).orElse(TriState.DEFAULT);
    }
    
    public static ItemStack createHammerHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.HAMMER_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        stack.setTag(tag);
        return stack;
    }
    
    public static UUID getItemModifierDamage() {
        return ColoredItem.getItemModifierDamage();
    }
    
    public static UUID getItemModifierSwingSpeed() {
        return ColoredItem.getItemModifierSwingSpeed();
    }
    
    public static void appendToolTooltip(ItemStack stack, MaterialisedMiningTool tool, World world_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        int toolDurability = getToolDurability(stack);
        int baseMaxDurability = getToolMaxDurability(stack, false);
        int maxDurability = getToolMaxDurability(stack);
        if (baseMaxDurability > maxDurability)
            list_1.add(new TranslatableText("text.materialisation.max_durability_less", maxDurability, baseMaxDurability - maxDurability));
        else if (baseMaxDurability < maxDurability)
            list_1.add(new TranslatableText("text.materialisation.max_durability_more", maxDurability, maxDurability - baseMaxDurability));
        else list_1.add(new TranslatableText("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0) {
            float percentage = toolDurability / (float) maxDurability * 100;
            Formatting coloringPercentage = getColoringPercentage(percentage);
            list_1.add(new TranslatableText("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + TWO_DECIMAL_FORMATTER.format(percentage) + Formatting.WHITE.toString()));
        } else
            list_1.add(new TranslatableText("text.materialisation.broken"));
        if (ImmutableList.copyOf(ToolType.MINING_TOOLS).contains(tool.getToolType())) {
            {
                float breakingSpeed = getToolBreakingSpeed(stack, false);
                float extra = getToolBreakingSpeed(stack) - breakingSpeed;
                if (extra > 0)
                    list_1.add(new TranslatableText("text.materialisation.breaking_speed_extra", TWO_DECIMAL_FORMATTER.format(breakingSpeed + extra), TWO_DECIMAL_FORMATTER.format(extra)));
                else if (extra < 0)
                    list_1.add(new TranslatableText("text.materialisation.breaking_speed_less", TWO_DECIMAL_FORMATTER.format(breakingSpeed + extra), TWO_DECIMAL_FORMATTER.format(-extra)));
                else
                    list_1.add(new TranslatableText("text.materialisation.breaking_speed", TWO_DECIMAL_FORMATTER.format(breakingSpeed)));
            }
            {
                int miningLevel = getToolMiningLevel(stack, false);
                int extra = getToolMiningLevel(stack) - miningLevel;
                if (extra > 0)
                    list_1.add(new TranslatableText("text.materialisation.mining_level_extra", miningLevel + extra, extra));
                else if (extra < 0)
                    list_1.add(new TranslatableText("text.materialisation.mining_level_less", miningLevel + extra, -extra));
                else
                    list_1.add(new TranslatableText("text.materialisation.mining_level", miningLevel));
            }
        }
        {
            int enchantability = getToolEnchantability(stack, false);
            int extra = getToolEnchantability(stack) - enchantability;
            if (extra > 0)
                list_1.add(new TranslatableText("text.materialisation.enchantability_extra", enchantability + extra, extra));
            else if (extra < 0)
                list_1.add(new TranslatableText("text.materialisation.enchantability_less", enchantability + extra, -extra));
            else
                list_1.add(new TranslatableText("text.materialisation.enchantability", enchantability));
        }
        {
            float attackDamage = getToolAttackDamage(stack, false);
            float extra = getToolAttackDamage(stack) - attackDamage;
            if (extra > 0)
                list_1.add(new TranslatableText("text.materialisation.attack_damage_extra", TWO_DECIMAL_FORMATTER.format(attackDamage + extra), TWO_DECIMAL_FORMATTER.format(extra)));
            else if (extra < 0)
                list_1.add(new TranslatableText("text.materialisation.attack_damage_less", TWO_DECIMAL_FORMATTER.format(attackDamage + extra), TWO_DECIMAL_FORMATTER.format(-extra)));
            else
                list_1.add(new TranslatableText("text.materialisation.attack_damage", TWO_DECIMAL_FORMATTER.format(attackDamage)));
        }
        Map<Modifier, Integer> modifiers = getToolModifiers(stack);
        if (!modifiers.isEmpty()) {
            list_1.add(new LiteralText(" "));
            for (Map.Entry<Modifier, Integer> entry : modifiers.entrySet()) {
                Identifier id = Materialisation.MODIFIERS.getId(entry.getKey());
                if (entry.getValue() != 1)
                    list_1.add(new TranslatableText("modifier." + id.getNamespace() + "." + id.getPath()).append(" " + entry.getValue()));
                else
                    list_1.add(new TranslatableText("modifier." + id.getNamespace() + "." + id.getPath()));
            }
        }
    }
    
}

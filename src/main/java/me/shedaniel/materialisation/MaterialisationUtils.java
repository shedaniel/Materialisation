package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.config.ConfigHelper;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.materialisation.modifiers.Modifiers;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tools.ToolManager;
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

    public static float getToolBreakingSpeed(ItemStack stack) {
        return getToolBreakingSpeed(stack, true);
    }

    public static float getToolBreakingSpeed(ItemStack stack, boolean modifiers) {
        float speed = getBaseToolBreakingSpeed(stack);
        if (speed <= 0) return 0;
        if (!modifiers) return speed;
        return speed + getToolExtraBreakingSpeed(stack, speed);
    }

    public static float getToolExtraBreakingSpeed(ItemStack stack, float base) {
        float extraSpeedMultiplier = 0;

        extraSpeedMultiplier += ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, Modifiers.HASTE) * .5;

        return Math.min(base * extraSpeedMultiplier, 50 - base);
    }

    private static float getBaseToolBreakingSpeed(ItemStack stack) {
        if (!stack.hasTag())
            return 0;
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material") && stack.getItem() == Materialisation.MATERIALISED_HAMMER)
            return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue() / 6f;
        if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material") && stack.getItem() == Materialisation.MATERIALISED_MEGAAXE)
            return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue() / 6.5f;
        if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material"))
            return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        return 0f;
    }

    public static int getToolMiningLevel(ItemStack stack) {
        if (!stack.hasTag())
            return 0;
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("mt_1_material"))
            return getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getMiningLevel).orElse(0);
        return 0;
    }

    public static int getToolDurability(ItemStack stack) {
        if (!stack.hasTag())
            return 1;
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("mt_durability"))
            return Math.min(stack.getTag().getInt("mt_durability"), getToolMaxDurability(stack));
        return getToolMaxDurability(stack);
    }

    public static int getToolMaxDurability(ItemStack stack) {
        return getToolMaxDurability(stack, true);
    }

    public static int getToolMaxDurability(ItemStack stack, boolean modifiers) {
        if (!stack.hasTag())
            return 1;
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material"))
            if (tag.containsKey("mt_maxdurability") || (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material"))) {
                int max = tag.containsKey("mt_maxdurability") ? tag.getInt("mt_maxdurability") :
                        MathHelper.floor(getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolDurability).orElse(0));
                if (modifiers)
                    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
                        max = entry.getKey().getMaximumDurability(stack, (MaterialisedMiningTool) stack.getItem(), max);
                    }
                return max;
            }
        return 1;
    }

    public static Map<Modifier, Integer> getToolModifiers(ItemStack stack) {
        if (!stack.hasTag())
            return Collections.emptyMap();
        Map<Modifier, Integer> map = new HashMap<>();
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("modifiers")) {
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
        if (!stack.hasTag())
            return 0;
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("mt_damage"))
            return tag.getFloat("mt_damage");
        PartMaterial material = tag.containsKey("mt_1_material") ? getMatFromString(tag.getString("mt_1_material")).get() : null;
        return material == null ? 0 : (float) material.getAttackDamage() + ColoredItem.getExtraDamage(stack.getItem());
    }

    public static int getItemLayerColor(ItemStack stack, int layer) {
        if (!stack.hasTag())
            return -1;
        CompoundTag tag = stack.getTag();
        if (layer == 0)
            if (tag.containsKey("mt_0_material"))
                return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getToolColor).orElse(-1);
        if (layer == 1)
            if (tag.containsKey("mt_1_material"))
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
        if (stack.getOrCreateTag().containsKey("mt_0_material"))
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
        ToolManager.Entry entry = (ToolManager.Entry) ToolManager.entry(state.getBlock());
        Tag<Item>[] tags = Materialisation.getReflectionField(entry, Tag[].class, 0).orElse(new Tag[0]);
        int[] tagLevels = Materialisation.getReflectionField(entry, int[].class, 1).orElse(new int[tags.length]);
        Item item = stack.getItem();
        for (int i = 0; i < tags.length; ++i)
            if (item.isIn(tags[i]))
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
            list_1.add(new TranslatableText("text.materialisation.max_durability_less", maxDurability, TWO_DECIMAL_FORMATTER.format(baseMaxDurability - maxDurability)));
        else if (baseMaxDurability < maxDurability)
            list_1.add(new TranslatableText("text.materialisation.max_durability_more", maxDurability, TWO_DECIMAL_FORMATTER.format(maxDurability - baseMaxDurability)));
        else list_1.add(new TranslatableText("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0) {
            float percentage = toolDurability / (float) maxDurability * 100;
            Formatting coloringPercentage = getColoringPercentage(percentage);
            list_1.add(new TranslatableText("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + TWO_DECIMAL_FORMATTER.format(percentage) + Formatting.WHITE.toString()));
        } else
            list_1.add(new TranslatableText("text.materialisation.broken"));
        switch (tool.getToolType()) {
            case AXE:
            case HAMMER:
            case MEGA_AXE:
            case PICKAXE:
            case SHOVEL: {
                float breakingSpeed = getToolBreakingSpeed(stack, false);
                float extra = getToolExtraBreakingSpeed(stack, breakingSpeed);
                if (extra > 0)
                    list_1.add(new TranslatableText("text.materialisation.breaking_speed_extra", TWO_DECIMAL_FORMATTER.format(breakingSpeed + extra), TWO_DECIMAL_FORMATTER.format(extra)));
                else if (extra < 0)
                    list_1.add(new TranslatableText("text.materialisation.breaking_speed_less", TWO_DECIMAL_FORMATTER.format(breakingSpeed + extra), TWO_DECIMAL_FORMATTER.format(-extra)));
                else
                    list_1.add(new TranslatableText("text.materialisation.breaking_speed", TWO_DECIMAL_FORMATTER.format(breakingSpeed)));
                list_1.add(new TranslatableText("text.materialisation.mining_level", getToolMiningLevel(stack)));
                break;
            }
        }
        Map<Modifier, Integer> modifiers = getToolModifiers(stack);
        if (!modifiers.isEmpty()) {
            list_1.add(new LiteralText(" "));
            modifiers.forEach((modifier, level) -> {
                Identifier id = Materialisation.MODIFIERS.getId(modifier);
                if (level != 1)
                    list_1.add(new TranslatableText("modifier." + id.getNamespace() + "." + id.getPath()).append(" " + level).formatted(Formatting.YELLOW));
                else
                    list_1.add(new TranslatableText("modifier." + id.getNamespace() + "." + id.getPath()).formatted(Formatting.YELLOW));
            });
        }
    }

}

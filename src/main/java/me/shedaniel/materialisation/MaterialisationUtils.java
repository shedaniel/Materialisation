package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.config.ConfigHelper;
import me.shedaniel.materialisation.items.PartItem;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tools.ToolManager;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static net.minecraft.util.math.MathHelper.ceil;

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

    public static Formatting getDurabilityColoring(double durabilityPercentage) {
        if (durabilityPercentage >= 70d)
            return Formatting.GREEN;
        else if (durabilityPercentage >= 40d)
            return Formatting.GOLD;
        return Formatting.RED;
    }

    public static float getToolBreakingSpeed(ItemStack stack) {
        float speed = 0;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material")) {
                speed = getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getMiningSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
            }
        }
        if (stack.getItem() == Materialisation.MATERIALISED_HAMMER) speed /= 6f;
        if (stack.getItem() == Materialisation.MATERIALISED_MEGAAXE) speed /= 6.5f;
        return 0f;
    }

    public static int getToolMiningLevel(ItemStack stack) {
        int miningLevel = 0;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.containsKey("mt_1_material"))
                miningLevel = getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getMiningLevel).orElse(0);
        }
        return miningLevel;
    }

    public static int getToolDurability(ItemStack stack) {
        if (!stack.hasTag())
            return 1;
        CompoundTag tag = stack.getTag();
        if (tag.containsKey("mt_durability"))
            return stack.getTag().getInt("mt_durability");
        return getToolMaxDurability(stack);
    }

    public static int getToolMaxDurability(ItemStack stack) {
        int maxDurability = 1;
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material"))
                maxDurability = ceil(getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolDurability).orElse(0));
        }
        return maxDurability;
    }

    public static float getToolAttackDamage(ItemStack stack) {
        float damage = 0f;
        if (stack.hasTag() && stack.getItem() instanceof MaterialisedMiningTool) {
            CompoundTag tag = stack.getTag();
            if (tag.containsKey("mt_1_material"))
                damage = getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getAttackDamage).orElse(0d).floatValue() + MaterialisedMiningTool.getExtraDamage(((MaterialisedMiningTool) stack.getItem()).getToolType());
        }
        return damage;
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
        return PartItem.getItemModifierDamage();
    }

    public static UUID getItemModifierSwingSpeed() {
        return PartItem.getItemModifierSwingSpeed();
    }

}

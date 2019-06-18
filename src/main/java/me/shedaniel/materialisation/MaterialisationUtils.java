package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.items.ColoredItem;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tools.ToolManager;
import net.minecraft.ChatFormat;
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
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static net.minecraft.util.math.MathHelper.floor;

public class MaterialisationUtils {
    
    public static final NumberFormat TWO_DECIMAL_FORMATTER = new DecimalFormat("#.##");
    public static final ToolMaterial DUMMY_MATERIAL = new ToolMaterial() {
        @Override
        public int getDurability() {
            return 0;
        }
        
        @Override
        public float getBlockBreakingSpeed() {
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
    
    public static ChatFormat getColoring(double f) {
        if (f == 1d)
            return ChatFormat.GOLD;
        else if (f > 1d)
            return ChatFormat.GREEN;
        return ChatFormat.RED;
    }
    
    public static ChatFormat getColoringPercentage(double f) {
        if (f >= 70d)
            return ChatFormat.GREEN;
        else if (f >= 40d)
            return ChatFormat.GOLD;
        return ChatFormat.RED;
    }
    
    public static float getToolBreakingSpeed(ItemStack stack) {
        if (stack.getOrCreateTag().containsKey("mt_0_material") && stack.getOrCreateTag().containsKey("mt_1_material") && stack.getItem() == Materialisation.MATERIALISED_HAMMER)
            return getMatFromString(stack.getOrCreateTag().getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue() / 6f;
        if (stack.getOrCreateTag().containsKey("mt_0_material") && stack.getOrCreateTag().containsKey("mt_1_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_axe_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_axe_head_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_hammer_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_hammer_head_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue() / 6f;
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_pickaxe_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_pickaxe_head_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_shovel_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_shovel_head_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_sword_blade_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(stack.getOrCreateTag().getString("mt_sword_blade_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
        if (stack.getOrCreateTag().containsKey("mt_breakingspeed"))
            return stack.getOrCreateTag().getFloat("mt_breakingspeed");
        return 0f;
    }
    
    public static int getToolMiningLevel(ItemStack stack) {
        if (stack.getOrCreateTag().containsKey("mt_1_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::getMiningLevel).orElse(0);
        if (stack.getOrCreateTag().containsKey("mt_axe_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_axe_head_material")).map(PartMaterial::getMiningLevel).orElse(0);
        if (stack.getOrCreateTag().containsKey("mt_hammer_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_hammer_head_material")).map(PartMaterial::getMiningLevel).orElse(0);
        if (stack.getOrCreateTag().containsKey("mt_pickaxe_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_pickaxe_head_material")).map(PartMaterial::getMiningLevel).orElse(0);
        if (stack.getOrCreateTag().containsKey("mt_shovel_head_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_shovel_head_material")).map(PartMaterial::getMiningLevel).orElse(0);
        if (stack.getOrCreateTag().containsKey("mt_sword_blade_material"))
            return getMatFromString(stack.getOrCreateTag().getString("mt_sword_blade_material")).map(PartMaterial::getMiningLevel).orElse(0);
        if (stack.getOrCreateTag().containsKey("mt_mininglevel"))
            return stack.getOrCreateTag().getInt("mt_mininglevel");
        return 0;
    }
    
    public static int getToolDurability(ItemStack stack) {
        return stack.getOrCreateTag().containsKey("mt_durability") ? stack.getOrCreateTag().getInt("mt_durability") : getToolMaxDurability(stack);
    }
    
    public static int getToolMaxDurability(ItemStack stack) {
        if (stack.getOrCreateTag().containsKey("mt_0_material") && stack.getOrCreateTag().containsKey("mt_1_material"))
            return floor(getMatFromString(stack.getOrCreateTag().getString("mt_0_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(stack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::getToolDurability).orElse(0));
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_axe_head_material"))
            return floor(getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(stack.getOrCreateTag().getString("mt_axe_head_material")).map(PartMaterial::getToolDurability).orElse(0));
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_hammer_head_material"))
            return floor(getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(stack.getOrCreateTag().getString("mt_hammer_head_material")).map(PartMaterial::getToolDurability).orElse(0));
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_pickaxe_head_material"))
            return floor(getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(stack.getOrCreateTag().getString("mt_pickaxe_head_material")).map(PartMaterial::getToolDurability).orElse(0));
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_shovel_head_material"))
            return floor(getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(stack.getOrCreateTag().getString("mt_shovel_head_material")).map(PartMaterial::getToolDurability).orElse(0));
        if (stack.getOrCreateTag().containsKey("mt_handle_material") && stack.getOrCreateTag().containsKey("mt_sword_blade_material"))
            return floor(getMatFromString(stack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::getDurabilityMultiplier).orElse(0d) * getMatFromString(stack.getOrCreateTag().getString("mt_sword_blade_material")).map(PartMaterial::getToolDurability).orElse(0));
        if (stack.getOrCreateTag().containsKey("mt_maxdurability"))
            return stack.getOrCreateTag().getInt("mt_maxdurability");
        return 1;
    }
    
    public static float getToolAttackDamage(ItemStack stack) {
        PartMaterial material = stack.getOrCreateTag().containsKey("mt_1_material") ? getMatFromString(stack.getOrCreateTag().getString("mt_1_material")).orElse(null) : null;
        if (stack.getItem() == Materialisation.MATERIALISED_AXE) {
            if (material == null && stack.getOrCreateTag().containsKey("mt_axe_head_material"))
                material = getMatFromString(stack.getOrCreateTag().getString("mt_axe_head_material")).orElse(null);
            return material == null ? 0 : (float) material.getAttackDamage() + 7f;
        }
        if (stack.getItem() == Materialisation.MATERIALISED_HAMMER) {
            if (material == null && stack.getOrCreateTag().containsKey("mt_hammer_head_material"))
                material = getMatFromString(stack.getOrCreateTag().getString("mt_hammer_head_material")).orElse(null);
            return material == null ? 0 : (float) material.getAttackDamage() + 9f;
        }
        if (stack.getItem() == Materialisation.MATERIALISED_PICKAXE) {
            if (material == null && stack.getOrCreateTag().containsKey("mt_pickaxe_head_material"))
                material = getMatFromString(stack.getOrCreateTag().getString("mt_pickaxe_head_material")).orElse(null);
            return material == null ? 0 : (float) material.getAttackDamage() + 2f;
        }
        if (stack.getItem() == Materialisation.MATERIALISED_SWORD) {
            if (material == null && stack.getOrCreateTag().containsKey("mt_sword_blade_material"))
                material = getMatFromString(stack.getOrCreateTag().getString("mt_sword_blade_material")).orElse(null);
            return material == null ? 0 : (float) material.getAttackDamage() + 4f;
        }
        if (stack.getItem() == Materialisation.MATERIALISED_SHOVEL) {
            if (material == null && stack.getOrCreateTag().containsKey("mt_shovel_head_material"))
                material = getMatFromString(stack.getOrCreateTag().getString("mt_shovel_head_material")).orElse(null);
            return material == null ? 0 : (float) material.getAttackDamage() + 2.5f;
        }
        if (stack.getOrCreateTag().containsKey("mt_damage"))
            return stack.getOrCreateTag().getFloat("mt_damage");
        return 0f;
    }
    
    public static int getItemLayerColor(ItemStack stack, int layer) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.containsKey("mt_material"))
            return getMatFromString(tag.getString("mt_material")).map(PartMaterial::getToolColor).orElse(0);
        if (layer == 0)
            if (tag.containsKey("mt_handle_material"))
                return getMatFromString(tag.getString("mt_handle_material")).map(PartMaterial::getToolColor).orElse(0);
            else if (tag.containsKey("mt_0_material"))
                return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getToolColor).orElse(0);
        if (layer == 1)
            if (tag.containsKey("mt_1_material"))
                return getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolColor).orElse(0);
            else if (tag.containsKey("mt_pickaxe_head_material"))
                return getMatFromString(tag.getString("mt_pickaxe_head_material")).map(PartMaterial::getToolColor).orElse(0);
            else if (tag.containsKey("mt_axe_head_material"))
                return getMatFromString(tag.getString("mt_axe_head_material")).map(PartMaterial::getToolColor).orElse(0);
            else if (tag.containsKey("mt_shovel_head_material"))
                return getMatFromString(tag.getString("mt_shovel_head_material")).map(PartMaterial::getToolColor).orElse(0);
            else if (tag.containsKey("mt_sword_blade_material"))
                return getMatFromString(tag.getString("mt_sword_blade_material")).map(PartMaterial::getToolColor).orElse(0);
            else if (tag.containsKey("mt_hammer_head_material"))
                return getMatFromString(tag.getString("mt_hammer_head_material")).map(PartMaterial::getToolColor).orElse(0);
        return 0;
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
                for(int int_4 = 0; int_2 > 0 && int_4 < int_1; ++int_4)
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
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createAxeHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.AXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createPickaxeHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.PICKAXE_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createShovelHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.SHOVEL_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static ItemStack createSwordBlade(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.SWORD_BLADE);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static PartMaterial getMaterialFromPart(ItemStack stack) {
        if (stack.getOrCreateTag().containsKey("mt_0_material"))
            return getMaterialFromString(stack.getOrCreateTag().getString("mt_0_material"));
        else if (stack.getOrCreateTag().containsKey("mt_material"))
            return getMaterialFromString(stack.getOrCreateTag().getString("mt_material"));
        else
            return null;
    }
    
    public static PartMaterial getMaterialFromString(String s) {
        return getMatFromString(s).orElse(null);
    }
    
    public static Optional<PartMaterial> getMatFromString(String s) {
        Identifier identifier = new Identifier(s);
        Optional<PartMaterial> any = PartMaterials.getKnownMaterials().filter(mat -> mat.getIdentifier().equals(identifier)).findAny();
        return any;
    }
    
    public static boolean isHandleBright(ItemStack itemStack) {
        if (itemStack.getOrCreateTag().containsKey("mt_handle_bright"))
            return true;
        if (itemStack.getOrCreateTag().containsKey("mt_0_material"))
            return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_0_material")).map(PartMaterial::isBright).orElse(false);
        return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_handle_material")).map(PartMaterial::isBright).orElse(false);
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
    
    public static TriState mt_handleIsEffectiveOn(ItemStack stack, BlockState state) {
        ToolManager.Entry entry = (ToolManager.Entry) ToolManager.entry(state.getBlock());
        Tag<Item>[] tags = Materialisation.getReflectionField(entry, Tag[].class, 0).orElse(new Tag[0]);
        int[] tagLevels = Materialisation.getReflectionField(entry, int[].class, 1).orElse(new int[tags.length]);
        Item item = stack.getItem();
        for(int i = 0; i < tags.length; ++i)
            if (item.matches(tags[i]))
                return TriState.of(MaterialisationUtils.getToolMiningLevel(stack) >= tagLevels[i]);
        return Materialisation.getReflectionField(entry, TriState.class, 2).orElse(TriState.DEFAULT);
    }
    
    public static ItemStack createHammerHead(PartMaterial material) {
        ItemStack stack = new ItemStack(Materialisation.HAMMER_HEAD);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("mt_0_material", material.getIdentifier().toString());
        if (material.isBright())
            tag.putBoolean("mt_bright", true);
        stack.setTag(tag);
        return stack;
    }
    
    public static UUID getItemModifierDamage() {
        return ColoredItem.getItemModifierDamage();
    }
    
    public static UUID getItemModifierSwingSpeed() {
        return ColoredItem.getItemModifierSwingSpeed();
    }
    
}

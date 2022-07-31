package me.shedaniel.materialisation;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.api.*;
import me.shedaniel.materialisation.config.ConfigHelper;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.materialisation.modifiers.DefaultModifiers;
import me.shedaniel.materialisation.utils.RomanNumber;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@SuppressWarnings({"ConstantConditions", "DeprecatedIsStillUsed", "unused"})
public class MaterialisationUtils {
  public static final NumberFormat TWO_DECIMAL_FORMATTER = new DecimalFormat("#.##");
  public static final ToolMaterial DUMMY_MATERIAL = new ToolMaterial() {
    @Override
    public int getDurability() {
      return 0;
    }

    @Override
    public float getMiningSpeedMultiplier() {
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
    if (f == 1d) {
      return Formatting.GOLD;
    } else if (f > 1d) {
      return Formatting.GREEN;
    }
    return Formatting.RED;
  }

  public static Formatting getColoringPercentage(double f) {
    if (f >= 70d) {
      return Formatting.GREEN;
    } else if (f >= 40d) {
      return Formatting.GOLD;
    }
    return Formatting.RED;
  }

  public static int getToolEnchantability(ItemStack stack) {
    return getToolEnchantability(stack, true);
  }

  public static int getToolEnchantability(ItemStack stack, boolean modifiers) {
    int enchantability = 0;
    NbtCompound tag = stack.getOrCreateNbt();
    if (tag.contains("mt_0_material") && tag.contains("mt_1_material")) {
      PartMaterial handle = MaterialisationUtils.getMaterialFromString(tag.getString("mt_0_material"));
      PartMaterial head = MaterialisationUtils.getMaterialFromString(tag.getString("mt_1_material"));
      enchantability = ((handle == null ? 0 : handle.getEnchantability()) + (head == null ? 0 : head.getEnchantability())) / 2;
    }
    if (!modifiers) {
      return enchantability;
    }
    return enchantability + getToolExtraEnchantability(stack, enchantability);
  }

  @Deprecated
  private static int getToolExtraEnchantability(ItemStack stack, int base) {
    int extraEnchantability = 0;

    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
      if (entry.getValue() > 0) {
        extraEnchantability += entry.getKey().getExtraEnchantability(stack, entry.getValue());
      }
    }

    // We are not going to allow enchantability higher than 100
    return Math.min(extraEnchantability, 100 - base);
  }

  public static float getToolBreakingSpeed(ItemStack stack) {
    return getToolBreakingSpeed(stack, true);
  }

  public static float getToolBreakingSpeed(ItemStack stack, boolean modifiers) {
    float speed = getBaseToolBreakingSpeed(stack);
    if (speed <= 0) {
      return 0;
    }
    if (!modifiers) {
      return speed;
    }
    return getToolAfterExtraBreakingSpeed(stack, speed);
  }

  @Deprecated
  private static float getToolAfterExtraBreakingSpeed(ItemStack stack, float base) {
    float speed = base;

    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
      if (entry.getValue() > 0) {
        speed *= entry.getKey().getMiningSpeedMultiplier(stack, entry.getValue());
      }
    }
    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
      if (entry.getValue() > 0) {
        speed += entry.getKey().getExtraMiningSpeed(stack, entry.getValue());
      }
    }

    // We are not going to allow speed higher than 50
    return Math.min(speed, 50);
  }

  public static float getBaseToolBreakingSpeed(ItemStack stack) {
    float speed = 0;
    if (stack.hasNbt()) {
      NbtCompound tag = stack.getNbt();
      if (tag.contains("mt_0_material") && tag.contains("mt_1_material")) {
        speed = getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getBreakingSpeedMultiplier).orElse(0d).floatValue() * getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolSpeed).orElse(0d).floatValue();
      }
    }
    if (stack.getItem() == Materialisation.MATERIALISED_HAMMER) {
      speed /= 4.5f;
    }
    if (stack.getItem() == Materialisation.MATERIALISED_MEGAAXE) {
      speed /= 5.5f;
    }
    return Math.max(1.001f, speed);
  }

  public static int getToolMiningLevel(ItemStack stack) {
    return getToolMiningLevel(stack, true);
  }

  public static int getToolMiningLevel(ItemStack stack, boolean modifiers) {
    int miningLevel = 0;
    if (stack.hasNbt()) {
      NbtCompound tag = stack.getNbt();
      if (tag.contains("mt_1_material")) {
        miningLevel = getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getMiningLevel).orElse(0);
      }
    }
    if (miningLevel <= 0) {
      miningLevel = 0;
    }
    if (!modifiers) {
      return miningLevel;
    }
    return miningLevel + getToolExtraMiningLevel(stack, miningLevel);
  }

  @Deprecated
  private static int getToolExtraMiningLevel(ItemStack stack, int base) {
    int extraMiningLevel = 0;

    for (Map.Entry<Modifier, Integer> entry : getToolModifiers(stack).entrySet()) {
      if (entry.getValue() > 0) {
        extraMiningLevel += entry.getKey().getExtraMiningLevel(stack, entry.getValue());
      }
    }

    // We are not going to allow level higher than 10
    return Math.min(extraMiningLevel, 10 - base);
  }

  public static int getToolDurability(ItemStack stack) {
    if (!stack.hasNbt()) {
      return 1;
    }
    NbtCompound tag = stack.getNbt();
    if (tag.contains("mt_durability")) {
      return Math.min(tag.getInt("mt_durability"), getToolMaxDurability(stack));
    }
    return getToolMaxDurability(stack);
  }

  public static int getToolMaxDurability(ItemStack stack) {
    return getToolMaxDurability(stack, true);
  }

  public static int getToolMaxDurability(ItemStack stack, boolean modifiers) {
    if (!stack.hasNbt()) {
      return 1;
    }
    NbtCompound tag = stack.getNbt();
    if (tag.contains("mt_0_material") && tag.contains("mt_1_material")) {
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
    }
    return 1;
  }

  public static Map<Modifier, Integer> getToolModifiers(ItemStack stack) {
    if (!stack.hasNbt())
      return Collections.emptyMap();
    Map<Modifier, Integer> map = new HashMap<>();
    NbtCompound tag = stack.getNbt();
    assert tag != null;
    if (tag.contains("modifiers")) {
      NbtCompound modifiersTag = tag.getCompound("modifiers");
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
    if (!stack.hasNbt())
      return 0;
    NbtCompound tag = stack.getNbt();
    assert tag != null;
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
    if (!stack.hasNbt())
      return -1;
    NbtCompound tag = stack.getNbt();
    if (layer == 0) {
      assert tag != null;
      if (tag.contains("mt_0_material"))
        return getMatFromString(tag.getString("mt_0_material")).map(PartMaterial::getToolColor).orElse(-1);
    }
    if (layer == 1) {
      assert tag != null;
      if (tag.contains("mt_1_material"))
        return getMatFromString(tag.getString("mt_1_material")).map(PartMaterial::getToolColor).orElse(-1);
    }
    return -1;
  }

  public static void setToolDurability(ItemStack stack, int i) {
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putInt("mt_durability", Math.min(i, getToolMaxDurability(stack)));
    stack.setNbt(tag);
  }

  public static boolean applyDamage(ItemStack stack, int toDamage, Random random) {
    if (getToolDurability(stack) <= 0) {
      return false;
    } else {
      int reinforcedLevel;
      if (toDamage > 0) {
        reinforcedLevel = ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, DefaultModifiers.REINFORCED);
        int reducedDamage = 0;
        for (int i = 0; reinforcedLevel > 0 && i < toDamage; ++i)
          if (random.nextFloat() <= .2f * reinforcedLevel)
            ++reducedDamage;
        toDamage -= reducedDamage;
        if (toDamage <= 0)
          return false;
      }
      reinforcedLevel = getToolDurability(stack) - toDamage;
      setToolDurability(stack, reinforcedLevel);
      return reinforcedLevel < getToolDurability(stack);
    }
  }

  public static ItemStack createToolHandle(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.HANDLE);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createAxeHead(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.AXE_HEAD);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createPickaxeHead(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.PICKAXE_HEAD);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createShovelHead(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.SHOVEL_HEAD);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createSwordBlade(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.SWORD_BLADE);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static PartMaterial getMaterialFromPart(ItemStack stack) {
    if (stack.getOrCreateNbt().contains("mt_0_material"))
      return getMaterialFromString(stack.getOrCreateNbt().getString("mt_0_material"));
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
    return MaterialisationUtils.getMatFromString(itemStack.getOrCreateNbt().getString("mt_0_material")).map(PartMaterial::isBright).orElse(false);
  }

  public static boolean isHeadBright(ItemStack itemStack) {
    return MaterialisationUtils.getMatFromString(itemStack.getOrCreateNbt().getString("mt_1_material")).map(PartMaterial::isBright).orElse(false);
  }

  public static ItemStack createPickaxe(PartMaterial handle, PartMaterial pickaxeHead) {
    ItemStack stack = new ItemStack(Materialisation.MATERIALISED_PICKAXE);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putBoolean("mt_done_tool", true);
    tag.putString("mt_0_material", handle.getIdentifier().toString());
    tag.putString("mt_1_material", pickaxeHead.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createAxe(PartMaterial handle, PartMaterial axeHead) {
    ItemStack stack = new ItemStack(Materialisation.MATERIALISED_AXE);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putBoolean("mt_done_tool", true);
    tag.putString("mt_0_material", handle.getIdentifier().toString());
    tag.putString("mt_1_material", axeHead.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createShovel(PartMaterial handle, PartMaterial shovelHead) {
    ItemStack stack = new ItemStack(Materialisation.MATERIALISED_SHOVEL);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putBoolean("mt_done_tool", true);
    tag.putString("mt_0_material", handle.getIdentifier().toString());
    tag.putString("mt_1_material", shovelHead.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createSword(PartMaterial handle, PartMaterial swordBlade) {
    ItemStack stack = new ItemStack(Materialisation.MATERIALISED_SWORD);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putBoolean("mt_done_tool", true);
    tag.putString("mt_0_material", handle.getIdentifier().toString());
    tag.putString("mt_1_material", swordBlade.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createHammer(PartMaterial handle, PartMaterial hammerHead) {
    ItemStack stack = new ItemStack(Materialisation.MATERIALISED_HAMMER);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putBoolean("mt_done_tool", true);
    tag.putString("mt_0_material", handle.getIdentifier().toString());
    tag.putString("mt_1_material", hammerHead.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createMegaAxe(PartMaterial handle, PartMaterial megaAxeHead) {
    ItemStack stack = new ItemStack(Materialisation.MATERIALISED_MEGAAXE);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putBoolean("mt_done_tool", true);
    tag.putString("mt_0_material", handle.getIdentifier().toString());
    tag.putString("mt_1_material", megaAxeHead.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createMegaAxeHead(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.MEGAAXE_HEAD);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static ItemStack createHammerHead(PartMaterial material) {
    ItemStack stack = new ItemStack(Materialisation.HAMMER_HEAD);
    NbtCompound tag = stack.getOrCreateNbt();
    tag.putString("mt_0_material", material.getIdentifier().toString());
    stack.setNbt(tag);
    return stack;
  }

  public static UUID getItemModifierDamage() {
    return ColoredItem.getItemModifierDamage();
  }

  public static UUID getItemModifierSwingSpeed() {
    return ColoredItem.getItemModifierSwingSpeed();
  }

  @Environment(EnvType.CLIENT)
  public static void appendToolTooltip(ItemStack stack, MaterialisedMiningTool tool, World world, List<Text> texts, TooltipContext context) {
    int toolDurability = getToolDurability(stack);
    int baseMaxDurability = getToolMaxDurability(stack, false);
    int maxDurability = getToolMaxDurability(stack);
    if (baseMaxDurability > maxDurability) {
        texts.add(new LiteralText(I18n.translate("text.materialisation.max_durability_less", maxDurability, baseMaxDurability - maxDurability)));
    } else if (baseMaxDurability < maxDurability) {
        texts.add(new LiteralText(I18n.translate("text.materialisation.max_durability_more", maxDurability, maxDurability - baseMaxDurability)));
    } else {
        texts.add(new LiteralText(I18n.translate("text.materialisation.max_durability", maxDurability)));
    }
    if (toolDurability > 0) {
      float percentage = toolDurability / (float) maxDurability * 100;
      Formatting coloringPercentage = getColoringPercentage(percentage);
      texts.add(new LiteralText(I18n.translate("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + TWO_DECIMAL_FORMATTER.format(percentage) + Formatting.WHITE.toString())));
    } else {
        texts.add(new LiteralText(I18n.translate("text.materialisation.broken")));
    }
    if (ImmutableList.copyOf(ToolType.MINING_TOOLS).contains(tool.getToolType())) {
      {
        float breakingSpeed = getToolBreakingSpeed(stack, false);
        float extra = getToolBreakingSpeed(stack) - breakingSpeed;
        if (extra > 0) {
            texts.add(new LiteralText(I18n.translate("text.materialisation.breaking_speed_extra", TWO_DECIMAL_FORMATTER.format(breakingSpeed + extra), TWO_DECIMAL_FORMATTER.format(extra))));
        } else if (extra < 0) {
            texts.add(new LiteralText(I18n.translate("text.materialisation.breaking_speed_less", TWO_DECIMAL_FORMATTER.format(breakingSpeed + extra), TWO_DECIMAL_FORMATTER.format(-extra))));
        } else {
            texts.add(new LiteralText(I18n.translate("text.materialisation.breaking_speed", TWO_DECIMAL_FORMATTER.format(breakingSpeed))));
        }
      }
      {
        int miningLevel = getToolMiningLevel(stack, false);
        int extra = getToolMiningLevel(stack) - miningLevel;
        if (extra > 0) {
            texts.add(new LiteralText(I18n.translate("text.materialisation.mining_level_extra", miningLevel + extra, extra)));
        } else if (extra < 0) {
            texts.add(new LiteralText(I18n.translate("text.materialisation.mining_level_less", miningLevel + extra, -extra)));
        } else {
            texts.add(new LiteralText(I18n.translate("text.materialisation.mining_level", miningLevel)));
        }
      }
    }
    {
      int enchantability = getToolEnchantability(stack, false);
      int extra = getToolEnchantability(stack) - enchantability;
      if (extra > 0) {
          texts.add(new LiteralText(I18n.translate("text.materialisation.enchantability_extra", enchantability + extra, extra)));
      } else if (extra < 0) {
          texts.add(new LiteralText(I18n.translate("text.materialisation.enchantability_less", enchantability + extra, -extra)));
      } else {
          texts.add(new LiteralText(I18n.translate("text.materialisation.enchantability", enchantability)));
      }
    }
    {
      float attackDamage = getToolAttackDamage(stack, false);
      float extra = getToolAttackDamage(stack) - attackDamage;
      if (extra > 0) {
          texts.add(new LiteralText(I18n.translate("text.materialisation.attack_damage_extra", TWO_DECIMAL_FORMATTER.format(attackDamage + extra), TWO_DECIMAL_FORMATTER.format(extra))));
      } else if (extra < 0) {
          texts.add(new LiteralText(I18n.translate("text.materialisation.attack_damage_less", TWO_DECIMAL_FORMATTER.format(attackDamage + extra), TWO_DECIMAL_FORMATTER.format(-extra))));
      } else {
          texts.add(new LiteralText(I18n.translate("text.materialisation.attack_damage", TWO_DECIMAL_FORMATTER.format(attackDamage))));
      }
    }
    Map<Modifier, Integer> modifiers = getToolModifiers(stack);
    if (!modifiers.isEmpty()) {
      texts.add(new LiteralText(" "));
      for (Map.Entry<Modifier, Integer> entry : modifiers.entrySet()) {
        Identifier id = Materialisation.MODIFIERS.getId(entry.getKey());
        if (entry.getValue() != 1 || entry.getKey().getMaximumLevel(stack) != 1) {
          assert id != null;
          texts.add(new LiteralText(I18n.translate("modifier." + id.getNamespace() + "." + id.getPath()) + " " + RomanNumber.toRoman(entry.getValue())));
        } else {
          assert id != null;
          texts.add(new LiteralText(I18n.translate("modifier." + id.getNamespace() + "." + id.getPath())));
        }
      }
    }
  }

}

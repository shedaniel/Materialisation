package me.shedaniel.materialisation.modifiers;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationModifierMaterials;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.*;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DefaultModifiers implements DefaultModifiersSupplier {
    public static final Modifier HASTE;
    // public static final Modifier DIAMOND;
    public static final Modifier EMERALD;
    public static final Modifier SHARP;
    public static final Modifier LUCK;
    public static final Modifier FIRE;
    public static final Modifier AUTO_SMELT;
    public static final Modifier REINFORCED;
    private static final NumberFormat TWO_DECIMAL_FORMATTER = new DecimalFormat("#.##");
    
    static {
        HASTE = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .maximumLevel(4)
                .durabilityMultiplier((tool, level) -> {
                    float multiplier = 1;
                    if (level >= 1) multiplier *= 0.93;
                    if (level >= 2) multiplier *= 0.92;
                    if (level >= 3) multiplier *= 0.91;
                    if (level >= 4) multiplier *= 0.9;
                    return multiplier;
                })
                .miningSpeedMultiplier((tool, level) -> (level <= 0) ? 1 : 1 + level * 0.5f)
                .graphicalDescriptionLevelRange(1, 4)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.haste.line" + i));
                    {
                        float multiplier = 1;
                        if (level >= 1) multiplier *= 0.93;
                        if (level >= 2) multiplier *= 0.92;
                        if (level >= 3) multiplier *= 0.91;
                        if (level >= 4) multiplier *= 0.9;
                        textList.add(new TranslatableText("desc.materialisation.haste.line4", TWO_DECIMAL_FORMATTER.format(multiplier)));
                    }
                    textList.add(new TranslatableText("desc.materialisation.haste.line5", TWO_DECIMAL_FORMATTER.format((level <= 0) ? 1 : 1 + level * 0.5f)));
                    return textList;
                })
                .model(new Identifier(ModReference.MOD_ID, "modifier/pickaxe_haste"), ToolType.PICKAXE)
                .build();
        /*DIAMOND = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .maximumLevel(1)
                .durabilityMultiplier(0.9f)
                .extraMiningLevel(1)
                .graphicalDescriptionLevelRange(1, 1)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.diamond.line" + i));
                    textList.add(new TranslatableText("desc.materialisation.diamond.line4", TWO_DECIMAL_FORMATTER.format(.9f)));
                    textList.add(new TranslatableText("desc.materialisation.diamond.line5"));
                    return textList;
                })
                .model(new Identifier(ModReference.MOD_ID, "modifier/pickaxe_diamond"), ToolType.PICKAXE)
                .model(new Identifier(ModReference.MOD_ID, "modifier/axe_diamond"), ToolType.AXE, ToolType.MEGA_AXE)
                .model(new Identifier(ModReference.MOD_ID, "modifier/hammer_diamond"), ToolType.HAMMER)
                .model(new Identifier(ModReference.MOD_ID, "modifier/shovel_diamond"), ToolType.SHOVEL)
                .build();

         */
        EMERALD = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.ALL))
                .maximumLevel(1)
                .durabilityCost(-300)
                .graphicalDescriptionLevelRange(1, 1)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.emerald.line" + i));
                    textList.add(new TranslatableText("desc.materialisation.emerald.line4", 300));
                    return textList;
                })
                .model(new Identifier(ModReference.MOD_ID, "modifier/pickaxe_emerald"), ToolType.PICKAXE)
                .build();
        SHARP = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.WEAPON))
                .maximumLevel(8)
                .durabilityMultiplier((tool, level) -> {
                    float multiplier = 1;
                    if (level >= 1) multiplier *= 0.96;
                    if (level >= 2) multiplier *= 0.96;
                    if (level >= 3) multiplier *= 0.96;
                    if (level >= 4) multiplier *= 0.96;
                    if (level >= 5) multiplier *= 0.96;
                    if (level >= 6) multiplier *= 0.96;
                    if (level >= 7) multiplier *= 0.96;
                    if (level >= 8) multiplier *= 0.96;
                    return multiplier;
                })
                .attackDamageMultiplier((tool, level) -> 1 + level * 0.17f)
                .graphicalDescriptionLevelRange(1, 8)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.sharp.line" + i));
                    {
                        float multiplier = 1;
                        if (level >= 1) multiplier *= 0.96;
                        if (level >= 2) multiplier *= 0.96;
                        if (level >= 3) multiplier *= 0.96;
                        if (level >= 4) multiplier *= 0.96;
                        if (level >= 5) multiplier *= 0.96;
                        if (level >= 6) multiplier *= 0.96;
                        if (level >= 7) multiplier *= 0.96;
                        if (level >= 8) multiplier *= 0.96;
                        textList.add(new TranslatableText("desc.materialisation.sharp.line4", TWO_DECIMAL_FORMATTER.format(multiplier)));
                    }
                    textList.add(new TranslatableText("desc.materialisation.sharp.line5", TWO_DECIMAL_FORMATTER.format(1 + level * 0.17f)));
                    return textList;
                })
                .build();
        LUCK = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.ALL))
                .maximumLevel(3)
                .durabilityMultiplier((tool, level) -> {
                    float multiplier = 1;
                    if (level >= 1) multiplier *= 0.93;
                    if (level >= 2) multiplier *= 0.92;
                    if (level >= 3) multiplier *= 0.91;
                    return multiplier;
                })
                .graphicalDescriptionLevelRange(1, 3)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.luck.line" + i));
                    {
                        float multiplier = 1;
                        if (level >= 1) multiplier *= 0.93;
                        if (level >= 2) multiplier *= 0.92;
                        if (level >= 3) multiplier *= 0.91;
                        textList.add(new TranslatableText("desc.materialisation.luck.line4", TWO_DECIMAL_FORMATTER.format(multiplier)));
                    }
                    return textList;
                })
                .build();
        FIRE = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.WEAPON))
                .maximumLevel(1)
                .durabilityMultiplier((tool, level) -> level * .93f)
                .attackDamageMultiplier((tool, level) -> level * .97f)
                .graphicalDescriptionLevelRange(1, 1)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 4; i++)
                        textList.add(new TranslatableText("desc.materialisation.fire.line" + i));
                    textList.add(new TranslatableText("desc.materialisation.fire.line5", TWO_DECIMAL_FORMATTER.format(.93f)));
                    textList.add(new TranslatableText("desc.materialisation.fire.line6", TWO_DECIMAL_FORMATTER.format(1 + level * 0.17f)));
                    return textList;
                })
                .build();
        AUTO_SMELT = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .maximumLevel(1)
                .durabilityMultiplier((tool, level) -> level * .93f)
                .graphicalDescriptionLevelRange(1, 1)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.auto_smelt.line" + i));
                    textList.add(new TranslatableText("desc.materialisation.auto_smelt.line4", TWO_DECIMAL_FORMATTER.format(.93f)));
                    return textList;
                })
                .build();
        REINFORCED = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.ALL))
                .maximumLevel(3)
                .durabilityMultiplier((tool, level) -> (float) Math.pow(.96f, level))
                .graphicalDescriptionLevelRange(1, 3)
                .description(level -> {
                    List<Text> textList = new ArrayList<>();
                    for (int i = 1; i <= 3; i++)
                        textList.add(new TranslatableText("desc.materialisation.reinforced.line" + i));
                    textList.add(new TranslatableText("desc.materialisation.reinforced.line4", 20 * level));
                    textList.add(new TranslatableText("desc.materialisation.reinforced.line5", TWO_DECIMAL_FORMATTER.format(Math.pow(.96f, level))));
                    return textList;
                })
                .build();
    }
    
    @Override
    public void registerModifiers() {
        Registry.register(Materialisation.MODIFIERS, "materialisation:haste", HASTE);
        // Registry.register(Materialisation.MODIFIERS, "materialisation:diamond", DIAMOND);
        Registry.register(Materialisation.MODIFIERS, "materialisation:emerald", EMERALD);
        Registry.register(Materialisation.MODIFIERS, "materialisation:sharp", SHARP);
        Registry.register(Materialisation.MODIFIERS, "materialisation:luck", LUCK);
        Registry.register(Materialisation.MODIFIERS, "materialisation:fire", FIRE);
        Registry.register(Materialisation.MODIFIERS, "materialisation:auto_smelt", AUTO_SMELT);
        Registry.register(Materialisation.MODIFIERS, "materialisation:reinforced", REINFORCED);
    }
    
    @Override
    public void registerIngredients(ModifierIngredientsHandler handler) {
        handler.registerDefaultIngredient(HASTE, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 3))
                .registerIngredient(1, BetterIngredient.fromItem(Items.REDSTONE, 27))
                .registerIngredient(2, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 6))
                .registerIngredient(2, BetterIngredient.fromItem(Items.REDSTONE, 54))
                .registerIngredient(3, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 10))
                .registerIngredient(4, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 16))
                .build());
        /*
        handler.registerDefaultIngredient(DIAMOND, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.DIAMOND))
                .build());

         */
        handler.registerDefaultIngredient(EMERALD, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.EMERALD))
                .build());
        handler.registerDefaultIngredient(SHARP, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.QUARTZ, 12))
                .registerIngredient(1, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 3))
                .registerIngredient(2, BetterIngredient.fromItem(Items.QUARTZ, 12))
                .registerIngredient(2, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 3))
                .registerIngredient(3, BetterIngredient.fromItem(Items.QUARTZ, 16))
                .registerIngredient(3, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 4))
                .registerIngredient(4, BetterIngredient.fromItem(Items.QUARTZ, 24))
                .registerIngredient(4, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 6))
                .registerIngredient(4, BetterIngredient.fromItem(Items.QUARTZ, 40))
                .registerIngredient(5, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 10))
                .registerIngredient(4, BetterIngredient.fromItem(Items.QUARTZ, 64))
                .registerIngredient(6, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 16))
                .registerIngredient(7, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 24))
                .registerIngredient(8, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 32))
                .build());
        handler.registerDefaultIngredient(LUCK, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.LAPIS_BLOCK, 3))
                .registerIngredient(1, BetterIngredient.fromItem(Items.LAPIS_LAZULI, 27))
                .registerIngredient(2, BetterIngredient.fromItem(Items.LAPIS_BLOCK, 6))
                .registerIngredient(2, BetterIngredient.fromItem(Items.LAPIS_LAZULI, 54))
                .registerIngredient(3, BetterIngredient.fromItem(Items.LAPIS_BLOCK, 10))
                .registerIngredient(4, BetterIngredient.fromItem(Items.LAPIS_BLOCK, 16))
                .build());
        handler.registerDefaultIngredient(FIRE, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.MAGMA_CREAM, 16))
                .build());
        handler.registerDefaultIngredient(AUTO_SMELT, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(MaterialisationModifierMaterials.GOLD_INFUSED_CREAM, 1))
                .build());
        handler.registerDefaultIngredient(REINFORCED, ModifierIngredient.builder()
                .registerIngredient(-1, BetterIngredient.fromItem(MaterialisationModifierMaterials.REINFORCER, 1))
                .build());
    }
}

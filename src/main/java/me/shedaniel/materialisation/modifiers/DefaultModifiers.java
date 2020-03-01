package me.shedaniel.materialisation.modifiers;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.*;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DefaultModifiers implements DefaultModifiersSupplier {
    public static final Modifier HASTE;
    public static final Modifier DIAMOND;
    public static final Modifier SHARP;
    public static final Modifier FIRE;
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
                .miningSpeedMultiplier((tool, level) -> (level <= 0) ? 1 : 1 + level * 0.35f)
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
                    textList.add(new TranslatableText("desc.materialisation.haste.line5", TWO_DECIMAL_FORMATTER.format((level <= 0) ? 1 : 1 + level * 0.35f)));
                    return textList;
                })
                .build();
        DIAMOND = Modifier.builder()
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
    }
    
    @Override
    public void registerModifiers() {
        Registry.register(Materialisation.MODIFIERS, "materialisation:haste", HASTE);
        Registry.register(Materialisation.MODIFIERS, "materialisation:diamond", DIAMOND);
        Registry.register(Materialisation.MODIFIERS, "materialisation:sharp", SHARP);
        Registry.register(Materialisation.MODIFIERS, "materialisation:fire", FIRE);
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
        handler.registerDefaultIngredient(DIAMOND, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.DIAMOND))
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
        handler.registerDefaultIngredient(FIRE, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.MAGMA_CREAM, 16))
                .build());
    }
}

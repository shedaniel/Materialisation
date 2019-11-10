package me.shedaniel.materialisation.modifiers;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.*;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class DefaultModifiers implements DefaultModifiersSupplier {
    public static final Modifier HASTE;
    public static final Modifier DIAMOND;
    public static final Modifier SHARP;
    public static final Modifier FIRE;

    static {
        HASTE = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .maximumLevel(4)
                .durabilityMultiplier((tool, level) -> {
                    float multiplier = 1;
                    if (level >= 1) multiplier *= 0.93;
                    if (level >= 2) multiplier *= 0.87;
                    if (level >= 3) multiplier *= 0.84;
                    if (level >= 4) multiplier *= 0.83;
                    return multiplier;
                })
                .miningSpeedMultiplier((tool, level) -> (level <= 0) ? 1 : 1 + level * 0.4f)
                .build();
        DIAMOND = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .maximumLevel(1)
                .durabilityMultiplier(0.9f)
                .extraMiningLevel(1)
                .build();
        SHARP = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.WEAPON))
                .maximumLevel(8)
                .durabilityMultiplier((tool, level) -> {
                    float multiplier = 1;
                    if (level >= 1) multiplier *= 0.94;
                    if (level >= 2) multiplier *= 0.93;
                    if (level >= 3) multiplier *= 0.93;
                    if (level >= 4) multiplier *= 0.92;
                    if (level >= 5) multiplier *= 0.92;
                    if (level >= 6) multiplier *= 0.91;
                    if (level >= 7) multiplier *= 0.91;
                    if (level >= 8) multiplier *= 0.9;
                    return multiplier;
                })
                .attackDamageMultiplier((tool, level) -> 1 + level * 0.17f)
                .build();
        FIRE = Modifier.builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.WEAPON))
                .maximumLevel(1)
                .durabilityMultiplier((tool, level) -> 1 - level * .15f)
                .attackDamageMultiplier((tool, level) -> 1 - level * .05f)
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
                .registerIngredient(1, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 6))
                .registerIngredient(1, BetterIngredient.fromItem(Items.QUARTZ, 24))
                .registerIngredient(2, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 12))
                .registerIngredient(2, BetterIngredient.fromItem(Items.QUARTZ, 48))
                .registerIngredient(3, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 18))
                .registerIngredient(4, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 24))
                .build());
        handler.registerDefaultIngredient(DIAMOND, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.DIAMOND))
                .build());
        handler.registerDefaultIngredient(SHARP, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.REDSTONE, 27))
                .registerIngredient(1, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 3))
                .registerIngredient(2, BetterIngredient.fromItem(Items.REDSTONE, 27))
                .registerIngredient(2, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 3))
                .registerIngredient(3, BetterIngredient.fromItem(Items.REDSTONE, 36))
                .registerIngredient(3, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 4))
                .registerIngredient(4, BetterIngredient.fromItem(Items.REDSTONE, 54))
                .registerIngredient(4, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 6))
                .registerIngredient(5, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 10))
                .registerIngredient(6, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 16))
                .registerIngredient(7, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 24))
                .registerIngredient(8, BetterIngredient.fromItem(Items.REDSTONE_BLOCK, 32))
                .build());
        handler.registerDefaultIngredient(FIRE, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.MAGMA_CREAM, 16))
                .build());
    }
}

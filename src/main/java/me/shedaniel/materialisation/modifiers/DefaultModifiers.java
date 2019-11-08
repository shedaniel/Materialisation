package me.shedaniel.materialisation.modifiers;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.*;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import static me.shedaniel.materialisation.MaterialisationUtils.getBaseToolBreakingSpeed;

public class DefaultModifiers implements DefaultModifiersSupplier {
    public static final Modifier HASTE;
    public static final Modifier DIAMOND;

    static {
        HASTE = new Modifier.Builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .durabilityMultiplier((tool, level) -> {
                    float multiplier = 1;
                    if (level >= 1) multiplier *= 0.93;
                    if (level >= 2) multiplier *= 0.87;
                    if (level >= 3) multiplier *= 0.84;
                    return multiplier;
                })
                .extraMiningSpeed((tool, level) -> (level <= 0) ? 0 : (int) (level * 0.5 * getBaseToolBreakingSpeed(tool)))
                .build();
        DIAMOND = new Modifier.Builder()
                .applicableToolTypes(ImmutableList.copyOf(ToolType.MINING_TOOLS))
                .maximalLevel(1)
                .durabilityMultiplier(0.9f)
                .extraMiningLevel(1)
                .build();
    }

    @Override
    public void registerModifiers() {
        Registry.register(Materialisation.MODIFIERS, new Identifier("materialisation", "haste"), HASTE);
        Registry.register(Materialisation.MODIFIERS, new Identifier("materialisation", "diamond"), DIAMOND);
    }

    @Override
    public void registerIngredients(ModifierIngredientsHandler handler) {
        handler.registerDefaultIngredient(HASTE, ModifierIngredient.builder()
                .registerIngredient(1, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 12))
                .registerIngredient(2, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 16))
                .registerIngredient(3, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 16))
                .registerIngredient(4, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 32))
                .build());
        handler.registerDefaultIngredient(DIAMOND, ModifierIngredient.builder()
                .registerBase(BetterIngredient.fromItem(Items.DIAMOND))
                .build());
    }
}

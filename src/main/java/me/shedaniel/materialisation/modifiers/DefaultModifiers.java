package me.shedaniel.materialisation.modifiers;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.*;
import me.shedaniel.materialisation.modifiers.DiamondModifier;
import me.shedaniel.materialisation.modifiers.HasteModifier;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DefaultModifiers implements DefaultModifiersSupplier {

    public static final OldModifier HASTE;
    public static final OldModifier DIAMOND;

    static {
        HASTE = new HasteModifier();
        DIAMOND = new DiamondModifier();
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

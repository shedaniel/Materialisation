package me.shedaniel.materialisation.materials;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.DefaultModifiersSupplier;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ModifierIngredientsHandler;
import me.shedaniel.materialisation.modifiers.DiamondModifier;
import me.shedaniel.materialisation.modifiers.HasteModifier;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DefaultModifiers implements DefaultModifiersSupplier {

    public static final Modifier HASTE;
    public static final Modifier DIAMOND;

    static {
        HASTE = new HasteModifier();
        DIAMOND = new DiamondModifier();
    }

    @Override
    public void registerModifiers() {
        Registry.register(Materialisation.modifiers, new Identifier("materialisation", "haste"), HASTE);
        Registry.register(Materialisation.modifiers, new Identifier("materialisation", "diamond"), DIAMOND);
    }

    @Override
    public void registerIngredients(ModifierIngredientsHandler handler) {
        handler.registerDefaultIngredient(HASTE, BetterIngredient.fromItem(Items.QUARTZ_BLOCK, 16));
        handler.registerDefaultIngredient(DIAMOND, BetterIngredient.fromItem(Items.DIAMOND));
    }
}

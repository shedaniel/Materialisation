package me.shedaniel.materialisation.api;

public interface DefaultModifiersSupplier {
    default void registerModifiers() {
    }

    default void registerIngredients(ModifierIngredientsHandler handler) {
    }
}

package me.shedaniel.materialisation.modifiers;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.DefaultModifiersSupplier;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ModifierIngredientsHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class Modifiers {
    private static final Map<Modifier, List<BetterIngredient>> MODIFIER_MAP = new HashMap<>();

    public static void registerModifiers(ModifierIngredientsHandler handler) {
        for (Object o : FabricLoader.getInstance().getEntrypoints("materialisation_default", Object.class)) {
            if (o instanceof DefaultModifiersSupplier) {
                DefaultModifiersSupplier supplier = (DefaultModifiersSupplier) o;
                try {
                    supplier.registerModifiers();
                    supplier.registerIngredients(handler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    public static void resetMap() {
        MODIFIER_MAP.clear();
    }

    public static boolean containsIngredientForModifier(Identifier identifier) {
        Optional<Modifier> modifier = Materialisation.modifiers.getOrEmpty(identifier);
        if (!modifier.isPresent())
            throw new NullPointerException("Invalid identifier for modifier: " + identifier);
        return MODIFIER_MAP.containsKey(modifier.get());
    }

    public static void registerIngredient(Identifier identifier, BetterIngredient betterIngredient) {
        Optional<Modifier> modifier = Materialisation.modifiers.getOrEmpty(identifier);
        if (!modifier.isPresent())
            throw new NullPointerException("Invalid identifier for modifier: " + identifier);
        List<BetterIngredient> list = MODIFIER_MAP.getOrDefault(modifier.get(), new ArrayList<>());
        list.add(betterIngredient);
        MODIFIER_MAP.put(modifier.get(), list);
    }

    public static void registerIngredients(Identifier identifier, List<BetterIngredient> betterIngredients) {
        for (BetterIngredient ingredient : betterIngredients) registerIngredient(identifier, ingredient);
    }

    public static void fillEmpty() {
        for (Modifier modifier : Materialisation.modifiers) {
            if (!MODIFIER_MAP.containsKey(modifier)) MODIFIER_MAP.put(modifier, new ArrayList<>());
        }
    }

    public static boolean isIngredient(ItemStack itemStack) {
        return getModifierByIngredient(itemStack).isPresent();
    }

    public static Optional<Pair<Modifier, BetterIngredient>> getModifierByIngredient(ItemStack itemStack) {
        for (Map.Entry<Modifier, List<BetterIngredient>> entry : MODIFIER_MAP.entrySet()) {
            List<BetterIngredient> ingredientList = entry.getValue();
            for (BetterIngredient ingredient : ingredientList) {
                if (ingredient.isIncluded(itemStack)) {
                    return Optional.of(new Pair<>(entry.getKey(), ingredient));
                }
            }
        }
        return Optional.empty();
    }
}

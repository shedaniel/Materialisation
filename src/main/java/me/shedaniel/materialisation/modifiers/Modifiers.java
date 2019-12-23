package me.shedaniel.materialisation.modifiers;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class Modifiers {
    private static final Map<Modifier, List<ModifierIngredient>> MODIFIER_MAP = new HashMap<>();

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

    public static LevelMap<BetterIngredient> getIngredient(Modifier modifier) {
        LevelMap<BetterIngredient> map = new LevelMap<>();
        List<ModifierIngredient> list = MODIFIER_MAP.get(modifier);
        for (ModifierIngredient ingredient : list) {
            map.getBase().addAll(ingredient.getBaseIngredient());
            for (Map.Entry<Integer, List<BetterIngredient>> entry : ingredient.getIngredients().entrySet()) {
                List<BetterIngredient> ingredients = map.getOrDefault(entry.getKey(), new ArrayList<>());
                ingredients.addAll(entry.getValue());
                map.put(entry.getKey(), ingredients);
            }
        }
        return map;
    }

    public static void resetMap() {
        MODIFIER_MAP.clear();
    }

    public static boolean containsIngredientForModifier(Identifier identifier) {
        Optional<Modifier> modifier = Materialisation.MODIFIERS.getOrEmpty(identifier);
        if (!modifier.isPresent())
            throw new NullPointerException("Invalid identifier for modifier: " + identifier);
        return MODIFIER_MAP.containsKey(modifier.get());
    }

    public static void registerIngredient(Identifier identifier, ModifierIngredient betterIngredient) {
        Optional<Modifier> modifier = Materialisation.MODIFIERS.getOrEmpty(identifier);
        if (!modifier.isPresent())
            throw new NullPointerException("Invalid identifier for modifier: " + identifier);
        List<ModifierIngredient> list = MODIFIER_MAP.getOrDefault(modifier.get(), new ArrayList<>());
        list.add(betterIngredient);
        MODIFIER_MAP.put(modifier.get(), list);
    }

    public static void registerIngredients(Identifier identifier, List<ModifierIngredient> betterIngredients) {
        for (ModifierIngredient ingredient : betterIngredients) registerIngredient(identifier, ingredient);
    }

    public static void fillEmpty() {
        for (Modifier modifier : Materialisation.MODIFIERS) {
            if (!MODIFIER_MAP.containsKey(modifier)) MODIFIER_MAP.put(modifier, new ArrayList<>());
        }
    }

    public static boolean isIngredient(ItemStack itemStack, Modifier modifier, int level) {
        return getModifierByIngredient(itemStack, modifier, level).isPresent();
    }

    public static Optional<Pair<Modifier, Pair<ModifierIngredient, BetterIngredient>>> getModifierByIngredient(ItemStack itemStack, Modifier modifier, int level) {
        List<ModifierIngredient> ingredientList = MODIFIER_MAP.get(modifier);
        for (ModifierIngredient ingredient : ingredientList) {
            for (BetterIngredient betterIngredient : ingredient.getIngredient(level)) {
                if (betterIngredient.isIncluded(itemStack)) {
                    return Optional.of(new Pair<>(modifier, new Pair<>(ingredient, betterIngredient)));
                }
            }
        }
        return Optional.empty();
    }
}

package me.shedaniel.materialisation.api;

import java.util.*;

public interface ModifierIngredient {
    static Builder builder() {
        return new BuilderImpl();
    }

    Map<Integer, List<BetterIngredient>> getIngredients();

    default List<BetterIngredient> getIngredient(int level) {
        List<BetterIngredient> ingredients = getIngredients().get(level);
        if (ingredients != null) return ingredients;
        return getBaseIngredient();
    }

    default List<BetterIngredient> getBaseIngredient() {
        List<BetterIngredient> ingredients = getIngredients().get(-1);
        if (ingredients != null) return ingredients;
        return Collections.emptyList();
    }

    public static interface Builder {
        @Deprecated
        default Builder registerBase(BetterIngredient... ingredients) {
            return registerIngredient(-1, ingredients);
        }

        Builder registerIngredient(int level, BetterIngredient... ingredients);

        ModifierIngredient build();
    }

    public static final class ModifierIngredientImpl implements ModifierIngredient {
        private Map<Integer, List<BetterIngredient>> map;

        private ModifierIngredientImpl(Map<Integer, List<BetterIngredient>> map) {
            this.map = map;
        }

        @Override
        public Map<Integer, List<BetterIngredient>> getIngredients() {
            return map;
        }
    }

    public static class BuilderImpl implements Builder {
        private Map<Integer, List<BetterIngredient>> map;

        private BuilderImpl() {
            map = new HashMap<>();
        }

        @Override
        public Builder registerIngredient(int level, BetterIngredient... ingredients) {
            if (!map.containsKey(level)) map.put(level, new ArrayList<>());
            map.get(level).addAll(Arrays.asList(ingredients));
            return this;
        }

        @Override
        public ModifierIngredient build() {
            return new ModifierIngredientImpl(map);
        }
    }
}

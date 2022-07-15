package me.shedaniel.materialisation.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MaterialPreparerDisplay implements Display {
    
    private EntryStack first, result;
    private List<EntryStack> second;
    
    public MaterialPreparerDisplay(EntryStack first, List<EntryStack> second, EntryStack result) {
        this.first = first;
        this.second = second;
        this.result = result;
    }
    
    public EntryStack getFirst() {
        return first;
    }
    
    public List<EntryStack> getSecond() {
        return second;
    }
    
    public EntryStack getResult() {
        return result;
    }
    
    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> ingredients = new ArrayList<>(Collections.singletonList(EntryIngredient.of(getFirst())));
        ingredients.addAll(MaterialisationREIPlugin.map(getSecond(), EntryIngredient::of));
        return ingredients;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(MaterialisationREIPlugin.MATERIAL_PREPARER);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(EntryIngredient.of(getResult()));
    }
}

package me.shedaniel.materialisation.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class MaterialPreparerDisplay implements RecipeDisplay {

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
    public List<List<EntryStack>> getInputEntries() {
        return ImmutableList.of(Collections.singletonList(getFirst()), getSecond());
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return Collections.singletonList(getResult());
    }

    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MATERIAL_PREPARER;
    }
}

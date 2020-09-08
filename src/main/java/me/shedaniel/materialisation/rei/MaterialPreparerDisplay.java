package me.shedaniel.materialisation.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("CanBeFinal")
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
    
    @NotNull
    @Override
    public List<List<EntryStack>> getInputEntries() {
        return ImmutableList.of(Collections.singletonList(getFirst()), getSecond());
    }
    
    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return ImmutableList.of(Collections.singletonList(getResult()));
    }
    
    @NotNull
    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MATERIAL_PREPARER;
    }
}

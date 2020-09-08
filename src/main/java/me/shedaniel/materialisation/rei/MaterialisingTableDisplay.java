package me.shedaniel.materialisation.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MaterialisingTableDisplay implements RecipeDisplay {
    private EntryStack first, result;
    private EntryStack second;
    
    public MaterialisingTableDisplay(ItemStack first, ItemStack second, ItemStack result) {
        this(EntryStack.create(first).copy(), EntryStack.create(second).copy(), EntryStack.create(result).copy());
    }
    
    public MaterialisingTableDisplay(EntryStack first, EntryStack second, EntryStack result) {
        this.first = first;
        this.second = second;
        this.result = result;
    }
    
    public EntryStack getFirst() {
        return first;
    }
    
    public List<EntryStack> getSecond() {
        return Collections.singletonList(second);
    }
    
    public EntryStack getResult() {
        return result;
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return ImmutableList.of(Collections.singletonList(getFirst()), getSecond());
    }

    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return ImmutableList.of(Collections.singletonList(getResult()));
    }

    @NotNull
    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MATERIALISING_TABLE;
    }
}

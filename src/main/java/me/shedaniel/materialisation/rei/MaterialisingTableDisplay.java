package me.shedaniel.materialisation.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

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
    public List<List<EntryStack>> getInputEntries() {
        return ImmutableList.of(Collections.singletonList(getFirst()), getSecond());
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return Collections.singletonList(getResult());
    }

    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MATERIALISING_TABLE;
    }
}

package me.shedaniel.materialisation.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MaterialisingTableDisplay implements Display {
    private EntryStack first, result;
    private EntryStack second;
    
    public MaterialisingTableDisplay(ItemStack first, ItemStack second, ItemStack result) {
        this(EntryStacks.of(first).copy(), EntryStacks.of(second).copy(), EntryStacks.of(result).copy());
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
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> ingredients = new ArrayList<>(Collections.singletonList(EntryIngredient.of(getFirst())));
        ingredients.addAll(MaterialisationREIPlugin.map(getSecond(), EntryIngredient::of));
        return ingredients;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return null;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MaterialisationREIPlugin.MATERIALISING_TABLE;
    }
}

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
public class MaterialPreparerDisplay implements Display {

    // Pattern
    private final EntryStack<?> first;

    // Materials
    private final List<EntryStack<?>> second;

    // Result
    private final EntryStack<?> result;

    
    public MaterialPreparerDisplay(ItemStack pattern, List<ItemStack> materials, ItemStack result) {
        this.first = EntryStacks.of(pattern);
        this.second = MaterialisationREIPlugin.map(materials, EntryStacks::of);
        this.result = EntryStacks.of(result);
    }

    public EntryStack<?> getFirst() {
        return first;
    }

    public List<EntryStack<?>> getSecond() {
        return second;
    }

    public EntryStack<?> getResult() {
        return result;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> ingredients = new ArrayList<>(Collections.singletonList(EntryIngredient.of(getFirst())));
        ingredients.add(EntryIngredient.of(getSecond()));
        return ingredients;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MaterialisationREIPlugin.MATERIAL_PREPARER;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(EntryIngredient.of(getResult()));
    }
}

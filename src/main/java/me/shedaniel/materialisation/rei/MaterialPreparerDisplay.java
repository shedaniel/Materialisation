package me.shedaniel.materialisation.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MaterialPreparerDisplay implements Display {

    private List<EntryIngredient> inputs;
    private List<EntryIngredient> outputs;

    private EntryStack<?> first;
    private EntryStack<?> second;
    private EntryStack<?> result;

    
    public MaterialPreparerDisplay(ItemStack patternStack, List<ItemStack> inputs, ItemStack outputStack) {
        inputs.add(patternStack);
        this.inputs = MaterialisationREIPlugin.map(inputs, EntryIngredients::of);
        this.outputs = MaterialisationREIPlugin.stackToIngredients(outputStack);
        this.first = EntryStacks.of(inputs.get(0));
        this.second = EntryStacks.of(inputs.get(1));
        this.result = EntryStacks.of(outputStack);
    }

    public EntryStack<?> getFirst() {
        return first;
    }

    public EntryStack<?> getSecond() {
        return second;
    }

    public EntryStack<?> getResult() {
        return result;
    }

    public List<EntryIngredient> getInputs() {
        return inputs;
    }

    public List<EntryIngredient> getOutputs() {
        return outputs;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return getInputs();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(MaterialisationREIPlugin.MATERIAL_PREPARER);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return getOutputs();
    }
}

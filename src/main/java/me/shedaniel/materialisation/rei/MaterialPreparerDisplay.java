package me.shedaniel.materialisation.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MaterialPreparerDisplay implements Display {

    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    private final EntryStack<?> first;
    private final List<EntryStack<?>> second;
    private final EntryStack<?> result;

    
    public MaterialPreparerDisplay(ItemStack patternStack, List<ItemStack> secondInputs, ItemStack outputStack) {
        List<ItemStack> inputStacks = new ArrayList<>();
        inputStacks.add(patternStack);
        inputStacks.addAll(secondInputs);
        this.inputs = MaterialisationREIPlugin.map(inputStacks, EntryIngredients::of);
        this.outputs = MaterialisationREIPlugin.stackToIngredients(outputStack);
        this.first = EntryStacks.of(patternStack);
        this.second = MaterialisationREIPlugin.map(secondInputs, EntryStacks::of);
        this.result = EntryStacks.of(outputStack);
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

    public List<EntryIngredient> getInputs() {
        return inputs;
    }

    public List<EntryIngredient> getOutputs() {
        if (outputs.isEmpty()) outputs.add(EntryIngredients.of(ItemStack.EMPTY));
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

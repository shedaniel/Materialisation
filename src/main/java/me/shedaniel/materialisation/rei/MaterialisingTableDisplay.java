package me.shedaniel.materialisation.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MaterialisingTableDisplay implements RecipeDisplay {

    private ItemStack first, result;
    private ItemStack second;

    public MaterialisingTableDisplay(ItemStack first, ItemStack second, ItemStack result) {
        this.first = first;
        this.second = second;
        this.result = result;
    }

    public ItemStack getFirst() {
        return first;
    }

    public List<ItemStack> getSecond() {
        return Collections.singletonList(second);
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public Optional getRecipe() {
        return Optional.empty();
    }

    @Override
    public List<List<ItemStack>> getInput() {
        return ImmutableList.of(Collections.singletonList(getFirst()), getSecond());
    }

    @Override
    public List<ItemStack> getOutput() {
        return Collections.singletonList(getResult());
    }

    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MATERIALISING_TABLE;
    }
}

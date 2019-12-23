package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.LevelMap;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.modifiers.Modifiers;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaterialisationModifiersDisplay implements RecipeDisplay {
    private final Identifier modifierId;
    private int level;

    public MaterialisationModifiersDisplay(Identifier modifierId, int level) {
        this.modifierId = modifierId;
        this.level = level;
    }

    public final Modifier getModifier() {
        return Materialisation.MODIFIERS.get(getModifierId());
    }

    public final Identifier getModifierId() {
        return modifierId;
    }

    public final int getLevel() {
        return level;
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        LevelMap<BetterIngredient> ingredient = Modifiers.getIngredient(getModifier());
        if (ingredient.containsKey(level)) {
            List<EntryStack> stacks = new ArrayList<>();
            for (BetterIngredient betterIngredient : ingredient.get(level)) {
                for (ItemStack stack : betterIngredient.getStacks()) {
                    stacks.add(EntryStack.create(stack));
                }
            }
            return stacks.isEmpty() ? Collections.emptyList() : Collections.singletonList(stacks);
        }
        List<EntryStack> stacks = new ArrayList<>();
        for (BetterIngredient betterIngredient : ingredient.getBase()) {
            for (ItemStack stack : betterIngredient.getStacks()) {
                stacks.add(EntryStack.create(stack));
            }
        }
        return stacks.isEmpty() ? Collections.emptyList() : Collections.singletonList(stacks);
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return Collections.emptyList();
    }

    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MODIFIERS;
    }
}

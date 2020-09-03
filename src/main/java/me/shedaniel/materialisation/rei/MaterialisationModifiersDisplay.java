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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("CanBeFinal")
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
    
    @NotNull
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
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public Identifier getRecipeCategory() {
        return MaterialisationREIPlugin.MODIFIERS;
    }
}

package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.LevelMap;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.modifiers.Modifiers;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class MaterialisationModifiersDisplay implements Display {
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
    public List<EntryIngredient> getInputEntries() {
        LevelMap<BetterIngredient> ingredient = Modifiers.getIngredient(getModifier());
        if (ingredient.containsKey(level)) {
            List<EntryStack> stacks = new ArrayList<>();
            for (BetterIngredient betterIngredient : ingredient.get(level)) {
                for (ItemStack stack : betterIngredient.getStacks()) {
                    stacks.add(EntryStacks.of(stack));
                }
            }
            return stacks.isEmpty() ? Collections.emptyList() : MaterialisationREIPlugin.map(stacks, EntryIngredient::of);
        }
        List<EntryStack> stacks = new ArrayList<>();
        for (BetterIngredient betterIngredient : ingredient.getBase()) {
            for (ItemStack stack : betterIngredient.getStacks()) {
                stacks.add(EntryStacks.of(stack));
            }
        }
        return stacks.isEmpty() ? Collections.emptyList() : MaterialisationREIPlugin.map(stacks, EntryIngredient::of);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.emptyList();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MaterialisationREIPlugin.MODIFIERS;
    }
}

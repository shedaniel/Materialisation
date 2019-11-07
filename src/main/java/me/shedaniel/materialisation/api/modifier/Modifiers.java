package me.shedaniel.materialisation.api.modifier;

import me.shedaniel.materialisation.Materialisation;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.HashMap;
import java.util.Map;

public class Modifiers {
    private static final Map<Item, Modifier> ITEM_TO_MODIFIER = new HashMap<>();
    public static Registry<Modifier> modifiers = new SimpleRegistry();
    public static final Modifier DIAMOND;
//    public static final Modifier EMERALD;
//    public static final Modifier OBSIDIAN;

    static {
        DIAMOND = Registry.register(Modifiers.modifiers, new Identifier(Materialisation.MOD_ID, "diamiond"), new Modifier.Builder()
                .extraDurability(300)
                .extraMiningLevel(1).build());
    }

    public static boolean isModifier(Item item) {
        return ITEM_TO_MODIFIER.containsKey(item);
    }

    public static Modifier fromItem(Item item) {
        return ITEM_TO_MODIFIER.get(item);
    }
}

package me.shedaniel.materialisation.api.modifier;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class Modifiers {
    private static final Map<Item, Modifier> ITEM_TO_MODIFIER = new HashMap<>();
    public static final Modifier DIAMOND;
//    public static final Modifier EMERALD;
//    public static final Modifier OBSIDIAN;

    static {
        DIAMOND = register(Items.DIAMOND, new Modifier.Builder()
                .extraDurability(300)
                .extraMiningLevel(1).build());
    }

    public static Modifier register(Item item, Modifier modifier) {
        ITEM_TO_MODIFIER.putIfAbsent(item, modifier);
        return modifier;
    }

    public static boolean isModifier(Item item) {
        return ITEM_TO_MODIFIER.containsKey(item);
    }

    public static Modifier fromItem(Item item) {
        return ITEM_TO_MODIFIER.get(item);
    }
}

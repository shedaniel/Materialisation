package me.shedaniel.materialisation.api;

import me.shedaniel.materialisation.config.MaterialisationConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterIngredient {
    public Type type;
    public String content;
    public int count = 1;
    private transient Lazy<ItemStack[]> stacks = new Lazy<>(() -> {
        if (type == Type.ITEM)
            return new ItemStack[]{new ItemStack(Registry.ITEM.get(new Identifier(content)), count)};
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Item value : new ItemTags.CachingTag(new Identifier(content)).values()) {
            itemStacks.add(new ItemStack(value, count));
        }
        return itemStacks.toArray(new ItemStack[0]);
    });

    public BetterIngredient(Type type, String content) {
        this(type, content, 1);
    }

    public BetterIngredient(Type type, String content, int count) {
        this.type = type;
        this.content = content;
        this.count = count;
    }

    public static BetterIngredient fromItem(Item item) {
        return new BetterIngredient(Type.ITEM, Registry.ITEM.getId(item).toString());
    }

    public static BetterIngredient fromItem(Item item, int count) {
        return new BetterIngredient(Type.ITEM, Registry.ITEM.getId(item).toString(), count);
    }

    public static BetterIngredient fromItem(Identifier item) {
        return new BetterIngredient(Type.ITEM, item.toString());
    }

    public static BetterIngredient fromItem(Identifier item, int count) {
        return new BetterIngredient(Type.ITEM, item.toString(), count);
    }

    public static BetterIngredient fromTag(Tag<Item> tag) {
        return new BetterIngredient(Type.TAG, tag.getId().toString());
    }

    public static BetterIngredient fromTag(Tag<Item> tag, int count) {
        return new BetterIngredient(Type.TAG, tag.getId().toString(), count);
    }

    public static BetterIngredient fromTag(Identifier tag) {
        return new BetterIngredient(Type.TAG, tag.toString());
    }

    public static BetterIngredient fromTag(Identifier tag, int count) {
        return new BetterIngredient(Type.TAG, tag.toString(), count);
    }

    public MaterialisationConfig.ConfigIngredient toConfigIngredient() {
        return new MaterialisationConfig.ConfigIngredient(type, content, count);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BetterIngredient))
            return false;
        if (obj == this)
            return true;
        return ((BetterIngredient) obj).type == this.type && ((BetterIngredient) obj).content.equalsIgnoreCase(this.content) && this.count == ((BetterIngredient) obj).count;
    }

    public ItemStack[] getStacks() {
        return stacks.get();
    }

    public List<ItemStack> getStacksList() {
        return Arrays.asList(getStacks());
    }

    public boolean isIncluded(ItemStack itemStack_1) {
        if (itemStack_1 == null)
            return false;
        for (ItemStack stack : getStacks())
            if (stack.getItem() == itemStack_1.getItem() && stack.getCount() <= itemStack_1.getCount())
                return true;
        return false;
    }

    public static enum Type {
        ITEM,
        TAG
    }
}
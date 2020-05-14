package me.shedaniel.materialisation;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MaterialisationModifierMaterials {
    public static final Item REINFORCER = new Item(new Item.Settings().group(ItemGroup.MATERIALS).maxCount(4));
    public static final Item GOLD_INFUSED_CREAM = new Item(new Item.Settings().group(ItemGroup.MATERIALS).maxCount(4));
    
    public static void register() {
        registerItem("reinforcer", REINFORCER);
        registerItem("gold_infused_cream", GOLD_INFUSED_CREAM);
    }
    
    private static void registerBlock(String name, Block block) {
        registerBlock(name, block, new Item.Settings());
    }
    
    private static void registerBlock(String name, Block block, ItemGroup group) {
        registerBlock(name, block, new Item.Settings().group(group));
    }
    
    private static void registerBlock(String name, Block block, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(ModReference.MOD_ID, name), block);
        registerItem(name, new BlockItem(block, settings));
    }
    
    private static void registerItem(String name, Item item) {
        Registry.register(Registry.ITEM, new Identifier(ModReference.MOD_ID, name), item);
    }
}

package me.shedaniel.materialisedtools;

import me.shedaniel.materialisedtools.blocks.MaterialisingTableBlock;
import me.shedaniel.materialisedtools.containers.MaterialisingTableContainer;
import me.shedaniel.materialisedtools.items.MaterialisedPickaxeItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.container.BlockContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;
import java.util.Optional;

public class MaterialisedTools implements ModInitializer {
    
    public static final Block MATERIALISING_TABLE = new MaterialisingTableBlock();
    public static final Identifier MATERIALISING_TABLE_CONTAINER = new Identifier(MaterialisedReference.MOD_ID, "materialising_table");
    public static final Item MATERIALISED_PICKAXE = new MaterialisedPickaxeItem(new Item.Settings());
    
    public static <T> Optional<T> getReflectionField(Object parent, Class<T> clazz, int index) {
        try {
            Field field = parent.getClass().getDeclaredFields()[index];
            if (!field.isAccessible())
                field.setAccessible(true);
            return Optional.ofNullable(clazz.cast(field.get(parent)));
        } catch (Exception e) {
            System.out.printf("Reflection failed! Trying to get #" + index + " from %s", clazz.getName());
            return Optional.empty();
        }
    }
    
    @Override
    public void onInitialize() {
        registerBlock("materialising_table", MATERIALISING_TABLE, ItemGroup.DECORATIONS);
        ContainerProviderRegistry.INSTANCE.registerFactory(MATERIALISING_TABLE_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> {
            return new MaterialisingTableContainer(syncId, playerEntity.inventory, BlockContext.create(playerEntity.world, packetByteBuf.readBlockPos()));
        });
        registerItem("materialised_pickaxe", MATERIALISED_PICKAXE);
    }
    
    private void registerBlock(String name, Block block) {
        registerBlock(name, block, new Item.Settings());
    }
    
    private void registerBlock(String name, Block block, ItemGroup group) {
        registerBlock(name, block, new Item.Settings().itemGroup(group));
    }
    
    private void registerBlock(String name, Block block, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(MaterialisedReference.MOD_ID, name), block);
        registerItem(name, new BlockItem(block, settings));
    }
    
    private void registerItem(String name, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MaterialisedReference.MOD_ID, name), item);
    }
    
}

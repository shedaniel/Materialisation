package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.blocks.MaterialPreparerBlock;
import me.shedaniel.materialisation.blocks.MaterialisingTableBlock;
import me.shedaniel.materialisation.config.ConfigHelper;
import me.shedaniel.materialisation.config.MaterialisationConfig;
import me.shedaniel.materialisation.gui.MaterialPreparerScreenHandler;
import me.shedaniel.materialisation.gui.MaterialisingTableScreenHandler;
import me.shedaniel.materialisation.items.*;
import me.shedaniel.materialisation.utils.ResettableSimpleRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

@SuppressWarnings("unused")
public class Materialisation implements ModInitializer {
    
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Block MATERIALISING_TABLE = new MaterialisingTableBlock();
    public static final Block MATERIAL_PREPARER = new MaterialPreparerBlock();
    public static final Identifier MATERIAL_PREPARER_CONTAINER = new Identifier(ModReference.MOD_ID, "material_preparer");
    public static final Identifier MATERIALISING_TABLE_CONTAINER = new Identifier(ModReference.MOD_ID, "materialising_table");
    public static final ScreenHandlerType<MaterialPreparerScreenHandler> MATERIAL_PREPARER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(Materialisation.MATERIAL_PREPARER_CONTAINER, MaterialPreparerScreenHandler::new);
    public static final ScreenHandlerType<MaterialisingTableScreenHandler> MATERIALISING_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(Materialisation.MATERIALISING_TABLE_CONTAINER, MaterialisingTableScreenHandler::new);
    public static final Identifier MATERIALISING_TABLE_RENAME = new Identifier(ModReference.MOD_ID, "materialising_table_rename");
    public static final Identifier MATERIALISING_TABLE_PLAY_SOUND = new Identifier(ModReference.MOD_ID, "materialising_table_play_sound");
    public static final Item MATERIALISED_PICKAXE = new MaterialisedPickaxeItem(new Item.Settings());
    public static final Item MATERIALISED_AXE = new MaterialisedAxeItem(new Item.Settings());
    public static final Item MATERIALISED_SHOVEL = new MaterialisedShovelItem(new Item.Settings());
    public static final Item MATERIALISED_SWORD = new MaterialisedSwordItem(new Item.Settings());
    public static final Item MATERIALISED_HAMMER = new MaterialisedHammerItem(new Item.Settings());
    public static final Item MATERIALISED_MEGAAXE = new MaterialisedMegaAxeItem(new Item.Settings());
    public static final Item HANDLE = new ColoredItem(new Item.Settings());
    public static final Item AXE_HEAD = new ColoredItem(new Item.Settings());
    public static final Item PICKAXE_HEAD = new ColoredItem(new Item.Settings());
    public static final Item SHOVEL_HEAD = new ColoredItem(new Item.Settings());
    public static final Item SWORD_BLADE = new ColoredItem(new Item.Settings());
    public static final Item HAMMER_HEAD = new ColoredItem(new Item.Settings());
    public static final Item MEGAAXE_HEAD = new ColoredItem(new Item.Settings());
    public static final Item BLANK_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item TOOL_HANDLE_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item PICKAXE_HEAD_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item AXE_HEAD_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item SHOVEL_HEAD_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item SWORD_BLADE_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item HAMMER_HEAD_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item MEGAAXE_HEAD_PATTERN = new PatternItem(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Registry<Modifier> MODIFIERS = new ResettableSimpleRegistry<>("modifiers");
    public static MaterialisationConfig config;

    public static <T> Optional<T> getReflectionField(Object parent, Class<T> clazz, int index) {
        try {
            Field field = parent.getClass().getDeclaredFields()[index];
            if (!field.isAccessible())
                field.setAccessible(true);
            return Optional.ofNullable(clazz.cast(field.get(parent)));
        } catch (Exception e) {
            Materialisation.LOGGER.printf(Level.ERROR, "Reflection failed! Trying to get #%d from %s", index, clazz.getName());
            return Optional.empty();
        }
    }
    
    @Override
    public void onInitialize() {
        MaterialisationModifierMaterials.register();
        registerBlock("materialising_table", MATERIALISING_TABLE, ItemGroup.DECORATIONS);
        registerBlock("material_preparer", MATERIAL_PREPARER, ItemGroup.DECORATIONS);
        ServerSidePacketRegistry.INSTANCE.register(MATERIALISING_TABLE_RENAME, (packetContext, packetByteBuf) -> {
            if (packetContext.getPlayer().currentScreenHandler instanceof MaterialisingTableScreenHandler) {
                MaterialisingTableScreenHandler container = (MaterialisingTableScreenHandler)packetContext.getPlayer().currentScreenHandler;
                String string_1 = SharedConstants.stripInvalidChars(packetByteBuf.readString(32767));
                if (string_1.length() <= 35)
                    container.setNewItemName(string_1);
            }
        });
        registerItem("materialised_pickaxe", MATERIALISED_PICKAXE);
        registerItem("materialised_axe", MATERIALISED_AXE);
        registerItem("materialised_shovel", MATERIALISED_SHOVEL);
        registerItem("materialised_sword", MATERIALISED_SWORD);
        registerItem("materialised_hammer", MATERIALISED_HAMMER);
        registerItem("materialised_megaaxe", MATERIALISED_MEGAAXE);
        registerItem("handle", HANDLE);
        registerItem("axe_head", AXE_HEAD);
        registerItem("pickaxe_head", PICKAXE_HEAD);
        registerItem("shovel_head", SHOVEL_HEAD);
        registerItem("sword_blade", SWORD_BLADE);
        registerItem("hammer_head", HAMMER_HEAD);
        registerItem("megaaxe_head", MEGAAXE_HEAD);
        registerItem("blank_pattern", BLANK_PATTERN);
        registerItem("handle_pattern", TOOL_HANDLE_PATTERN);
        registerItem("pickaxe_head_pattern", PICKAXE_HEAD_PATTERN);
        registerItem("axe_head_pattern", AXE_HEAD_PATTERN);
        registerItem("shovel_head_pattern", SHOVEL_HEAD_PATTERN);
        registerItem("sword_blade_pattern", SWORD_BLADE_PATTERN);
        registerItem("hammer_head_pattern", HAMMER_HEAD_PATTERN);
        registerItem("megaaxe_head_pattern", MEGAAXE_HEAD_PATTERN);
        try {
            ConfigHelper.loadDefault();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ConfigHelper.loadConfig();
    }
    
    private void registerBlock(String name, Block block) {
        registerBlock(name, block, new Item.Settings());
    }
    
    @SuppressWarnings("SameParameterValue")
    private void registerBlock(String name, Block block, ItemGroup group) {
        registerBlock(name, block, new Item.Settings().group(group));
    }
    
    private void registerBlock(String name, Block block, Item.Settings settings) {
        Registry.register(Registry.BLOCK, new Identifier(ModReference.MOD_ID, name), block);
        registerItem(name, new BlockItem(block, settings));
    }
    
    private void registerItem(String name, Item item) {
        Registry.register(Registry.ITEM, new Identifier(ModReference.MOD_ID, name), item);
    }
}

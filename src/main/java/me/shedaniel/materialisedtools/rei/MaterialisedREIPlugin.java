package me.shedaniel.materialisedtools.rei;

import me.shedaniel.materialisedtools.MaterialisedReference;
import me.shedaniel.materialisedtools.MaterialisedTools;
import me.shedaniel.materialisedtools.api.KnownMaterials;
import me.shedaniel.materialisedtools.items.ColoredItem;
import me.shedaniel.materialisedtools.items.MaterialisedToolUtils;
import me.shedaniel.rei.api.ItemRegistry;
import me.shedaniel.rei.api.REIPluginEntry;
import net.minecraft.util.Identifier;

public class MaterialisedREIPlugin implements REIPluginEntry {
    
    private static final Identifier PLUGIN = new Identifier(MaterialisedReference.MOD_ID, "rei_plugin");
    
    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void registerItems(ItemRegistry itemRegistry) {
        KnownMaterials.getKnownMaterials().map(MaterialisedToolUtils::createToolHandle).forEach(stack -> itemRegistry.registerItemStack(MaterialisedTools.HANDLE, stack));
        KnownMaterials.getKnownMaterials().map(MaterialisedToolUtils::createPickaxeHead).forEach(stack -> itemRegistry.registerItemStack(MaterialisedTools.PICKAXE_HEAD, stack));
        KnownMaterials.getKnownMaterials().forEach(handle -> KnownMaterials.getKnownMaterials().forEach(pickaxeHead -> itemRegistry.registerItemStack(MaterialisedTools.MATERIALISED_PICKAXE, MaterialisedToolUtils.createPickaxe(handle, pickaxeHead))));
        itemRegistry.getModifiableItemList().removeIf(stack -> (stack.getItem() == MaterialisedTools.MATERIALISED_PICKAXE && !stack.getOrCreateTag().containsKey("mt_done_tool") && !stack.getOrCreateTag().getBoolean("mt_done_tool")) || (stack.getItem() instanceof ColoredItem && !stack.getOrCreateTag().containsKey("mt_name_key") && !stack.getOrCreateTag().containsKey("mt_material")));
    }
    
}

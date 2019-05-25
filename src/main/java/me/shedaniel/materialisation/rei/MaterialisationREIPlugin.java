package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.KnownMaterials;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedToolUtils;
import me.shedaniel.rei.api.ItemRegistry;
import me.shedaniel.rei.api.REIPluginEntry;
import net.minecraft.util.Identifier;

public class MaterialisationREIPlugin implements REIPluginEntry {
    
    private static final Identifier PLUGIN = new Identifier(ModReference.MOD_ID, "rei_plugin");
    
    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void registerItems(ItemRegistry itemRegistry) {
        KnownMaterials.getKnownMaterials().map(MaterialisedToolUtils::createToolHandle).forEach(stack -> itemRegistry.registerItemStack(Materialisation.HANDLE, stack));
        KnownMaterials.getKnownMaterials().map(MaterialisedToolUtils::createPickaxeHead).forEach(stack -> itemRegistry.registerItemStack(Materialisation.PICKAXE_HEAD, stack));
        KnownMaterials.getKnownMaterials().forEach(handle -> KnownMaterials.getKnownMaterials().forEach(pickaxeHead -> itemRegistry.registerItemStack(Materialisation.MATERIALISED_PICKAXE, MaterialisedToolUtils.createPickaxe(handle, pickaxeHead))));
        itemRegistry.getModifiableItemList().removeIf(stack -> (stack.getItem() == Materialisation.MATERIALISED_PICKAXE && !stack.getOrCreateTag().containsKey("mt_done_tool") && !stack.getOrCreateTag().getBoolean("mt_done_tool")) || (stack.getItem() instanceof ColoredItem && !stack.getOrCreateTag().containsKey("mt_name_key") && !stack.getOrCreateTag().containsKey("mt_material")));
    }
    
}

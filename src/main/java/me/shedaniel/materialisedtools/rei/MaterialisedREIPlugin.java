package me.shedaniel.materialisedtools.rei;

import me.shedaniel.materialisedtools.MaterialisedReference;
import me.shedaniel.materialisedtools.MaterialisedTools;
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
        itemRegistry.getModifiableItemList().removeIf(itemStack -> itemStack.getItem() == MaterialisedTools.MATERIALISED_PICKAXE);
    }
    
}

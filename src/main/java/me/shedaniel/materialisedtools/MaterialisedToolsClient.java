package me.shedaniel.materialisedtools;

import me.shedaniel.materialisedtools.containers.MaterialisingTableContainer;
import me.shedaniel.materialisedtools.containers.MaterialisingTableScreen;
import me.shedaniel.materialisedtools.items.MaterialisedToolUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.impl.client.render.ColorProviderRegistryImpl;
import net.minecraft.network.chat.TranslatableComponent;

public class MaterialisedToolsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(MaterialisedTools.MATERIALISING_TABLE_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> {
            return new MaterialisingTableScreen(new MaterialisingTableContainer(syncId, playerEntity.inventory), playerEntity.inventory, new TranslatableComponent("container.materialisedtools.materialising_table"));
        });
        ColorProviderRegistryImpl.ITEM.register(MaterialisedToolUtils::getColor, MaterialisedTools.MATERIALISED_PICKAXE);
    }
}

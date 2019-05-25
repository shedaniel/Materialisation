package me.shedaniel.materialisation;

import me.shedaniel.materialisation.containers.MaterialisingTableContainer;
import me.shedaniel.materialisation.containers.MaterialisingTableScreen;
import me.shedaniel.materialisation.items.MaterialisedToolUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.impl.client.render.ColorProviderRegistryImpl;
import net.minecraft.network.chat.TranslatableComponent;

public class MaterialisationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(Materialisation.MATERIALISING_TABLE_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> {
            return new MaterialisingTableScreen(new MaterialisingTableContainer(syncId, playerEntity.inventory), playerEntity.inventory, new TranslatableComponent("container.materialisation.materialising_table"));
        });
        ColorProviderRegistryImpl.ITEM.register(MaterialisedToolUtils::getItemLayerColor, Materialisation.MATERIALISED_PICKAXE, Materialisation.HANDLE, Materialisation.PICKAXE_HEAD);
    }
}

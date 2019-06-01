package me.shedaniel.materialisation;

import me.shedaniel.materialisation.containers.MaterialPreparerContainer;
import me.shedaniel.materialisation.containers.MaterialPreparerScreen;
import me.shedaniel.materialisation.containers.MaterialisingTableContainer;
import me.shedaniel.materialisation.containers.MaterialisingTableScreen;
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
        ScreenProviderRegistry.INSTANCE.registerFactory(Materialisation.MATERIAL_PREPARER_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> {
            return new MaterialPreparerScreen(new MaterialPreparerContainer(syncId, playerEntity.inventory), playerEntity.inventory, new TranslatableComponent("container.materialisation.material_preparer"));
        });
        ColorProviderRegistryImpl.ITEM.register(MaterialisationUtils::getItemLayerColor, Materialisation.MATERIALISED_PICKAXE, Materialisation.MATERIALISED_SHOVEL, Materialisation.MATERIALISED_AXE, Materialisation.MATERIALISED_SWORD, Materialisation.HANDLE, Materialisation.SWORD_BLADE, Materialisation.SHOVEL_HEAD, Materialisation.PICKAXE_HEAD, Materialisation.AXE_HEAD);
    }
}

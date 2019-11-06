package me.shedaniel.materialisation;

import me.shedaniel.materialisation.containers.MaterialPreparerContainer;
import me.shedaniel.materialisation.containers.MaterialPreparerScreen;
import me.shedaniel.materialisation.containers.MaterialisingTableContainer;
import me.shedaniel.materialisation.containers.MaterialisingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.impl.client.render.ColorProviderRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class MaterialisationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(Materialisation.MATERIALISING_TABLE_CONTAINER,
                (syncId, identifier, playerEntity, packetByteBuf) -> new MaterialisingTableScreen(
                        new MaterialisingTableContainer(syncId, playerEntity.inventory),
                        playerEntity.inventory,
                        new TranslatableText("container.materialisation.materialising_table")));
        ScreenProviderRegistry.INSTANCE.registerFactory(Materialisation.MATERIAL_PREPARER_CONTAINER,
                (syncId, identifier, playerEntity, packetByteBuf) -> new MaterialPreparerScreen(
                        new MaterialPreparerContainer(syncId, playerEntity.inventory),
                        playerEntity.inventory,
                        new TranslatableText("container.materialisation.material_preparer")));

        ClientSidePacketRegistry.INSTANCE.register(Materialisation.MATERIALISING_TABLE_PLAY_SOUND,
                (packetContext, packetByteBuf) -> MinecraftClient.getInstance().getSoundManager().play(
                        PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_USE, 1, 1)));

        ColorProviderRegistryImpl.ITEM.register(
                MaterialisationUtils::getItemLayerColor,
                Materialisation.MATERIALISED_MEGAAXE,
                Materialisation.MEGAAXE_HEAD,
                Materialisation.MATERIALISED_PICKAXE,
                Materialisation.MATERIALISED_SHOVEL,
                Materialisation.MATERIALISED_AXE,
                Materialisation.MATERIALISED_SWORD,
                Materialisation.MATERIALISED_HAMMER,
                Materialisation.HAMMER_HEAD,
                Materialisation.HANDLE,
                Materialisation.SWORD_BLADE,
                Materialisation.SHOVEL_HEAD,
                Materialisation.PICKAXE_HEAD,
                Materialisation.AXE_HEAD
        );
    }
}

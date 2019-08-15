package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.PartMaterial;
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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MaterialisationClient implements ClientModInitializer {
    public static Optional<String> getItemTranslationKey(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.containsKey("mt_name_key")) {
            return Optional.ofNullable(tag.getString("mt_name_key"));
        } else if (tag.containsKey("mt_0_material") || tag.containsKey("mt_material")) {
            PartMaterial material = MaterialisationUtils.getMaterialFromPart(stack);
            if (material != null)
                if (stack.getItem() == Materialisation.HANDLE)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_handle", I18n.translate(material.getMaterialTranslateKey())));
                else if (stack.getItem() == Materialisation.PICKAXE_HEAD)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_pickaxe_head", I18n.translate(material.getMaterialTranslateKey())));
                else if (stack.getItem() == Materialisation.AXE_HEAD)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_axe_head", I18n.translate(material.getMaterialTranslateKey())));
                else if (stack.getItem() == Materialisation.SHOVEL_HEAD)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_shovel_head", I18n.translate(material.getMaterialTranslateKey())));
                else if (stack.getItem() == Materialisation.SWORD_BLADE)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_sword_blade", I18n.translate(material.getMaterialTranslateKey())));
                else if (stack.getItem() == Materialisation.HAMMER_HEAD)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_hammer_head", I18n.translate(material.getMaterialTranslateKey())));
                else if (stack.getItem() == Materialisation.MEGAAXE_HEAD)
                    return Optional.ofNullable(I18n.translate("item.materialisation.materialised_megaaxe_head", I18n.translate(material.getMaterialTranslateKey())));
        }
        return Optional.empty();
    }

    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(Materialisation.MATERIALISING_TABLE_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> {
            return new MaterialisingTableScreen(new MaterialisingTableContainer(syncId, playerEntity.inventory), playerEntity.inventory, new TranslatableText("container.materialisation.materialising_table"));
        });
        ScreenProviderRegistry.INSTANCE.registerFactory(Materialisation.MATERIAL_PREPARER_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> {
            return new MaterialPreparerScreen(new MaterialPreparerContainer(syncId, playerEntity.inventory), playerEntity.inventory, new TranslatableText("container.materialisation.material_preparer"));
        });
        ClientSidePacketRegistry.INSTANCE.register(Materialisation.MATERIALISING_TABLE_PLAY_SOUND, (packetContext, packetByteBuf) -> {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_USE, 1, 1));
        });
        ColorProviderRegistryImpl.ITEM.register(MaterialisationUtils::getItemLayerColor, Materialisation.MATERIALISED_MEGAAXE, Materialisation.MEGAAXE_HEAD, Materialisation.MATERIALISED_PICKAXE, Materialisation.MATERIALISED_SHOVEL, Materialisation.MATERIALISED_AXE, Materialisation.MATERIALISED_SWORD, Materialisation.MATERIALISED_HAMMER, Materialisation.HAMMER_HEAD, Materialisation.HANDLE, Materialisation.SWORD_BLADE, Materialisation.SHOVEL_HEAD, Materialisation.PICKAXE_HEAD, Materialisation.AXE_HEAD);
    }
}

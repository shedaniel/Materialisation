package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.Materialisation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ColoredItem extends Item {
    
    public ColoredItem(Settings item$Settings_1) {
        super(item$Settings_1);
        addProperty(new Identifier(ModReference.MOD_ID, "bright"), (itemStack, world, livingEntity) -> {
            return itemStack.getOrCreateTag().containsKey("mt_bright") ? 1f : 0f;
        });
    }
    
    @Environment(EnvType.CLIENT)
    public static Optional<String> getItemTranslationKey(ItemStack stack) {
        if (stack.getOrCreateTag().containsKey("mt_name_key")) {
            return Optional.ofNullable(stack.getOrCreateTag().getString("mt_name_key"));
        } else if (stack.getOrCreateTag().containsKey("mt_material")) {
            if (stack.getItem() == Materialisation.HANDLE)
                return Optional.ofNullable(I18n.translate("item.materialisation.materialised_handle", I18n.translate("material.materialisation." + stack.getOrCreateTag().getString("mt_material"))));
            if (stack.getItem() == Materialisation.PICKAXE_HEAD)
                return Optional.ofNullable(I18n.translate("item.materialisation.materialised_pickaxe_head", I18n.translate("material.materialisation." + stack.getOrCreateTag().getString("mt_material"))));
        }
        return Optional.empty();
    }
    
    @Override
    public Component getTranslatedNameTrimmed(ItemStack itemStack_1) {
        return getItemTranslationKey(itemStack_1).map(s -> (Component) new TranslatableComponent(s)).orElse(super.getTranslatedNameTrimmed(itemStack_1));
    }
    
}

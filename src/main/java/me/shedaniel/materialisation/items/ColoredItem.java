package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.KnownMaterial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
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
            if (stack.getItem() == Materialisation.AXE_HEAD)
                return Optional.ofNullable(I18n.translate("item.materialisation.materialised_axe_head", I18n.translate("material.materialisation." + stack.getOrCreateTag().getString("mt_material"))));
            if (stack.getItem() == Materialisation.SHOVEL_HEAD)
                return Optional.ofNullable(I18n.translate("item.materialisation.materialised_shovel_head", I18n.translate("material.materialisation." + stack.getOrCreateTag().getString("mt_material"))));
            if (stack.getItem() == Materialisation.SWORD_BLADE)
                return Optional.ofNullable(I18n.translate("item.materialisation.materialised_sword_blade", I18n.translate("material.materialisation." + stack.getOrCreateTag().getString("mt_material"))));
        }
        return Optional.empty();
    }
    
    @Override
    public void buildTooltip(ItemStack stack, World world_1, List<Component> list, TooltipContext tooltipContext_1) {
        super.buildTooltip(stack, world_1, list, tooltipContext_1);
        if (stack.getOrCreateTag().containsKey("mt_material")) {
            KnownMaterial material = MaterialisationUtils.getMaterialFromPart(stack);
            if (material != null)
                if (stack.getItem() == Materialisation.HANDLE) {
                    list.add(new TranslatableComponent("text.materialisation.tool_handle_durability_multiplier", MaterialisationUtils.getColoring(material.getHandleDurabilityMultiplier()).toString() + "x" + material.getHandleDurabilityMultiplier()));
                    list.add(new TranslatableComponent("text.materialisation.tool_handle_speed_multiplier", MaterialisationUtils.getColoring(material.getHandleBreakingSpeedMultiplier()).toString() + "x" + material.getHandleBreakingSpeedMultiplier()));
                } else if (stack.getItem() == Materialisation.PICKAXE_HEAD || stack.getItem() == Materialisation.AXE_HEAD || stack.getItem() == Materialisation.SHOVEL_HEAD) {
                    list.add(new TranslatableComponent("text.materialisation.head_part_speed", ChatFormat.YELLOW.toString() + material.getPickaxeHeadSpeed()));
                    list.add(new TranslatableComponent("text.materialisation.head_part_durability", ChatFormat.YELLOW.toString() + material.getPickaxeHeadDurability()));
                }
        }
    }
    
    @Override
    public Component getTranslatedNameTrimmed(ItemStack itemStack_1) {
        return getItemTranslationKey(itemStack_1).map(s -> (Component) new TranslatableComponent(s)).orElse(super.getTranslatedNameTrimmed(itemStack_1));
    }
    
}

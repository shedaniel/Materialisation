package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.PartMaterial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ColoredItem extends Item {

    private static final ItemPropertyGetter COLORED_ITEM_BRIGHT_ITEM = (itemStack, world, livingEntity) -> MaterialisationUtils.isHandleBright(itemStack) ? 1 : 0;

    public ColoredItem(Settings item$Settings_1) {
        super(item$Settings_1);
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "bright"), COLORED_ITEM_BRIGHT_ITEM);
    }

    public static UUID getItemModifierDamage() {
        return ATTACK_DAMAGE_MODIFIER_UUID;
    }

    public static UUID getItemModifierSwingSpeed() {
        return ATTACK_SPEED_MODIFIER_UUID;
    }

    public static float getExtraDamage(Item item) {
        if (item == Materialisation.SWORD_BLADE)
            return 4f;
        if (item == Materialisation.PICKAXE_HEAD)
            return 2f;
        if (item == Materialisation.AXE_HEAD)
            return 7f;
        if (item == Materialisation.MEGAAXE_HEAD)
            return 10f;
        if (item == Materialisation.HAMMER_HEAD)
            return 9f;
        if (item == Materialisation.SHOVEL_HEAD)
            return 2.5f;
        return 0f;
    }

    private static double getBaseToolBreakingSpeed(ColoredItem item, PartMaterial material) {
        double speed = material.getToolSpeed();
        if (item == Materialisation.HAMMER_HEAD) speed /= 6f;
        if (item == Materialisation.MEGAAXE_HEAD) speed /= 6.5f;
        return speed;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world_1, List<Text> list, TooltipContext tooltipContext_1) {
        super.appendTooltip(stack, world_1, list, tooltipContext_1);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.containsKey("mt_0_material")) {
            PartMaterial material = MaterialisationUtils.getMaterialFromPart(stack);
            if (material != null) {
                if (this == Materialisation.HANDLE) {
                    list.add(new TranslatableText("text.materialisation.tool_handle_durability_multiplier", MaterialisationUtils.getColoring(material.getDurabilityMultiplier()).toString() + "x" + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getDurabilityMultiplier())));
                    list.add(new TranslatableText("text.materialisation.tool_handle_speed_multiplier", MaterialisationUtils.getColoring(material.getBreakingSpeedMultiplier()).toString() + "x" + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getBreakingSpeedMultiplier())));
                }
                if (this == Materialisation.PICKAXE_HEAD || this == Materialisation.AXE_HEAD || this == Materialisation.SHOVEL_HEAD || this == Materialisation.HAMMER_HEAD || this == Materialisation.MEGAAXE_HEAD || this == Materialisation.SWORD_BLADE) {
                    if (this != Materialisation.SWORD_BLADE)
                        list.add(new TranslatableText("text.materialisation.head_part_speed", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(getBaseToolBreakingSpeed(this, material))));
                    list.add(new TranslatableText("text.materialisation.head_part_durability", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolDurability())));
                    list.add(new TranslatableText("text.materialisation.head_part_damage", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(getExtraDamage(this) + material.getAttackDamage())));
                }
            }
        }
    }

    @Override
    public Text getName(ItemStack itemStack_1) {
        PartMaterial part = MaterialisationUtils.getMaterialFromPart(itemStack_1);
        if (part != null)
            return new TranslatableText("item.materialisation.materialised_" + Registry.ITEM.getId(this).getPath(), new TranslatableText(part.getMaterialTranslateKey()));
        return new TranslatableText(this.getTranslationKey(itemStack_1));
    }

}

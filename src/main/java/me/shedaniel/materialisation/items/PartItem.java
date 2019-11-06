package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.PartMaterial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class PartItem extends Item {
    public PartItem(Settings settings) {
        super(settings);
        addPropertyGetter(new Identifier(Materialisation.MOD_ID, "bright"), (itemStack, world, livingEntity) ->
                !itemStack.hasTag() ? 0f : itemStack.getTag().containsKey("mt_bright") ? 1f : 0f);
    }

    public static UUID getItemModifierDamage() {
        return ATTACK_DAMAGE_MODIFIER_UUID;
    }

    public static UUID getItemModifierSwingSpeed() {
        return ATTACK_SPEED_MODIFIER_UUID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world_1, List<Text> list, TooltipContext tooltipContext_1) {
        super.appendTooltip(stack, world_1, list, tooltipContext_1);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.containsKey("mt_0_material") || tag.containsKey("mt_material")) {
            PartMaterial material = MaterialisationUtils.getMaterialFromPart(stack);
            if (material != null)
                if (stack.getItem() == Materialisation.HANDLE) {
                    list.add(new TranslatableText("text.materialisation.tool_handle_durability_multiplier", MaterialisationUtils.getColoring(material.getDurabilityMultiplier()).toString() + "x" + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getDurabilityMultiplier())));
                    list.add(new TranslatableText("text.materialisation.tool_handle_speed_multiplier", MaterialisationUtils.getColoring(material.getBreakingSpeedMultiplier()).toString() + "x" + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getBreakingSpeedMultiplier())));
                } else if (stack.getItem() == Materialisation.PICKAXE_HEAD || stack.getItem() == Materialisation.AXE_HEAD || stack.getItem() == Materialisation.SHOVEL_HEAD) {
                    list.add(new TranslatableText("text.materialisation.head_part_speed", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolSpeed())));
                    list.add(new TranslatableText("text.materialisation.head_part_durability", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolDurability())));
                    list.add(new TranslatableText("text.materialisation.head_part_damage", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(getExtraDamage(stack.getItem()) + material.getAttackDamage())));
                } else if (stack.getItem() == Materialisation.SWORD_BLADE) {
                    list.add(new TranslatableText("text.materialisation.head_part_durability", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolDurability())));
                    list.add(new TranslatableText("text.materialisation.head_part_damage", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(getExtraDamage(stack.getItem()) + material.getAttackDamage())));
                } else if (stack.getItem() == Materialisation.HAMMER_HEAD) {
                    list.add(new TranslatableText("text.materialisation.head_part_speed", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolSpeed() / 6f)));
                    list.add(new TranslatableText("text.materialisation.head_part_durability", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolDurability())));
                    list.add(new TranslatableText("text.materialisation.head_part_damage", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(getExtraDamage(stack.getItem()) + material.getAttackDamage())));
                } else if (stack.getItem() == Materialisation.MEGAAXE_HEAD) {
                    list.add(new TranslatableText("text.materialisation.head_part_speed", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolSpeed() / 6.5f)));
                    list.add(new TranslatableText("text.materialisation.head_part_durability", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(material.getToolDurability())));
                    list.add(new TranslatableText("text.materialisation.head_part_damage", Formatting.YELLOW.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(getExtraDamage(stack.getItem()) + material.getAttackDamage())));
                }
        }
    }

    public float getExtraDamage(Item item) {
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

    @Override
    @Environment(EnvType.CLIENT)
    public Text getName(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.containsKey("mt_0_material")) {
            return super.getName(stack);
        }

        PartMaterial material = MaterialisationUtils.getMaterialFromPart(stack);
        if (material == null) {
            return super.getName(stack);
        }

        String itemKey = stack.getItem().getTranslationKey();
        String materialKey = material.getMaterialTranslateKey();
        String materialName = I18n.translate(materialKey);
        if (materialName == null || materialName.equals(materialKey)) {
            return new LiteralText(I18n.translate(itemKey, I18n.translate("material.materialisation.unknown")));
        }

        return new LiteralText(I18n.translate(itemKey, materialName));
    }

}

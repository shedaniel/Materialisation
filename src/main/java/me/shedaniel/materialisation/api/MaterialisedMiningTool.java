package me.shedaniel.materialisation.api;

import com.sun.istack.internal.NotNull;
import me.shedaniel.materialisation.Materialisation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static me.shedaniel.materialisation.MaterialisationUtils.*;

public interface MaterialisedMiningTool {
    static float getExtraDamage(ToolType toolType) {
        if (toolType == ToolType.SWORD)
            return 4f;
        if (toolType == ToolType.PICKAXE)
            return 2f;
        if (toolType == ToolType.AXE)
            return 7f;
        if (toolType == ToolType.MEGA_AXE)
            return 10f;
        if (toolType == ToolType.HAMMER)
            return 9f;
        if (toolType == ToolType.SHOVEL)
            return 2.5f;
        return 0f;
    }

    default void init() {
        ((Item) this).addPropertyGetter(new Identifier(Materialisation.MOD_ID, "handle_isbright"),
                (itemStack, world, livingEntity) -> isHandleBright(itemStack) ? 1f : 0f);
        ((Item) this).addPropertyGetter(new Identifier(Materialisation.MOD_ID, "tool_head_isbright"),
                (itemStack, world, livingEntity) -> isHeadBright(itemStack) ? 1f : 0f);
    }

    @NotNull
    default ToolType getToolType() {
        return ToolType.UNKNOWN;
    }

    float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state);

    double getAttackSpeed();

    boolean canEffectivelyBreak(ItemStack itemStack, BlockState state);

    default boolean postHit(ItemStack stack, LivingEntity attacker, LivingEntity target) {
        if (!attacker.world.isClient && (!(attacker instanceof PlayerEntity) || !((PlayerEntity) attacker).abilities.creativeMode))
            if (getToolDurability(stack) > 0)
                if (applyDamage(stack, 2, attacker.getRand())) {
                    attacker.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    stack.decrement(1);
                    if (attacker instanceof PlayerEntity) {
                        ((PlayerEntity) attacker).incrementStat(Stats.BROKEN.getOrCreateStat(stack.getItem()));
                    }
                    setToolDurability(stack, 0);
                }
        return true;
    }

    default boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F)
            if (!miner.world.isClient && (!(miner instanceof PlayerEntity) || !((PlayerEntity) miner).abilities.creativeMode))
                if (getToolDurability(stack) > 0)
                    if (applyDamage(stack, 2, miner.getRand())) {
                        miner.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        stack.decrement(1);
                        if (miner instanceof PlayerEntity) {
                            ((PlayerEntity) miner).incrementStat(Stats.BROKEN.getOrCreateStat(stack.getItem()));
                        }
                        setToolDurability(stack, 0);
                    }
        return true;
    }

    default int getEnchantability(ItemStack stack) {
        int enchantability = 0;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.containsKey("mt_0_material") && tag.containsKey("mt_1_material")) {
            PartMaterial handle = getMaterialFromString(tag.getString("mt_0_material"));
            PartMaterial head = getMaterialFromString(tag.getString("mt_1_material"));
            enchantability = (handle.getEnchantability() + head.getEnchantability()) / 2;
        }
        return enchantability;
    }

    @Environment(EnvType.CLIENT)
    default void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        int toolDurability = getToolDurability(stack);
        int maxDurability = getToolMaxDurability(stack);
        tooltip.add(new TranslatableText("text.materialisation.max_durability", maxDurability));

        if (toolDurability == 0) {
            tooltip.add(new TranslatableText("text.materialisation.broken"));
        } else {
            float percentage = toolDurability / (float) maxDurability * 100;
            Formatting coloring = getDurabilityColoring(percentage);
            tooltip.add(new TranslatableText("text.materialisation.durability",
                    coloring.toString() + toolDurability,
                    coloring.toString() + TWO_DECIMAL_FORMATTER.format(percentage) + Formatting.WHITE.toString()));
        }

        tooltip.add(new TranslatableText("text.materialisation.breaking_speed",
                TWO_DECIMAL_FORMATTER.format(getToolBreakingSpeed(stack))));
        tooltip.add(new TranslatableText("text.materialisation.mining_level",
                getToolMiningLevel(stack)));
    }
}

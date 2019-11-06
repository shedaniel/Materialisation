package me.shedaniel.materialisation.items;

import com.google.common.collect.Sets;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;

public class MaterialisedAxeItem extends AxeItem implements MaterialisedMiningTool {

    private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
            Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS,
            Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS,
            Blocks.BOOKSHELF, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD,
            Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD,
            Blocks.DARK_OAK_WOOD, Blocks.OAK_LOG, Blocks.SPRUCE_LOG,
            Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG,
            Blocks.DARK_OAK_LOG, Blocks.CHEST, Blocks.PUMPKIN,
            Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON,
            Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON,
            Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON,
            Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.OAK_PRESSURE_PLATE,
            Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE,
            Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);

    public MaterialisedAxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.1F, settings.maxDamage(0));
        this.init();
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack stack, BlockState state) {
        Material material_1 = state.getMaterial();
        return material_1 != Material.WOOD && material_1 != Material.PLANT && material_1 != Material.REPLACEABLE_PLANT && material_1 != Material.BAMBOO ? (((MiningToolItemAccessor) stack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisationUtils.getToolBreakingSpeed(stack) : 1.0F) : MaterialisationUtils.getToolBreakingSpeed(stack);
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack itemStack, BlockState state) {
        return EFFECTIVE_BLOCKS.contains(state.getBlock());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();
        BlockState state = world.getBlockState(pos);
        Block block = STRIPPED_BLOCKS.get(state.getBlock());
        if (MaterialisationUtils.getToolDurability(stack) > 0 && block != null) {
            PlayerEntity player = context.getPlayer();
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient) {
                world.setBlockState(pos, block.getDefaultState().with(PillarBlock.AXIS, state.get(PillarBlock.AXIS)), 11);
                if (player != null) {
                    if (!player.world.isClient && !(player.abilities.creativeMode))
                        if (MaterialisationUtils.applyDamage(stack, 1, player.getRand())) {
                            player.sendToolBreakStatus(context.getHand());
                            stack.decrement(1);
                            player.incrementStat(Stats.BROKEN.getOrCreateStat(stack.getItem()));
                            MaterialisationUtils.setToolDurability(stack, 0);
                        }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return MaterialisedMiningTool.super.postHit(stack, target, attacker);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return MaterialisedMiningTool.super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public int getEnchantability(ItemStack stack) {
        return MaterialisedMiningTool.super.getEnchantability(stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        MaterialisedMiningTool.super.appendTooltip(stack, world, tooltip, context);
    }
}

package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class MaterialisedShovelItem extends ShovelItem implements MaterialisedMiningTool {

    public MaterialisedShovelItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.0F, settings.maxDamage(0));
        this.init();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack itemStack = context.getStack();
        if (MaterialisationUtils.getToolDurability(itemStack) > 0)
            if (context.getSide() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
                BlockState blockState = PATH_BLOCKSTATES.get(world.getBlockState(blockPos).getBlock());
                if (blockState != null) {
                    PlayerEntity playerEntity_1 = context.getPlayer();
                    world.playSound(playerEntity_1, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    if (!world.isClient) {
                        world.setBlockState(blockPos, blockState, 11);
                        if (playerEntity_1 != null)
                            if (!playerEntity_1.world.isClient && (!(playerEntity_1 instanceof PlayerEntity) || !(playerEntity_1.abilities.creativeMode)))
                                if (MaterialisationUtils.applyDamage(itemStack, 1, playerEntity_1.getRand())) {
                                    playerEntity_1.sendToolBreakStatus(context.getHand());
                                    Item item_1 = itemStack.getItem();
                                    itemStack.decrement(1);
                                    playerEntity_1.incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                                    MaterialisationUtils.setToolDurability(itemStack, 0);
                                }
                    }
                    return ActionResult.SUCCESS;
                }
            }
        return ActionResult.PASS;
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack itemStack, BlockState state) {
        return ((MiningToolItemAccessor) itemStack.getItem()).getEffectiveBlocks().contains(state.getBlock()) || isEffectiveOn(state);
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state) {
        return ((MiningToolItemAccessor) itemStack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisationUtils.getToolBreakingSpeed(itemStack) : 1f;
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

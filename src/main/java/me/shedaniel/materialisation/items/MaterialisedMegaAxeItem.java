package me.shedaniel.materialisation.items;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.ToolType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MaterialisedMegaAxeItem extends AxeItem implements MaterialisedMiningTool {
    
    public MaterialisedMegaAxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.65F, settings.maxDamage(0));
        initProperty();
    }
    
    @Override
    public boolean canMine(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient)
            return true;
        ItemStack mainHandStack = player.getMainHandStack();
        if (player.isSneaking() || MaterialisationUtils.getToolDurability(mainHandStack) <= 0 || !isLogs(blockState))
            return true;
        Block log = blockState.getBlock();
        AtomicReference<Block> leaves = new AtomicReference<>(null);
        List<BlockPos> posList = Lists.newArrayList();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    BlockPos add = pos.add(x, y, z);
                    if (x != 0 || y != 0 || z != 0)
                        tryBreak(posList, world, add, player, mainHandStack, pos, log, leaves);
                }
        return true;
    }
    
    public void tryBreak(List<BlockPos> posList, World world, BlockPos blockPos, PlayerEntity player, ItemStack stack, BlockPos ogPos, Block log, AtomicReference<Block> leaves) {
        if (posList.contains(blockPos) || blockPos.getManhattanDistance(ogPos) > 14 || MaterialisationUtils.getToolDurability(stack) <= 0)
            return;
        posList.add(blockPos);
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        if (Objects.isNull(leaves.get()) && isLeaves(state))
            leaves.set(block);
        boolean equalsLog = block.equals(log);
        if (!equalsLog && !block.equals(leaves.get()))
            return;
        if (equalsLog && stack.isEffectiveOn(state)) {
            world.breakBlock(blockPos, !player.isCreative());
            takeDamage(world, state, blockPos, player, stack);
        }
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    BlockPos add = blockPos.add(x, y, z);
                    if (x != 0 || y != 0 || z != 0)
                        tryBreak(posList, world, add, player, stack, ogPos, log, leaves);
                }
    }
    
    private void takeDamage(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity, ItemStack stack) {
        if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F)
            if (!playerEntity.world.isClient && (!(playerEntity instanceof PlayerEntity) || !playerEntity.abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    MaterialisationUtils.applyDamage(stack, 1, playerEntity.getRandom());
    }
    
    private boolean isLogs(BlockState state) {
        return BlockTags.LOGS.contains(state.getBlock());
    }
    
    private boolean isLeaves(BlockState state) {
        return BlockTags.LEAVES.contains(state.getBlock());
    }
    
    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }
    
    @Nonnull
    @Override
    public ToolType getToolType() {
        return ToolType.MEGA_AXE;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack itemStack = context.getStack();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = STRIPPED_BLOCKS.get(blockState.getBlock());
        if (MaterialisationUtils.getToolDurability(itemStack) > 0 && block != null) {
            PlayerEntity playerEntity_1 = context.getPlayer();
            world.playSound(playerEntity_1, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient) {
                world.setBlockState(blockPos, block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
                if (playerEntity_1 != null) {
                    if (!playerEntity_1.world.isClient && (!(playerEntity_1 instanceof PlayerEntity) || !(playerEntity_1.abilities.creativeMode)))
                        if (MaterialisationUtils.applyDamage(itemStack, 1, playerEntity_1.getRandom())) {
                            playerEntity_1.sendToolBreakStatus(context.getHand());
                            Item item_1 = itemStack.getItem();
                            itemStack.decrement(1);
                            playerEntity_1.incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                            MaterialisationUtils.setToolDurability(itemStack, 0);
                        }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    
    @Override
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }
    
    @Override
    public boolean postHit(ItemStack stack, LivingEntity livingEntity_1, LivingEntity livingEntity_2) {
        if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
            if (MaterialisationUtils.getToolDurability(stack) > 0)
                if (MaterialisationUtils.applyDamage(stack, 2, livingEntity_1.getRandom())) {
                    livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    Item item_1 = stack.getItem();
                    stack.decrement(1);
                    if (livingEntity_1 instanceof PlayerEntity) {
                        ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                    }
                    MaterialisationUtils.setToolDurability(stack, 0);
                }
        return true;
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world_1, BlockState blockState_1, BlockPos blockPos_1, LivingEntity livingEntity_1) {
        if (!world_1.isClient && blockState_1.getHardness(world_1, blockPos_1) != 0.0F)
            if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    if (MaterialisationUtils.applyDamage(stack, 1, livingEntity_1.getRandom())) {
                        livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        Item item_1 = stack.getItem();
                        stack.decrement(1);
                        if (livingEntity_1 instanceof PlayerEntity) {
                            ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                        }
                        MaterialisationUtils.setToolDurability(stack, 0);
                    }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        MaterialisationUtils.appendToolTooltip(stack, this, world_1, list_1, tooltipContext_1);
    }
    
}

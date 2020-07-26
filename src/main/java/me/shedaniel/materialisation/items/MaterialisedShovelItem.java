package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.ToolType;
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

import javax.annotation.Nonnull;
import java.util.List;

public class MaterialisedShovelItem extends ShovelItem implements MaterialisedMiningTool {
    
    public MaterialisedShovelItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.0F, settings.maxDamage(0));
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return MaterialisationUtils.getToolDurability(stack) <= 0 ? -1 : super.getMiningSpeed(stack, state);
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
                                if (MaterialisationUtils.applyDamage(itemStack, 1, playerEntity_1.getRandom())) {
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
    
    @Nonnull
    @Override
    public ToolType getToolType() {
        return ToolType.SHOVEL;
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

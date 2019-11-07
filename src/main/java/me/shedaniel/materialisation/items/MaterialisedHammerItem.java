package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.ToolType;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import java.util.List;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;
import static me.shedaniel.materialisation.MaterialisationUtils.isHeadBright;
import static net.minecraft.util.math.Direction.Axis.*;

public class MaterialisedHammerItem extends PickaxeItem implements MaterialisedMiningTool {

    public MaterialisedHammerItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.6F, settings.maxDamage(0));
        initProperty();
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack stack, BlockState state) {
        Block block_1 = state.getBlock();
        int int_1 = MaterialisationUtils.getToolMiningLevel(stack);
        if (block_1 == Blocks.OBSIDIAN) {
            return int_1 >= 3;
        } else if (block_1 != Blocks.DIAMOND_BLOCK && block_1 != Blocks.DIAMOND_ORE && block_1 != Blocks.EMERALD_ORE && block_1 != Blocks.EMERALD_BLOCK && block_1 != Blocks.GOLD_BLOCK && block_1 != Blocks.GOLD_ORE && block_1 != Blocks.REDSTONE_ORE) {
            if (block_1 != Blocks.IRON_BLOCK && block_1 != Blocks.IRON_ORE && block_1 != Blocks.LAPIS_BLOCK && block_1 != Blocks.LAPIS_ORE) {
                Material material_1 = state.getMaterial();
                return material_1 == Material.STONE || material_1 == Material.METAL || material_1 == Material.ANVIL;
            } else
                return int_1 >= 1;
        } else {
            return int_1 >= 2;
        }
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        return material != Material.METAL && material != Material.ANVIL && material != Material.STONE ? (((MiningToolItemAccessor) stack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisationUtils.getToolBreakingSpeed(stack) : 1.0F) : MaterialisationUtils.getToolBreakingSpeed(stack);
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public ToolType getToolType() {
        return ToolType.HAMMER;
    }

    @Override
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity livingEntity_1, LivingEntity livingEntity_2) {
        if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
            if (MaterialisationUtils.getToolDurability(stack) > 0)
                if (MaterialisationUtils.applyDamage(stack, 2, livingEntity_1.getRand())) {
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
                    if (MaterialisationUtils.applyDamage(stack, 1, livingEntity_1.getRand())) {
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
    public boolean canMine(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient)
            return true;
        ItemStack mainHandStack = player.getMainHandStack();
        if (player.isSneaking() || MaterialisationUtils.getToolDurability(mainHandStack) <= 0 || !canBreak(mainHandStack, blockState))
            return true;
        // Taken from Entity#rayTrace
        Vec3d vec3d_1 = player.getCameraPosVec(1);
        Vec3d vec3d_2 = player.getRotationVec(1);
        int range = 4;
        Vec3d vec3d_3 = vec3d_1.add(vec3d_2.x * range, vec3d_2.y * range, vec3d_2.z * range);
        BlockHitResult hitResult = world.rayTrace(new RayTraceContext(vec3d_1, vec3d_3, RayTraceContext.ShapeType.OUTLINE, true ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, player));
        Direction.Axis axis = hitResult.getSide().getAxis();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (MaterialisationUtils.getToolDurability(mainHandStack) <= 0)
                        return true;
                    BlockPos newPos = new BlockPos(axis == X ? pos.getX() : pos.getX() + i, axis == X ? pos.getY() + i : axis == Y ? pos.getY() : pos.getY() + j, axis != Z ? pos.getZ() + j : pos.getZ());
                    BlockState newState = world.getBlockState(newPos);
                    boolean canBreak = newState.getHardness(world, newPos) >= 0 && canBreak(mainHandStack, newState);
                    if (!canBreak)
                        continue;
                    // Let's break the block!
                    breakBlock(world, newState, newPos, !player.isCreative(), player, mainHandStack);
                    takeDamage(world, blockState, newPos, player, mainHandStack);
                }
            }
        return true;
    }

    public boolean breakBlock(World world, BlockState blockState_1, BlockPos blockPos_1, boolean boolean_1, Entity entity_1, ItemStack itemStack_1) {
        if (blockState_1.isAir()) {
            return false;
        } else {
            FluidState fluidState_1 = world.getFluidState(blockPos_1);
            world.playLevelEvent(2001, blockPos_1, Block.getRawIdFromState(blockState_1));
            if (boolean_1) {
                BlockEntity blockEntity_1 = blockState_1.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos_1) : null;
                Block.dropStacks(blockState_1, world, blockPos_1, blockEntity_1, entity_1, itemStack_1);
            }
            return world.setBlockState(blockPos_1, fluidState_1.getBlockState(), 3);
        }
    }

    private boolean canBreak(ItemStack stack, BlockState state) {
        TriState triState = MaterialisationUtils.mt_handleIsEffectiveOn(stack, state);
        if (triState != TriState.DEFAULT) {
            // If we are dealing with 3rd party blocks
            return triState.get();
        } else {
            // Lastly if we are not dealing with 3rd party blocks with durability left
            return ((MaterialisedHammerItem) stack.getItem()).canEffectivelyBreak(stack, state);
        }
    }

    private void takeDamage(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity, ItemStack stack) {
        if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F)
            if (!playerEntity.world.isClient && (!(playerEntity instanceof PlayerEntity) || !playerEntity.abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    MaterialisationUtils.applyDamage(stack, 1, playerEntity.getRand());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        MaterialisationUtils.appendToolTooltip(stack, this, world_1, list_1, tooltipContext_1);
    }

}

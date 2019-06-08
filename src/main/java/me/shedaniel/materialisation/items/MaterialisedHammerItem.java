package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.KnownMaterial;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.util.math.Direction.Axis.*;

public class MaterialisedHammerItem extends PickaxeItem implements MaterialisedMiningTool {
    
    public MaterialisedHammerItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.6F, settings.durability(0));
        addProperty(new Identifier(ModReference.MOD_ID, "handle_isbright"), (itemStack, world, livingEntity) -> {
            return itemStack.getOrCreateTag().containsKey("mt_handle_bright") ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "handle_isnotbright"), (itemStack, world, livingEntity) -> {
            return !itemStack.getOrCreateTag().containsKey("mt_handle_bright") ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "hammer_head_isbright"), (itemStack, world, livingEntity) -> {
            return itemStack.getOrCreateTag().containsKey("mt_hammer_head_bright") ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "hammer_head_isnotbright"), (itemStack, world, livingEntity) -> {
            return !itemStack.getOrCreateTag().containsKey("mt_hammer_head_bright") ? 1f : 0f;
        });
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
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }
    
    @Override
    public boolean onEntityDamaged(ItemStack stack, LivingEntity livingEntity_1, LivingEntity livingEntity_2) {
        if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
            if (MaterialisationUtils.getToolDurability(stack) > 0)
                if (MaterialisationUtils.applyDamage(stack, 2, livingEntity_1.getRand())) {
                    livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    Item item_1 = stack.getItem();
                    stack.subtractAmount(1);
                    if (livingEntity_1 instanceof PlayerEntity) {
                        ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                    }
                    MaterialisationUtils.setToolDurability(stack, 0);
                }
        return true;
    }
    
    
    @Override
    public boolean onBlockBroken(ItemStack stack, World world_1, BlockState blockState_1, BlockPos blockPos_1, LivingEntity livingEntity_1) {
        if (!world_1.isClient && blockState_1.getHardness(world_1, blockPos_1) != 0.0F)
            if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    if (MaterialisationUtils.applyDamage(stack, 1, livingEntity_1.getRand())) {
                        livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        Item item_1 = stack.getItem();
                        stack.subtractAmount(1);
                        if (livingEntity_1 instanceof PlayerEntity) {
                            ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                        }
                        MaterialisationUtils.setToolDurability(stack, 0);
                    }
        return true;
    }
    
    @Override
    public int getEnchantability(ItemStack stack) {
        if (!stack.getOrCreateTag().containsKey("mt_hammer_head_material") || !stack.getOrCreateTag().containsKey("mt_handle_material"))
            return 0;
        KnownMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_handle_material"));
        KnownMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_hammer_head_material"));
        return (handle.getEnchantability() + head.getEnchantability()) / 2;
    }
    
    @Override
    public boolean beforeBlockBreak(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
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
        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    if (MaterialisationUtils.getToolDurability(mainHandStack) <= 0)
                        return true;
                    BlockPos newPos = new BlockPos(axis == X ? pos.getX() : pos.getX() + i, axis == X ? pos.getY() + i : axis == Y ? pos.getY() : pos.getY() + j, axis != Z ? pos.getZ() + j : pos.getZ());
                    BlockState newState = world.getBlockState(newPos);
                    boolean canBreak = canBreak(mainHandStack, newState);
                    if (!canBreak)
                        continue;
                    // Let's break the block!
                    world.breakBlock(newPos, !player.isCreative());
                    takeDamage(world, blockState, newPos, player, mainHandStack);
                }
            }
        return true;
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
    public void buildTooltip(ItemStack stack, World world_1, List<Component> list_1, TooltipContext tooltipContext_1) {
        int toolDurability = MaterialisationUtils.getToolDurability(stack);
        int maxDurability = MaterialisationUtils.getToolMaxDurability(stack);
        list_1.add(new TranslatableComponent("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0) {
            float percentage = toolDurability / (float) maxDurability * 100;
            ChatFormat coloringPercentage = MaterialisationUtils.getColoringPercentage(percentage);
            list_1.add(new TranslatableComponent("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(percentage) + ChatFormat.WHITE.toString()));
        } else
            list_1.add(new TranslatableComponent("text.materialisation.broken"));
        list_1.add(new TranslatableComponent("text.materialisation.breaking_speed", MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(MaterialisationUtils.getToolBreakingSpeed(stack))));
        list_1.add(new TranslatableComponent("text.materialisation.mining_level", MaterialisationUtils.getToolMiningLevel(stack)));
    }
    
}

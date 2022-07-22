package me.shedaniel.materialisation.mixin;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedHammerItem;
import me.shedaniel.materialisation.items.MaterialisedMegaAxeItem;
import me.shedaniel.materialisation.utils.AppendedObjectIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.util.math.Direction.Axis.*;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class MixinWorldRenderer {
    
    @Shadow private ClientWorld world;
    
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions;
    
    @Shadow @Final private MinecraftClient client;
    
    @ModifyVariable(method = "render",
                    at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;iterator()Lit/unimi/dsi/fastutil/objects/ObjectIterator;",
                             shift = At.Shift.BY, by = 2), ordinal = 0)
    private ObjectIterator<Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>>> appendBlockBreakingProgressions(ObjectIterator<Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>>> originalIterator) {
        return new AppendedObjectIterator<>(originalIterator, getCurrentExtraBreakingInfos());
    }
    
    @Unique
    private Long2ObjectMap<BlockBreakingInfo> getCurrentExtraBreakingInfos() {
        ItemStack heldStack = this.client.player.getInventory().getMainHandStack();
        if (heldStack.getItem() instanceof MaterialisedHammerItem || heldStack.getItem() instanceof MaterialisedMegaAxeItem) {
            if (!client.player.isSneaking()) {
                HitResult crosshairTarget = client.crosshairTarget;
                if (crosshairTarget instanceof BlockHitResult) {
                    BlockPos crosshairPos = ((BlockHitResult) crosshairTarget).getBlockPos();
                    BlockState crosshairState = world.getBlockState(crosshairPos);
                    if (heldStack.isSuitableFor(crosshairState) || (!crosshairState.isToolRequired() && heldStack.getMiningSpeedMultiplier(crosshairState) > 1)) {
                        SortedSet<BlockBreakingInfo> infos = this.blockBreakingProgressions.get(crosshairPos.asLong());
                        if (infos != null && !infos.isEmpty()) {
                            BlockBreakingInfo breakingInfo = infos.last();
                            int stage = breakingInfo.getStage();
                            
                            List<BlockPos> positions = heldStack.getItem() instanceof MaterialisedHammerItem ? getHammerPos((BlockHitResult) crosshairTarget, crosshairPos) : getMegaAxePos((BlockHitResult) crosshairTarget, crosshairPos);
                            Long2ObjectMap<BlockBreakingInfo> map = new Long2ObjectLinkedOpenHashMap<>(positions.size());
                            for (BlockPos position : positions) {
                                BlockState state = world.getBlockState(position);
                                if (heldStack.isSuitableFor(state) || (!state.isToolRequired() && heldStack.getMiningSpeedMultiplier(state) > 1)) {
                                    BlockBreakingInfo info = new BlockBreakingInfo(breakingInfo.hashCode(), position);
                                    info.setStage(stage);
                                    map.put(position.asLong(), info);
                                }
                            }
                            return map;
                        }
                    }
                }
            }
        }
        
        return Long2ObjectMaps.emptyMap();
    }
    
    @Unique
    private List<BlockPos> getHammerPos(BlockHitResult crosshairTarget, BlockPos crosshairPos) {
        List<BlockPos> positions = new ArrayList<>();
        Direction.Axis axis = crosshairTarget.getSide().getAxis();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    positions.add(new BlockPos(axis == X ? crosshairPos.getX() : crosshairPos.getX() + i, axis == X ? crosshairPos.getY() + i : axis == Y ? crosshairPos.getY() : crosshairPos.getY() + j, axis != Z ? crosshairPos.getZ() + j : crosshairPos.getZ()));
                }
            }
        return positions;
    }
    
    @Unique
    private List<BlockPos> getMegaAxePos(BlockHitResult crosshairTarget, BlockPos crosshairPos) {
        List<BlockPos> positions = new ArrayList<>();
        Direction.Axis axis = crosshairTarget.getSide().getAxis();
        Block log = world.getBlockState(crosshairPos).getBlock();
        if (BlockTags.LOGS != TagKey.of(Registry.BLOCK_KEY, Registry.BLOCK.getId(log))) return Collections.emptyList();
        LongSet posList = new LongOpenHashSet();
        AtomicReference<Block> leaves = new AtomicReference<>(null);
        for (int x = -1; x <= 1; x++)
            for (int y = 0; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    BlockPos add = crosshairPos.add(x, y, z);
                    if (x != 0 || y != 0 || z != 0)
                        tryBreak(positions, posList, world, add, client.player, client.player.getMainHandStack(), crosshairPos, log, leaves, 0);
                }
        return positions;
    }
    
    @Unique
    public void tryBreak(List<BlockPos> positions, LongSet posList, World world, BlockPos blockPos, PlayerEntity player, ItemStack stack, BlockPos ogPos, Block log, AtomicReference<Block> leaves, int leavesDistance) {
        long posLong = blockPos.asLong();
        if (posList.contains(posLong) || blockPos.getManhattanDistance(ogPos) > 14 || MaterialisationUtils.getToolDurability(stack) <= 0 || leavesDistance > 3)
            return;
        posList.add(posLong);
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        if (Objects.isNull(leaves.get()) && isLeaves(state))
            leaves.set(block);
        boolean equalsLog = block.equals(log);
        boolean equalsLeaves = block.equals(leaves.get());
        if (!equalsLog && !equalsLeaves)
            return;
        if (equalsLog && (stack.isSuitableFor(state) || (!state.isToolRequired() && stack.getMiningSpeedMultiplier(state) > 1))) {
            positions.add(blockPos);
        }
        for (int x = -1; x <= 1; x++)
            for (int y = 0; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    BlockPos add = blockPos.add(x, y, z);
                    if (x != 0 || y != 0 || z != 0)
                        tryBreak(positions, posList, world, add, player, stack, ogPos, log, leaves, equalsLeaves ? leavesDistance + 1 : Math.max(0, leavesDistance - 2));
                }
    }
    
    @Unique
    private boolean isLeaves(BlockState state) {
        return BlockTags.LEAVES == TagKey.of(Registry.BLOCK_KEY, Registry.BLOCK.getId(state.getBlock()));
    }
}
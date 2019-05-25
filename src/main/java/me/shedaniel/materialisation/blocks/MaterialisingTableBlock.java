package me.shedaniel.materialisation.blocks;

import me.shedaniel.materialisation.Materialisation;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MaterialisingTableBlock extends HorizontalFacingBlock {
    
    public MaterialisingTableBlock() {
        super(FabricBlockSettings.copy(Blocks.CRAFTING_TABLE).build());
        this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext placementContext) {
        return getDefaultState().with(FACING, placementContext.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean activate(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (!world.isClient)
            ContainerProviderRegistry.INSTANCE.openContainer(Materialisation.MATERIALISING_TABLE_CONTAINER, player, buf -> buf.writeBlockPos(pos));
        return true;
    }
    
}

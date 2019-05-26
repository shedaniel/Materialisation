package me.shedaniel.materialisation.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.Direction;

public class MaterialPreparerBlock extends HorizontalFacingBlock {
    
    public MaterialPreparerBlock() {
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
    
}

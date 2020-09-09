package me.shedaniel.materialisation.blocks;

import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.gui.MaterialPreparerScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MaterialPreparerBlock extends HorizontalFacingBlock implements NamedScreenHandlerFactory {
    private static final Text TITLE = new TranslatableText("block.materialisation.material_preparer");
    private static final VoxelShape SHAPE;
    
    static {
        VoxelShape base = Block.createCuboidShape(0, 14, 0, 16, 16, 16);
        SHAPE = VoxelShapes.union(base, Block.createCuboidShape(0, 0, 0, 2, 14, 2), Block.createCuboidShape(0, 0, 14, 2, 14, 16), Block.createCuboidShape(14, 0, 14, 16, 14, 16), Block.createCuboidShape(14, 0, 0, 16, 14, 2));
    }
    
    public MaterialPreparerBlock() {
        super(FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.WOOD).burnable().build()).strength(3, 3).drops(new Identifier(ModReference.MOD_ID, "blocks/material_preparer")).sounds(BlockSoundGroup.WOOD));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext placementContext) {
        return getDefaultState().with(FACING, placementContext.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_LOOM);
            return ActionResult.CONSUME;
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
            return new MaterialPreparerScreenHandler(i, playerInventory, ScreenHandlerContext.create(world, pos));
        }, TITLE);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean hasSidedTransparency(BlockState blockState_1) {
        return true;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean canPathfindThrough(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, NavigationType type) {
        return false;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new MaterialPreparerScreenHandler(syncId, inv);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getDefaultState().getBlock().getTranslationKey());
    }
}

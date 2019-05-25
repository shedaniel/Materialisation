package me.shedaniel.materialisedtools.containers;

import me.shedaniel.materialisedtools.MaterialisedTools;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class MaterialisingTableContainer extends Container {
    
    private BlockContext context;
    
    public MaterialisingTableContainer(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, BlockContext.EMPTY);
    }
    
    public MaterialisingTableContainer(int syncId, PlayerInventory inventory, final BlockContext context) {
        super(null, syncId);
        this.context = context;
    }
    
    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return this.context.run((world, blockPos) -> {
            return world.getBlockState(blockPos).getBlock() != MaterialisedTools.MATERIALISING_TABLE ? false : playerEntity.squaredDistanceTo(blockPos.getX() + .5D, blockPos.getY() + .5D, blockPos.getZ() + .5D) < 64D;
        }, true);
    }
    
}

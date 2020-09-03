package me.shedaniel.materialisation.gui;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import javax.annotation.Nullable;

public abstract class AbstractMaterialisingBase extends ScreenHandler {
  protected final CraftingResultInventory output = new CraftingResultInventory();
  protected final Inventory input = new SimpleInventory(2) {
    public void markDirty() {
      super.markDirty();
      AbstractMaterialisingBase.this.onContentChanged(this);
    }
  };

  protected final ScreenHandlerContext context;
  protected final PlayerEntity player;

  protected abstract boolean canTakeOutput(PlayerEntity player, boolean present);

  protected abstract ItemStack onTakeOutput(PlayerEntity player, ItemStack stack);

  protected abstract boolean canUse(BlockState state);

  public AbstractMaterialisingBase(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
    super(type, syncId);
    this.context = context;
    this.player = playerInventory.player;
  }

  public abstract void updateResult();

  public void onContentChanged(Inventory inventory) {
    super.onContentChanged(inventory);
    if (inventory == this.input) {
      this.updateResult();
    }
  }


  public void close(PlayerEntity player) {
    super.close(player);
    this.context.run((world, blockPos) -> {
      this.dropInventory(player, world, this.input);
    });
  }


  public boolean canUse(PlayerEntity player) {
    return (Boolean)this.context.run((world, blockPos) -> {
      return this.canUse(world.getBlockState(blockPos)) && player.squaredDistanceTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D) <= 64.0D;
    }, true);
  }

  protected boolean returnFalse(ItemStack itemStack) {
    return false;
  }

  public ItemStack transferSlot(PlayerEntity player, int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (slot != null && slot.hasStack()) {
      ItemStack itemStack2 = slot.getStack();
      itemStack = itemStack2.copy();
      if (index == 2) {
        if (!this.insertItem(itemStack2, 3, 39, true)) {
          return ItemStack.EMPTY;
        }

        slot.onStackChanged(itemStack2, itemStack);
      } else if (index != 0 && index != 1) {
        if (index < 39) {
          int i = this.returnFalse(itemStack) ? 1 : 0;
          if (!this.insertItem(itemStack2, i, 2, false)) {
            return ItemStack.EMPTY;
          }
        }
      } else if (!this.insertItem(itemStack2, 3, 39, false)) {
        return ItemStack.EMPTY;
      }

      if (itemStack2.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }

      if (itemStack2.getCount() == itemStack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTakeItem(player, itemStack2);
    }

    return itemStack;
  }
}

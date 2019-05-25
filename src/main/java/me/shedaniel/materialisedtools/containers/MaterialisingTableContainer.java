package me.shedaniel.materialisedtools.containers;

import me.shedaniel.materialisedtools.MaterialisedTools;
import me.shedaniel.materialisedtools.api.KnownMaterial;
import me.shedaniel.materialisedtools.items.MaterialisedPickaxeItem;
import me.shedaniel.materialisedtools.items.MaterialisedToolUtils;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;

public class MaterialisingTableContainer extends Container {
    
    private final Inventory main, result;
    private final PlayerEntity player;
    private BlockContext context;
    private String itemName;
    
    public MaterialisingTableContainer(int syncId, PlayerInventory main) {
        this(syncId, main, BlockContext.EMPTY);
    }
    
    public MaterialisingTableContainer(int syncId, PlayerInventory playerInventory, final BlockContext context) {
        super(null, syncId);
        this.context = context;
        this.result = new CraftingResultInventory();
        this.main = new BasicInventory(2) {
            public void markDirty() {
                super.markDirty();
                onContentChanged(this);
            }
        };
        this.player = playerInventory.player;
        this.addSlot(new Slot(this.main, 0, 27, 47));
        this.addSlot(new Slot(this.main, 1, 76, 47));
        this.addSlot(new Slot(this.result, 2, 134, 47) {
            public boolean canInsert(ItemStack itemStack_1) {
                return false;
            }
            
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return hasStack();
            }
            
            public ItemStack onTakeItem(PlayerEntity playerEntity_1, ItemStack itemStack_1) {
                main.setInvStack(0, ItemStack.EMPTY);
                ItemStack stack = main.getInvStack(1);
                stack.subtractAmount(1);
                main.setInvStack(1, stack);
                return itemStack_1;
            }
        });
        int int_4;
        for(int_4 = 0; int_4 < 3; ++int_4)
            for(int int_3 = 0; int_3 < 9; ++int_3)
                this.addSlot(new Slot(playerInventory, int_3 + int_4 * 9 + 9, 8 + int_3 * 18, 84 + int_4 * 18));
        for(int_4 = 0; int_4 < 9; ++int_4)
            this.addSlot(new Slot(playerInventory, int_4, 8 + int_4 * 18, 142));
    }
    
    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return this.context.run((world, blockPos) -> {
            return world.getBlockState(blockPos).getBlock() != MaterialisedTools.MATERIALISING_TABLE ? false : playerEntity.squaredDistanceTo(blockPos.getX() + .5D, blockPos.getY() + .5D, blockPos.getZ() + .5D) < 64D;
        }, true);
    }
    
    public void setNewItemName(String string_1) {
        this.itemName = string_1;
        if (this.getSlot(2).hasStack()) {
            ItemStack itemStack_1 = this.getSlot(2).getStack();
            if (StringUtils.isBlank(string_1)) {
                itemStack_1.removeDisplayName();
            } else {
                itemStack_1.setDisplayName(new TextComponent(this.itemName));
            }
        }
        this.updateResult();
    }
    
    @Override
    public void onContentChanged(Inventory inventory_1) {
        super.onContentChanged(inventory_1);
        if (inventory_1 == this.main) {
            this.updateResult();
        }
    }
    
    private void updateResult() {
        ItemStack first = this.main.getInvStack(0);
        ItemStack second = this.main.getInvStack(1);
        if (first.isEmpty()) {
            this.result.setInvStack(0, ItemStack.EMPTY);
        } else if (first.getItem() instanceof MaterialisedPickaxeItem) {
            ItemStack copy = first.copy();
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if ((first.getItem() == MaterialisedTools.PICKAXE_HEAD && second.getItem() == MaterialisedTools.HANDLE) || (first.getItem() == MaterialisedTools.HANDLE && second.getItem() == MaterialisedTools.PICKAXE_HEAD)) {
            int handle = 0, head = 0;
            if (first.getItem() == MaterialisedTools.HANDLE)
                head = 1;
            else if (first.getItem() == MaterialisedTools.PICKAXE_HEAD)
                handle = 1;
            KnownMaterial handleMaterial = MaterialisedToolUtils.getMaterialFromPart(main.getInvStack(handle));
            KnownMaterial headMaterial = MaterialisedToolUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisedToolUtils.createPickaxe(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.itemName)) {
                    if (copy.hasDisplayName())
                        copy.removeDisplayName();
                } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                    if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                        copy.removeDisplayName();
                    else
                        copy.setDisplayName(new TextComponent(this.itemName));
                this.result.setInvStack(0, copy);
            }
        } else {
            this.result.setInvStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }
    
    @Override
    public void close(PlayerEntity playerEntity_1) {
        super.close(playerEntity_1);
        this.context.run((world_1, blockPos_1) -> {
            this.dropInventory(playerEntity_1, world_1, this.main);
        });
    }
    
    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity_1, int int_1) {
        ItemStack itemStack_1 = ItemStack.EMPTY;
        Slot slot_1 = (Slot) this.slotList.get(int_1);
        if (slot_1 != null && slot_1.hasStack()) {
            ItemStack itemStack_2 = slot_1.getStack();
            itemStack_1 = itemStack_2.copy();
            if (int_1 == 2) {
                if (!this.insertItem(itemStack_2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                
                slot_1.onStackChanged(itemStack_2, itemStack_1);
            } else if (int_1 != 0 && int_1 != 1) {
                if (int_1 >= 3 && int_1 < 39 && !this.insertItem(itemStack_2, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack_2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemStack_2.isEmpty()) {
                slot_1.setStack(ItemStack.EMPTY);
            } else {
                slot_1.markDirty();
            }
            
            if (itemStack_2.getAmount() == itemStack_1.getAmount()) {
                return ItemStack.EMPTY;
            }
            
            slot_1.onTakeItem(playerEntity_1, itemStack_2);
        }
        
        return itemStack_1;
    }
    
}

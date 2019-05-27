package me.shedaniel.materialisation.containers;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.KnownMaterial;
import me.shedaniel.materialisation.api.KnownMaterials;
import me.shedaniel.materialisation.items.MaterialisedToolUtils;
import me.shedaniel.materialisation.items.PatternItem;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.stream.Collectors;

public class MaterialPreparerContainer extends Container {
    
    private final Inventory main, result;
    private final PlayerEntity player;
    private BlockContext context;
    private String itemName;
    private int takingFirst, takingSecond;
    
    public MaterialPreparerContainer(int syncId, PlayerInventory main) {
        this(syncId, main, BlockContext.EMPTY);
    }
    
    public MaterialPreparerContainer(int syncId, PlayerInventory playerInventory, final BlockContext context) {
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
        this.addSlot(new Slot(this.main, 0, 27, 21) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return itemStack_1.getItem() instanceof PatternItem;
            }
        });
        this.addSlot(new Slot(this.main, 1, 76, 21));
        this.addSlot(new Slot(this.result, 2, 134, 21) {
            public boolean canInsert(ItemStack itemStack_1) {
                return false;
            }
            
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return hasStack() && main.getInvStack(0).getAmount() >= takingFirst && main.getInvStack(1).getAmount() >= takingSecond;
            }
            
            public ItemStack onTakeItem(PlayerEntity playerEntity_1, ItemStack itemStack_1) {
                ItemStack first = main.getInvStack(0);
                first.subtractAmount(takingFirst);
                main.setInvStack(0, first);
                ItemStack second = main.getInvStack(1);
                second.subtractAmount(takingSecond);
                main.setInvStack(1, second);
                return itemStack_1;
            }
        });
        int int_4;
        for(int_4 = 0; int_4 < 3; ++int_4)
            for(int int_3 = 0; int_3 < 9; ++int_3)
                this.addSlot(new Slot(playerInventory, int_3 + int_4 * 9 + 9, 8 + int_3 * 18, 58 + int_4 * 18));
        for(int_4 = 0; int_4 < 9; ++int_4)
            this.addSlot(new Slot(playerInventory, int_4, 8 + int_4 * 18, 116));
    }
    
    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return this.context.run((world, blockPos) -> {
            return world.getBlockState(blockPos).getBlock() != Materialisation.MATERIAL_PREPARER ? false : playerEntity.squaredDistanceTo(blockPos.getX() + .5D, blockPos.getY() + .5D, blockPos.getZ() + .5D) < 64D;
        }, true);
    }
    
    @Override
    public void onContentChanged(Inventory inventory_1) {
        super.onContentChanged(inventory_1);
        if (inventory_1 == this.main) {
            this.updateResult();
        }
    }
    
    private void updateResult() {
        takingFirst = 0;
        takingSecond = 0;
        ItemStack first = this.main.getInvStack(0);
        ItemStack second = this.main.getInvStack(1);
        if (first.isEmpty() || second.isEmpty()) {
            this.result.setInvStack(0, ItemStack.EMPTY);
        } else if (first.getItem() instanceof PatternItem && first.getItem() != Materialisation.BLANK_PATTERN) {
            if (first.getItem() == Materialisation.PICKAXE_HEAD_PATTERN) {
                KnownMaterial material = null;
                float repairMultiplier = -1;
                for(KnownMaterial knownMaterial : KnownMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = knownMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = knownMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setInvStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(4 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getAmount() < itemsNeeded) {
                        this.result.setInvStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setInvStack(0, MaterialisedToolUtils.createPickaxeHead(material));
                    }
                }
            } else if (first.getItem() == Materialisation.TOOL_HANDLE_PATTERN) {
                KnownMaterial material = null;
                float repairMultiplier = -1;
                for(KnownMaterial knownMaterial : KnownMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = knownMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = knownMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setInvStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(1 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getAmount() < itemsNeeded) {
                        this.result.setInvStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setInvStack(0, MaterialisedToolUtils.createToolHandle(material));
                    }
                }
            } else {
                this.result.setInvStack(0, ItemStack.EMPTY);
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

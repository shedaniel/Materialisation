package me.shedaniel.materialisation.containers;

import io.netty.buffer.Unpooled;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.items.*;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
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
import net.minecraft.util.PacketByteBuf;
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
                context.run((world, blockPos) -> {
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity_1, Materialisation.MATERIALISING_TABLE_PLAY_SOUND, new PacketByteBuf(Unpooled.buffer()));
                });
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
            return world.getBlockState(blockPos).getBlock() != Materialisation.MATERIALISING_TABLE ? false : playerEntity.squaredDistanceTo(blockPos.getX() + .5D, blockPos.getY() + .5D, blockPos.getZ() + .5D) < 64D;
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
        } else if (first.getItem() instanceof MaterialisedMiningTool && first.getOrCreateTag().containsKey("mt_0_material") && first.getOrCreateTag().containsKey("mt_1_material")) {
            // Fixing Special
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().containsKey("mt_1_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_1_material"));
                if (material == null) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if (first.getItem() instanceof MaterialisedPickaxeItem) {
            // Fixing pickaxe
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().containsKey("mt_pickaxe_head_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_pickaxe_head_material"));
                if (material == null) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if (first.getItem() instanceof MaterialisedAxeItem) {
            // Fixing axe
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().containsKey("mt_axe_head_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_axe_head_material"));
                if (material == null) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if (first.getItem() instanceof MaterialisedShovelItem) {
            // Fixing shovel
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().containsKey("mt_shovel_head_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_shovel_head_material"));
                if (material == null) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if (first.getItem() instanceof MaterialisedSwordItem) {
            // Fixing sword
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().containsKey("mt_sword_blade_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_sword_blade_material"));
                if (material == null) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if (first.getItem() instanceof MaterialisedHammerItem) {
            // Fixing hammer
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().containsKey("mt_hammer_head_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_hammer_head_material"));
                if (material == null) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.itemName)) {
                if (copy.hasDisplayName())
                    copy.removeDisplayName();
            } else if (!this.itemName.equals(copy.getDisplayName().getString()))
                if (itemName.equals(copy.getItem().getTranslatedNameTrimmed(copy)))
                    copy.removeDisplayName();
                else
                    copy.setDisplayName(new TextComponent(this.itemName));
            this.result.setInvStack(0, copy);
        } else if ((first.getItem() == Materialisation.PICKAXE_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.PICKAXE_HEAD)) {
            // Crafting a pickaxe
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.PICKAXE_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createPickaxe(handleMaterial, headMaterial);
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
        } else if ((first.getItem() == Materialisation.AXE_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.AXE_HEAD)) {
            // Crafting an axe
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.AXE_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createAxe(handleMaterial, headMaterial);
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
        } else if ((first.getItem() == Materialisation.SHOVEL_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.SHOVEL_HEAD)) {
            // Crafting a shovel
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.SHOVEL_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createShovel(handleMaterial, headMaterial);
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
        } else if ((first.getItem() == Materialisation.SWORD_BLADE && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.SWORD_BLADE)) {
            // Crafting a sword
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.SWORD_BLADE)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createSword(handleMaterial, headMaterial);
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
        }else if ((first.getItem() == Materialisation.MEGAAXE_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.MEGAAXE_HEAD)) {
            // Crafting a mega axe
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.MEGAAXE_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createMegaAxe(handleMaterial, headMaterial);
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
        } else if ((first.getItem() == Materialisation.HAMMER_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.HAMMER_HEAD)) {
            // Crafting a hammer
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.HAMMER_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getInvStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setInvStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createHammer(handleMaterial, headMaterial);
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

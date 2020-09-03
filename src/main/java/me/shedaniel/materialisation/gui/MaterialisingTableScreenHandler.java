package me.shedaniel.materialisation.gui;

import io.netty.buffer.Unpooled;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.ModifierIngredient;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.materialisation.modifiers.Modifiers;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

public class MaterialisingTableScreenHandler extends AbstractMaterialisingHandlerBase {
    private String newItemName;
    private int nextDecrease;

    public MaterialisingTableScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public MaterialisingTableScreenHandler(int syncId, PlayerInventory inventory, final ScreenHandlerContext context) {
        super(Materialisation.MATERIALISING_TABLE_SCREEN_HANDLER, syncId, inventory, context);
        this.addSlot(new Slot(this.main, 0, 27, 47));
        this.addSlot(new Slot(this.main, 1, 76, 47));
        this.addSlot(new Slot(this.result, 2, 134, 47) {
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            public boolean canTakeItems(PlayerEntity player) {
                return hasStack();
            }

            public ItemStack onTakeItem(PlayerEntity player, ItemStack itemStack) {
                ItemStack stack = main.getStack(0).copy();
                stack.decrement(1);
                main.setStack(0, stack);
                stack = main.getStack(1).copy();
                stack.decrement(nextDecrease);
                main.setStack(1, stack);
                context.run((world, blockPos) -> {
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Materialisation.MATERIALISING_TABLE_PLAY_SOUND, new PacketByteBuf(Unpooled.buffer()));
                });
                return itemStack;
            }
        });
        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    protected boolean canUse(BlockState state) {
        return state.getBlock() == Materialisation.MATERIALISING_TABLE;
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return true;
    }

    @Override
    protected ItemStack onTakeOutput(PlayerEntity player, ItemStack stack) {
        this.main.setStack(0, ItemStack.EMPTY);
        ItemStack itemStack = this.main.getStack(1);
        if (!itemStack.isEmpty()) {
            itemStack.decrement(1);
            this.main.setStack(1, itemStack);
        } else {
            this.main.setStack(1, ItemStack.EMPTY);
        }
        return stack;
    }

    public void updateResult() {
        ItemStack first = this.main.getStack(0);
        ItemStack second = this.main.getStack(1);
        if (first.isEmpty()) {
            this.result.setStack(0, ItemStack.EMPTY);
        } else if (first.getItem() instanceof MaterialisedMiningTool
            && first.getOrCreateTag().contains("mt_0_material")
            && first.getOrCreateTag().contains("mt_1_material")
        ) {
            // Modifiers
            if (!second.isEmpty()) {
                ItemStack copy = first.copy();
                Map<Modifier, Integer> modifierIntegerMap = MaterialisationUtils.getToolModifiers(copy);
                for (Modifier modifier : Materialisation.MODIFIERS) {
                    Integer currentLevel = modifierIntegerMap.getOrDefault(modifier, 0);
                    if (modifier.isApplicableTo(copy) && modifier.getMaximumLevel(copy) > currentLevel) {
                        int nextLevel = currentLevel + 1;
                        Optional<Pair<Modifier, Pair<ModifierIngredient, BetterIngredient>>> modifierOptional
                            = Modifiers.getModifierByIngredient(second, modifier, nextLevel);
                        if (modifierOptional.isPresent()) {
                            MaterialisedMiningTool tool = (MaterialisedMiningTool) copy.getItem();
                            int maximumLevel = modifier.getMaximumLevel(first);
                            int level = tool.getModifierLevel(first, modifier);
                            tool.setModifierLevel(copy, modifier, level + 1);
                            if (level + 1 <= maximumLevel || MaterialisationUtils.getToolMaxDurability(copy) >= 1) {
                                nextDecrease = modifierOptional.get().getRight().getRight().count;
                                this.result.setStack(0, copy);
                            } else {
                                this.result.setStack(0, ItemStack.EMPTY);
                            }
                            this.sendContentUpdates();
                            return;
                        }
                    }
                }
            }


            // Fixing Special
            ItemStack copy = first.copy();
            int toolDurability = MaterialisationUtils.getToolDurability(first);
            int maxDurability = MaterialisationUtils.getToolMaxDurability(first);
            if (!second.isEmpty()) {
                if (toolDurability >= maxDurability) {
                    this.result.setStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                PartMaterial material = null;
                if (copy.getOrCreateTag().contains("mt_1_material"))
                    material = MaterialisationUtils.getMaterialFromString(copy.getOrCreateTag().getString("mt_1_material"));
                if (material == null) {
                    this.result.setStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                int repairAmount = material.getRepairAmount(second);
                if (repairAmount <= 0) {
                    this.result.setStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                MaterialisationUtils.setToolDurability(copy, Math.min(maxDurability, toolDurability + repairAmount));
            }
            if (StringUtils.isBlank(this.newItemName)) {
                if (copy.hasCustomName())
                    copy.removeCustomName();
            } else if (!this.newItemName.equals(copy.getName().getString()))
                if (newItemName.equals(copy.getItem().getName(copy).getString()))
                    copy.removeCustomName();
                else
                    copy.setCustomName(new LiteralText(this.newItemName));
            nextDecrease = 1;
            this.result.setStack(0, copy);
        } else if ((first.getItem() == Materialisation.PICKAXE_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.PICKAXE_HEAD)) {
            // Crafting a pickaxe
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.PICKAXE_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createPickaxe(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.newItemName)) {
                    if (copy.hasCustomName())
                        copy.removeCustomName();
                } else if (!this.newItemName.equals(copy.getName().getString()))
                    if (newItemName.equals(copy.getItem().getName(copy).getString()))
                        copy.removeCustomName();
                    else
                        copy.setCustomName(new LiteralText(this.newItemName));
                nextDecrease = 1;
                this.result.setStack(0, copy);
            }
        } else if ((first.getItem() == Materialisation.AXE_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.AXE_HEAD)) {
            // Crafting an axe
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.AXE_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createAxe(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.newItemName)) {
                    if (copy.hasCustomName())
                        copy.removeCustomName();
                } else if (!this.newItemName.equals(copy.getName().getString()))
                    if (newItemName.equals(copy.getItem().getName(copy).getString()))
                        copy.removeCustomName();
                    else
                        copy.setCustomName(new LiteralText(this.newItemName));
                nextDecrease = 1;
                this.result.setStack(0, copy);
            }
        } else if ((first.getItem() == Materialisation.SHOVEL_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.SHOVEL_HEAD)) {
            // Crafting a shovel
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.SHOVEL_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createShovel(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.newItemName)) {
                    if (copy.hasCustomName())
                        copy.removeCustomName();
                } else if (!this.newItemName.equals(copy.getName().getString()))
                    if (newItemName.equals(copy.getItem().getName(copy).getString()))
                        copy.removeCustomName();
                    else
                        copy.setCustomName(new LiteralText(this.newItemName));
                nextDecrease = 1;
                this.result.setStack(0, copy);
            }
        } else if ((first.getItem() == Materialisation.SWORD_BLADE && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.SWORD_BLADE)) {
            // Crafting a sword
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.SWORD_BLADE)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createSword(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.newItemName)) {
                    if (copy.hasCustomName())
                        copy.removeCustomName();
                } else if (!this.newItemName.equals(copy.getName().getString()))
                    if (newItemName.equals(copy.getItem().getName(copy).getString()))
                        copy.removeCustomName();
                    else
                        copy.setCustomName(new LiteralText(this.newItemName));
                nextDecrease = 1;
                this.result.setStack(0, copy);
            }
        } else if ((first.getItem() == Materialisation.MEGAAXE_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.MEGAAXE_HEAD)) {
            // Crafting a mega axe
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.MEGAAXE_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createMegaAxe(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.newItemName)) {
                    if (copy.hasCustomName())
                        copy.removeCustomName();
                } else if (!this.newItemName.equals(copy.getName().getString()))
                    if (newItemName.equals(copy.getItem().getName(copy).getString()))
                        copy.removeCustomName();
                    else
                        copy.setCustomName(new LiteralText(this.newItemName));
                nextDecrease = 1;
                this.result.setStack(0, copy);
            }
        } else if ((first.getItem() == Materialisation.HAMMER_HEAD && second.getItem() == Materialisation.HANDLE) || (first.getItem() == Materialisation.HANDLE && second.getItem() == Materialisation.HAMMER_HEAD)) {
            // Crafting a hammer
            int handle = 0, head = 0;
            if (first.getItem() == Materialisation.HANDLE)
                head = 1;
            else if (first.getItem() == Materialisation.HAMMER_HEAD)
                handle = 1;
            PartMaterial handleMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(handle));
            PartMaterial headMaterial = MaterialisationUtils.getMaterialFromPart(main.getStack(head));
            if (handleMaterial == null || headMaterial == null) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack copy = MaterialisationUtils.createHammer(handleMaterial, headMaterial);
                if (StringUtils.isBlank(this.newItemName)) {
                    if (copy.hasCustomName())
                        copy.removeCustomName();
                } else if (!this.newItemName.equals(copy.getName().getString()))
                    if (newItemName.equals(copy.getItem().getName(copy).getString()))
                        copy.removeCustomName();
                    else
                        copy.setCustomName(new LiteralText(this.newItemName));
                nextDecrease = 1;
                this.result.setStack(0, copy);
            }
        } else {
            this.result.setStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack1 = ItemStack.EMPTY;
        Slot slot1 = this.slots.get(index);
        if (slot1 != null && slot1.hasStack()) {
            ItemStack itemStack2 = slot1.getStack();
            itemStack1 = itemStack2.copy();
            if (index == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot1.onStackChanged(itemStack2, itemStack1);
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39 && !this.insertItem(itemStack2, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot1.setStack(ItemStack.EMPTY);
            } else {
                slot1.markDirty();
            }

            if (itemStack2.getCount() == itemStack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot1.onTakeItem(player, itemStack2);
        }

        return itemStack1;
    }

    public void setNewItemName(String string) {
        this.newItemName = string;
        if (this.getSlot(2).hasStack()) {
            ItemStack itemStack = this.getSlot(2).getStack();
            if (StringUtils.isBlank(string)) {
                itemStack.removeCustomName();
            } else {
                itemStack.setCustomName(new LiteralText(this.newItemName));
            }
        }
        this.updateResult();
    }
}

package me.shedaniel.materialisation.gui;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.items.PatternItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

@SuppressWarnings("CanBeFinal")
public class MaterialPreparerScreenHandler extends AbstractMaterialisingHandlerBase {
    private int takingFirst, takingSecond;

    public MaterialPreparerScreenHandler(int syncId, PlayerInventory main) {
        this(syncId, main, ScreenHandlerContext.EMPTY);
    }

    @SuppressWarnings("unused")
    public MaterialPreparerScreenHandler(int syncId, PlayerInventory inventory, final ScreenHandlerContext context) {
        super(Materialisation.MATERIAL_PREPARER_SCREEN_HANDLER, syncId, inventory, context);
        this.addSlot(new Slot(this.main, 0, 27, 21) {
            @Override
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() instanceof PatternItem;
            }
        });
        this.addSlot(new Slot(this.main, 1, 76, 21));
        this.addSlot(new Slot(this.result, 2, 134, 21) {
            public boolean canInsert(ItemStack itemStack) {
                return false;
            }
            
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return hasStack() && main.getStack(0).getCount() >= takingFirst && main.getStack(1).getCount() >= takingSecond;
            }
            
            public ItemStack onTakeItem(PlayerEntity player, ItemStack itemStack) {
                ItemStack first = main.getStack(0);
                first.decrement(takingFirst);
                main.setStack(0, first);
                ItemStack second = main.getStack(1);
                second.decrement(takingSecond);
                main.setStack(1, second);
                return itemStack;
            }
        });
        int int_4;
        for (int_4 = 0; int_4 < 3; ++int_4)
            for (int int_3 = 0; int_3 < 9; ++int_3)
                this.addSlot(new Slot(inventory, int_3 + int_4 * 9 + 9, 8 + int_3 * 18, 58 + int_4 * 18));
        for (int_4 = 0; int_4 < 9; ++int_4)
            this.addSlot(new Slot(inventory, int_4, 8 + int_4 * 18, 116));
    }

    protected boolean canUse(BlockState state) {
        return state.getBlock() == Materialisation.MATERIAL_PREPARER;
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
        takingFirst = 0;
        takingSecond = 0;
        ItemStack first = this.main.getStack(0);
        ItemStack second = this.main.getStack(1);
        if (first.isEmpty() || second.isEmpty()) {
            this.result.setStack(0, ItemStack.EMPTY);
        } else if (first.getItem() instanceof PatternItem && first.getItem() != Materialisation.BLANK_PATTERN) {
            if (first.getItem() == Materialisation.PICKAXE_HEAD_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(4 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createPickaxeHead(material));
                    }
                }
            } else if (first.getItem() == Materialisation.TOOL_HANDLE_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(1 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createToolHandle(material));
                    }
                }
            } else if (first.getItem() == Materialisation.AXE_HEAD_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(4 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createAxeHead(material));
                    }
                }
            } else if (first.getItem() == Materialisation.SHOVEL_HEAD_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(4 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createShovelHead(material));
                    }
                }
            } else if (first.getItem() == Materialisation.SWORD_BLADE_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(4 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createSwordBlade(material));
                    }
                }
            } else if (first.getItem() == Materialisation.HAMMER_HEAD_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(16 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createHammerHead(material));
                    }
                }
            } else if (first.getItem() == Materialisation.MEGAAXE_HEAD_PATTERN) {
                PartMaterial material = null;
                float repairMultiplier = -1;
                for (PartMaterial partMaterial : PartMaterials.getKnownMaterials().collect(Collectors.toList())) {
                    float repairAmount = partMaterial.getRepairMultiplier(second);
                    if (repairAmount > 0) {
                        material = partMaterial;
                        repairMultiplier = repairAmount;
                    }
                }
                if (material == null || repairMultiplier <= 0)
                    this.result.setStack(0, ItemStack.EMPTY);
                else {
                    int itemsNeeded = MathHelper.ceil(64 / repairMultiplier);
                    takingSecond = itemsNeeded;
                    if (second.getCount() < itemsNeeded) {
                        this.result.setStack(0, ItemStack.EMPTY);
                    } else {
                        this.result.setStack(0, MaterialisationUtils.createMegaAxeHead(material));
                    }
                }
            } else {
                this.result.setStack(0, ItemStack.EMPTY);
            }
        } else {
            this.result.setStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @SuppressWarnings("ConstantConditions")
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
}

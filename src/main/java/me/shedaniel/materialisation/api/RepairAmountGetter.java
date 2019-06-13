package me.shedaniel.materialisation.api;

import net.minecraft.item.ItemStack;

public interface RepairAmountGetter {
    int getRepairAmount(ItemStack stack);
}
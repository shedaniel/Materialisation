package me.shedaniel.materialisation.mixin.client;

import com.google.common.collect.Multimap;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow public abstract Item getItem();

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 15, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void getTooltipAddDamage(PlayerEntity playerEntity_1, TooltipContext tooltipContext_1, CallbackInfoReturnable<List> cir, List list_1) {
        if (getItem() instanceof MaterialisedMiningTool && !list_1.isEmpty()) {
            list_1.remove(list_1.size() - 1);
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z", ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    public void getTooltipMultimap(PlayerEntity playerEntity_1, TooltipContext tooltipContext_1, CallbackInfoReturnable<List> cir, List list_1, int int_1, EquipmentSlot var6[], int var7, int var8, EquipmentSlot equipmentSlot_1, Multimap multimap_1) {
        if (getItem() instanceof MaterialisedMiningTool)
            multimap_1.clear();
    }

}

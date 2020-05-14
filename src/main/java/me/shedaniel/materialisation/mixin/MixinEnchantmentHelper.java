package me.shedaniel.materialisation.mixin;

import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
    @Inject(method = "getFireAspect", at = @At("RETURN"), cancellable = true)
    private static void getFireAspect(LivingEntity entity, CallbackInfoReturnable<Integer> callbackInfo) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            int fireLevel = ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, "materialisation:fire");
            if (fireLevel != 0)
                callbackInfo.setReturnValue(callbackInfo.getReturnValue() + fireLevel * 2);
        }
    }
}

package me.shedaniel.materialisation.mixin;

import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to EnchantmentTarget.BREAKABLE
 */
@Mixin(targets = {"net/minecraft/enchantment/EnchantmentTarget$3"})
public class MixinEnchantmentTarget {
    @Inject(method = {"isAcceptableItem(Lnet/minecraft/item/Item;)Z", "method_8177(Lnet/minecraft/class_1792;)Z"}, remap = false, at = @At("HEAD"), cancellable = true)
    public void isAcceptableItem(Item var1, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if ((Object) this == EnchantmentTarget.BREAKABLE && var1 instanceof MaterialisedMiningTool)
            callbackInfoReturnable.setReturnValue(true);
    }
}

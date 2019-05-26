package me.shedaniel.materialisation.mixin;

import me.shedaniel.materialisation.Materialisation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    
    @Shadow
    public abstract Item getItem();
    
    /**
     * Disable italic on tools
     */
    @Inject(method = "hasDisplayName", at = @At("HEAD"), cancellable = true)
    public void hasDisplayName(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (getItem() == Materialisation.MATERIALISED_PICKAXE)
            callbackInfo.setReturnValue(false);
    }
    
}

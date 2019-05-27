package me.shedaniel.materialisation.mixin;

import me.shedaniel.materialisation.Materialisation;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilContainer.class)
public abstract class MixinAnvilContainer extends Container {
    
    @Shadow @Final private Inventory inventory;
    
    @Shadow @Final private Inventory result;
    
    @Shadow @Final private Property levelCost;
    
    protected MixinAnvilContainer(ContainerType<?> containerType_1, int int_1) {
        super(containerType_1, int_1);
    }
    
    /**
     * Disable anvil on tools
     */
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void updateResult(CallbackInfo callbackInfo) {
        if (inventory.getInvStack(0).getItem() == Materialisation.MATERIALISED_PICKAXE) {
            result.setInvStack(0, ItemStack.EMPTY);
            levelCost.set(0);
            sendContentUpdates();
            callbackInfo.cancel();
        } else if (inventory.getInvStack(0).getItem() == Materialisation.MATERIALISED_AXE) {
            result.setInvStack(0, ItemStack.EMPTY);
            levelCost.set(0);
            sendContentUpdates();
            callbackInfo.cancel();
        }
    }
    
}

package me.shedaniel.materialisation.mixin;

import me.shedaniel.materialisation.optifine.RealItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    /**
     * This is used to render the custom damage bar
     *
     * @author shedaniel
     */
    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z", ordinal = 0))
    public void renderGuiItemOverlay(TextRenderer font, ItemStack stack, int x, int y, String overlayText, CallbackInfo callbackInfo) {
        RealItemRenderer.renderGuiItemOverlay(stack, x, y);
    }
}

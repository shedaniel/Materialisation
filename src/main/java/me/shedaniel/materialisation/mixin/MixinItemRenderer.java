package me.shedaniel.materialisation.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow
    protected abstract void renderGuiQuad(BufferBuilder bufferBuilder_1, int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, int int_8);

    /**
     * This is used to render the custom damage bar
     *
     * @author shedaniel
     */
    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z", ordinal = 0,
                    shift = At.Shift.BEFORE))
    public void renderGuiItemOverlay(TextRenderer font, ItemStack stack, int x, int y, String overlayText, CallbackInfo callbackInfo) {
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            float float_1 = MaterialisationUtils.getToolDurability(stack);
            float float_2 = MaterialisationUtils.getToolMaxDurability(stack);
            if (float_1 < float_2) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                GlStateManager.disableTexture();
                GlStateManager.disableAlphaTest();
                GlStateManager.disableBlend();
                Tessellator tessellator_1 = Tessellator.getInstance();
                BufferBuilder bufferBuilder_1 = tessellator_1.getBufferBuilder();
                float float_3 = Math.max(0.0F, float_1 / float_2);
                int int_3 = Math.round(13.0F - (float_2 - float_1) * 13.0F / float_2);
                int int_4 = MathHelper.hsvToRgb(float_3 / 3.0F, 1.0F, 1.0F);
                this.renderGuiQuad(bufferBuilder_1, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.renderGuiQuad(bufferBuilder_1, x + 2, y + 13, int_3, 1, int_4 >> 16 & 255, int_4 >> 8 & 255, int_4 & 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlphaTest();
                GlStateManager.enableTexture();
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
            }
        }
    }

}

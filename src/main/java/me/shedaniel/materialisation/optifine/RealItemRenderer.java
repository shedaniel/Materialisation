package me.shedaniel.materialisation.optifine;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class RealItemRenderer {
    public static void renderGuiItemOverlay(ItemStack stack, int x, int y) {
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            float float_1 = MaterialisationUtils.getToolDurability(stack);
            float float_2 = MaterialisationUtils.getToolMaxDurability(stack);
            if (float_1 < float_2) {
                RenderSystem.pushMatrix();
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator_1 = Tessellator.getInstance();
                BufferBuilder bufferBuilder_1 = tessellator_1.getBuffer();
                float float_3 = Math.max(0.0F, float_1 / float_2);
                int int_3 = Math.round(13.0F - (float_2 - float_1) * 13.0F / float_2);
                int int_4 = MathHelper.hsvToRgb(float_3 / 3.0F, 1.0F, 1.0F);
                renderGuiQuad(bufferBuilder_1, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                renderGuiQuad(bufferBuilder_1, x + 2, y + 13, int_3, 1, int_4 >> 16 & 255, int_4 >> 8 & 255, int_4 & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.popMatrix();
            }
        }
    }
    
    private static void renderGuiQuad(BufferBuilder bufferBuilder_1, int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, int int_8) {
        bufferBuilder_1.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder_1.vertex(int_1, int_2, 0.0D).color(int_5, int_6, int_7, int_8).next();
        bufferBuilder_1.vertex(int_1, int_2 + int_4, 0.0D).color(int_5, int_6, int_7, int_8).next();
        bufferBuilder_1.vertex(int_1 + int_3, int_2 + int_4, 0.0D).color(int_5, int_6, int_7, int_8).next();
        bufferBuilder_1.vertex(int_1 + int_3, int_2, 0.0D).color(int_5, int_6, int_7, int_8).next();
        Tessellator.getInstance().draw();
    }
}

package me.shedaniel.materialisation.optifine;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;


public class RealItemRenderer {
    public static boolean renderGuiItemOverlay(ItemStack stack, int x, int y) {
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            float durability = MaterialisationUtils.getToolDurability(stack);
            float maxDurability = MaterialisationUtils.getToolMaxDurability(stack);
            if (durability < maxDurability) {
                MatrixStack matrixStack = new MatrixStack();
                matrixStack.push();
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tessellator string = Tessellator.getInstance();
                BufferBuilder immediate = string.getBuffer();
                int i = Math.round(13.0F - (maxDurability - durability) * 13.0F / maxDurability);
                float hue = Math.max(0.0F, durability / maxDurability);
                int j = MathHelper.hsvToRgb(hue / 3.0F, 1.0F, 1.0F);
                renderGuiQuad(immediate, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                renderGuiQuad(immediate, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
                matrixStack.pop();
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("SameParameterValue")
    private static void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex((x), y, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex((x), (y + height), 0.0).color(red, green, blue, alpha).next();
        buffer.vertex((x + width), (y + height), 0.0).color(red, green, blue, alpha).next();
        buffer.vertex((x + width), (y), 0.0).color(red, green, blue, alpha).next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }
}

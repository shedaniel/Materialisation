package me.shedaniel.materialisation.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.opengl.GL11;

import static me.shedaniel.materialisation.modmenu.MaterialisationMaterialsScreen.overlayBackground;

@SuppressWarnings("CanBeFinal")
public class MaterialisationSimpleMessageScreen extends Screen {
    private Screen parent;
    private String text;
    
    public MaterialisationSimpleMessageScreen(Screen parent, Text title, String text) {
        super(title);
        this.parent = parent;
        this.text = text;
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256 && this.shouldCloseOnEsc()) {
            assert client != null;
            client.openScreen(parent);
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }
    
    @Override
    protected void init() {
        super.init();
        addButton(new ButtonWidget(4, 4, 75, 20, new TranslatableText("gui.back"), var1 -> {
            assert client != null;
            client.openScreen(parent);
        }));
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        overlayBackground(0, 0, width, height, 32, 32, 32, 255, 255);
        overlayBackground(0, 0, width, 28, 64, 64, 64, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(7425);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(0, 28 + 4, 0.0D).color(0, 0, 0, 0).texture(0.0F, 1.0F).next();
        buffer.vertex(this.width, 28 + 4, 0.0D).color(0, 0, 0, 0).texture(1.0F, 1.0F).next();
        buffer.vertex(this.width, 28, 0.0D).color(0, 0, 0, 255).texture(1.0F, 0.0F).next();
        buffer.vertex(0, 28, 0.0D).color(0, 0, 0, 255).texture(0.0F, 0.0F).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        GL11.glShadeModel(7424);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderSystem.disableBlend();
        drawCenteredText(stack, textRenderer, title, width / 2, 10, 16777215);
        int y = 40;
        for (String s : text.split("\n")) {
            for (OrderedText s1 : textRenderer.wrapLines(new LiteralText(s), width - 20)) {
                textRenderer.draw(stack, s1, (float)(width / 2 - textRenderer.getWidth(s1) / 2), y, 16777215);
                y += 9;
            }
        }
        super.render(stack, mouseX, mouseY, delta);
    }
}

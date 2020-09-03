package me.shedaniel.materialisation.modmenu;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
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
import net.minecraft.util.Formatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static me.shedaniel.materialisation.modmenu.MaterialisationMaterialsScreen.overlayBackground;

@SuppressWarnings("CanBeFinal")
public class MaterialisationErrorInstallScreen extends Screen {
    
    private Screen parent;
    private Throwable throwable;
    private MaterialisationOverridesListWidget listWidget;
    
    public MaterialisationErrorInstallScreen(Screen parent, Throwable throwable) {
        super(new TranslatableText("message.materialisation.installation_errored").formatted(Formatting.RED));
        this.parent = parent;
        this.throwable = throwable;
        throwable.printStackTrace();
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
        List<MaterialisationOverridesListWidget.Entry> entries = Lists.newArrayList();
        List<String> s = new ArrayList<>();
        s.add("An error occurred during materialisation pack installation: " + throwable.toString());
        for (StackTraceElement traceElement : throwable.getStackTrace()) {
            s.add("  at " + traceElement);
        }
        for (String s1 : s) {
            for (OrderedText s2 : textRenderer.wrapLines(new LiteralText(s1), width - 40)) {
                entries.add(new MaterialisationOverridesListWidget.TextEntry((Text)s2));
            }
        }
        children.add(listWidget = new MaterialisationOverridesListWidget(client, width, height, 28, height, DrawableHelper.BACKGROUND_TEXTURE));
        for (MaterialisationOverridesListWidget.Entry entry : entries) {
            listWidget.addItem(entry);
        }
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(0);
        listWidget.render(stack, mouseX, mouseY, delta);
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
        super.render(stack, mouseX, mouseY, delta);
    }
}

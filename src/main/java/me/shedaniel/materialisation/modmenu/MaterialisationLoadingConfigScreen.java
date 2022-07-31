package me.shedaniel.materialisation.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.config.ConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import static me.shedaniel.materialisation.modmenu.MaterialisationMaterialsScreen.overlayBackground;

@SuppressWarnings("CanBeFinal")
public class MaterialisationLoadingConfigScreen extends Screen {

    private MaterialisationMaterialsScreen previousScreen;

    public MaterialisationLoadingConfigScreen(MaterialisationMaterialsScreen previousScreen) {
        super(new TranslatableText("config.title.materialisation.loading"));
        this.previousScreen = previousScreen;
    }

    @Override
    public void tick() {
        super.tick();
        if (!ConfigHelper.loading) {
            MinecraftClient.getInstance().setScreen(new MaterialisationMaterialsScreen(previousScreen.parent));
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(0);
        overlayBackground(0, 0, width, 28, 64, 64, 64, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(0, 28 + 4, 0.0D).color(0, 0, 0, 0).texture(0.0F, 1.0F).next();
        buffer.vertex(this.width, 28 + 4, 0.0D).color(0, 0, 0, 0).texture(1.0F, 1.0F).next();
        buffer.vertex(this.width, 28, 0.0D).color(0, 0, 0, 255).texture(1.0F, 0.0F).next();
        buffer.vertex(0, 28, 0.0D).color(0, 0, 0, 255).texture(0.0F, 0.0F).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        drawCenteredText(stack, textRenderer, title, width / 2, 10, 16777215);
        super.render(stack, mouseX, mouseY, delta);
    }
}

package me.shedaniel.materialisation.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.config.ConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.TranslatableText;

import static me.shedaniel.materialisation.modmenu.MaterialisationMaterialsScreen.overlayBackground;

public class MaterialisationLoadingConfigScreen extends Screen {

    private Screen parent;
    private MaterialisationMaterialsScreen previousScreen;

    public MaterialisationLoadingConfigScreen(MaterialisationMaterialsScreen previousScreen, Screen parent) {
        super(new TranslatableText("config.title.materialisation.loading"));
        this.parent = parent;
        this.previousScreen = previousScreen;
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256 && this.shouldCloseOnEsc()) {
            minecraft.openScreen(parent);
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }

    @Override
    public void tick() {
        super.tick();
        if (!ConfigHelper.loading) {
            MinecraftClient.getInstance().openScreen(new MaterialisationMaterialsScreen(previousScreen.parent));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderDirtBackground(0);
        overlayBackground(0, 0, width, 28, 64, 64, 64, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(0, 28 + 4, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
        buffer.vertex(this.width, 28 + 4, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
        buffer.vertex(this.width, 28, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
        buffer.vertex(0, 28, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        drawCenteredString(font, title.asFormattedString(), width / 2, 10, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}

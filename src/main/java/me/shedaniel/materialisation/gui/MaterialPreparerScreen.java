package me.shedaniel.materialisation.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.ModReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class MaterialPreparerScreen extends HandledScreen<MaterialPreparerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ModReference.MOD_ID, "textures/gui/container/material_preparer.png");
    
    public MaterialPreparerScreen(MaterialPreparerScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
        this.titleX = 60;
    }
    
    @Override
    protected void init() {
        this.backgroundHeight = 140;
        super.init();
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.client != null;
            assert this.client.player != null;
            this.client.player.closeHandledScreen();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        this.textRenderer.draw(matrices, this.title, 6f, 6f, 4210752);
    }
    
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
            this.drawTexture(matrices, x + 99, y + 45 - 26, this.backgroundWidth, 0, 28, 21);
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    
}

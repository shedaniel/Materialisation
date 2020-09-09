package me.shedaniel.materialisation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.ModReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("ConstantConditions")
@Environment(EnvType.CLIENT)
public class MaterialPreparerScreen extends MaterialisingScreenBase<MaterialPreparerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ModReference.MOD_ID, "textures/gui/container/material_preparer.png");
    
    public MaterialPreparerScreen(MaterialPreparerScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, inventory, title, TEXTURE);
    }

    @Override
    protected void init() {
        this.backgroundHeight = 140;
        super.init();
    }

    protected void setup() {
        this.client.keyboard.setRepeatEvents(true);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
    }

    public void removed() {
        super.removed();
        assert this.client != null;
        this.client.keyboard.setRepeatEvents(false);
        this.handler.removeListener(this);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
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
    protected void drawBackground(MatrixStack matrixStack, float v, int i, int i1) {
        this.client.getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(matrixStack, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
            this.drawTexture(matrixStack, x + 99, y + 45 - 26, this.backgroundWidth, 0, 28, 21);
        }
    }
}

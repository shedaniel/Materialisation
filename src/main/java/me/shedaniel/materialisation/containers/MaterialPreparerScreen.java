package me.shedaniel.materialisation.containers;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.materialisation.ModReference;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

public class MaterialPreparerScreen extends AbstractContainerScreen<MaterialPreparerContainer> {
    
    private static final Identifier BG_TEX = new Identifier(ModReference.MOD_ID, "textures/gui/container/material_preparer.png");
    
    public MaterialPreparerScreen(MaterialPreparerContainer container, PlayerInventory inventory, Component title) {
        super(container, inventory, title);
    }
    
    @Override
    protected void init() {
        this.containerHeight = 140;
        super.init();
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }
    
    @Override
    protected void drawForeground(int int_1, int int_2) {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.font.draw(this.title.getFormattedText(), 6f, 6f, 4210752);
        GlStateManager.enableLighting();
    }
    
    @Override
    protected void drawBackground(float v, int i, int i1) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BG_TEX);
        this.blit(left, top, 0, 0, this.containerWidth, this.containerHeight);
        if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
            this.blit(left + 99, top + 45 - 26, this.containerWidth, 0, 28, 21);
        }
    }
    
    @Override
    public void render(int int_1, int int_2, float float_1) {
        renderBackground();
        super.render(int_1, int_2, float_1);
        this.drawMouseoverTooltip(int_1, int_2);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }
    
}

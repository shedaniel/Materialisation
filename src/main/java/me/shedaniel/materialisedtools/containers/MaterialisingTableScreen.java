package me.shedaniel.materialisedtools.containers;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.materialisedtools.MaterialisedReference;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

public class MaterialisingTableScreen extends AbstractContainerScreen<MaterialisingTableContainer> {
    
    private static final Identifier BG_TEX = new Identifier(MaterialisedReference.MOD_ID, "textures/gui/container/materialising_table.png");
    
    public MaterialisingTableScreen(MaterialisingTableContainer container, PlayerInventory inventory, Component title) {
        super(container, inventory, title);
    }
    
    @Override
    protected void drawBackground(float v, int i, int i1) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BG_TEX);
        int int_3 = this.left;
        int int_4 = (this.height - this.containerHeight) / 2;
        this.blit(int_3, int_4, 0, 0, this.containerWidth, this.containerHeight);
    }
    
    @Override
    public void render(int int_1, int int_2, float float_1) {
        renderBackground();
        super.render(int_1, int_2, float_1);
    }
    
}

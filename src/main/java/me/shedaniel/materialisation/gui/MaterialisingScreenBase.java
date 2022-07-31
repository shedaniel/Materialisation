package me.shedaniel.materialisation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@SuppressWarnings("ConstantConditions")
@Environment(EnvType.CLIENT)
public class MaterialisingScreenBase<T extends AbstractMaterialisingHandlerBase> extends HandledScreen<T> implements ScreenHandlerListener {
    private Identifier texture;
    
    public MaterialisingScreenBase(T handler, PlayerInventory inventory, Text title, Identifier texture) {
        super(handler, inventory, title);
        this.texture = texture;
    }
    
    protected void setup() {
    }
    
    @Override
    protected void init() {
        super.init();
        this.setup();
        this.handler.addListener(this);
    }
    
    @Override
    public void removed() {
        super.removed();
        this.handler.removeListener(this);
    }
    
    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
    
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.texture);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(matrices, i + 59, j + 20, 0, this.backgroundHeight + (this.handler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
            this.drawTexture(matrices, i + 99, j + 45, this.backgroundWidth, 0, 28, 21);
        }
    }

/*
    @Override
    public void onHandlerRegistered(ScreenHandler handler) {
        this.onSlotUpdate(handler, 0, handler.getSlot(0).getStack());
    }

 */
    
    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }
    
    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
    }
}

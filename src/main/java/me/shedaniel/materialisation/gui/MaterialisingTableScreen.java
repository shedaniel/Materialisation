package me.shedaniel.materialisation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.ModReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@SuppressWarnings("ConstantConditions")
@Environment(EnvType.CLIENT)
public class MaterialisingTableScreen extends MaterialisingScreenBase<MaterialisingTableScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ModReference.MOD_ID, "textures/gui/container/materialising_table.png");
    private TextFieldWidget nameField;
    
    public MaterialisingTableScreen(MaterialisingTableScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, inventory, title, TEXTURE);
        this.titleX = 60;
    }
    
    @Override
    protected void setup() {
        this.client.keyboard.setRepeatEvents(true);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.nameField = new TextFieldWidget(this.textRenderer, x + 38, y + 24, 103, 12, new TranslatableText("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.changeFocus(true);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setHasBorder(false);
        this.nameField.setMaxLength(35);
        this.nameField.setChangedListener(this::onRenamed);
        this.children.add(this.nameField);
        this.setInitialFocus(this.nameField);
    }
    
    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string);
    }
    
    @Override
    public void removed() {
        super.removed();
        this.client.keyboard.setRepeatEvents(false);
        this.handler.removeListener(this);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            assert this.client != null;
            assert this.client.player != null;
            this.client.player.closeHandledScreen();
        }
        return this.nameField.keyPressed(keyCode, scanCode, modifiers) && this.nameField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void onRenamed(String name) {
        if (!name.isEmpty()) {
            String string = name;
            Slot slot_1 = this.handler.getSlot(2);
            if (slot_1 != null && slot_1.hasStack() && !slot_1.getStack().hasCustomName() && name.equals(slot_1.getStack().getName().getString())) {
                string = "";
            }
            
            this.handler.setNewItemName(string);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeString(string);
            ClientSidePacketRegistry.INSTANCE.sendToServer(Materialisation.MATERIALISING_TABLE_RENAME, buf);
        }
    }
    
    protected void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        this.textRenderer.draw(matrixStack, this.title, 6f, 6f, 4210752);
    }
    
    @Override
    protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        this.client.getTextureManager().bindTexture(TEXTURE);
        int posX = x;
        int posY = y;
        this.drawTexture(matrixStack, posX, posY, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawTexture(matrixStack, posX + 34, posY + 20, 0, this.backgroundHeight + (this.handler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
            this.drawTexture(matrixStack, posX + 99, posY + 45, this.backgroundWidth, 0, 28, 21);
        }
        this.nameField.render(matrixStack, mouseY, mouseY, delta);
    }
    
    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 2) {
            this.nameField.setChangedListener(null);
            this.nameField.setText(!handler.getSlot(slotId).hasStack() ? "" : stack.getName().getString());
            this.nameField.setEditable(!stack.isEmpty());
            this.nameField.setChangedListener(this::onRenamed);
        }
    }
}

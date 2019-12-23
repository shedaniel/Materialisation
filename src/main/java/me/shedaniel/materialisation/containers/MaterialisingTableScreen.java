package me.shedaniel.materialisation.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.ModReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class MaterialisingTableScreen extends AbstractContainerScreen<MaterialisingTableContainer> implements ContainerListener {

    private static final Identifier BG_TEX = new Identifier(ModReference.MOD_ID, "textures/gui/container/materialising_table.png");
    private TextFieldWidget nameField;

    public MaterialisingTableScreen(MaterialisingTableContainer container, PlayerInventory inventory, Text title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboard.enableRepeatEvents(true);
        int x = (this.width - this.containerWidth) / 2;
        int y = (this.height - this.containerHeight) / 2;
        this.nameField = new TextFieldWidget(this.font, x + 38, y + 24, 103, 12, I18n.translate("container.repair", new Object[0]));
        this.nameField.setFocusUnlocked(false);
        this.nameField.changeFocus(true);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setHasBorder(false);
        this.nameField.setMaxLength(35);
        this.nameField.setChangedListener(this::onChangeName);
        this.children.add(this.nameField);
        this.container.addListener(this);
        this.setInitialFocus(this.nameField);
    }

    @Override
    public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        String string_1 = this.nameField.getText();
        this.init(minecraftClient_1, int_1, int_2);
        this.nameField.setText(string_1);
    }

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboard.enableRepeatEvents(false);
        this.container.removeListener(this);
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256) {
            this.minecraft.player.closeContainer();
        }
        return !this.nameField.keyPressed(int_1, int_2, int_3) && !this.nameField.isActive() ? super.keyPressed(int_1, int_2, int_3) : true;
    }

    @Override
    protected void drawForeground(int int_1, int int_2) {
        RenderSystem.disableBlend();
        this.font.draw(this.title.asFormattedString(), 6f, 6f, 4210752);
    }

    private void onChangeName(String string_1) {
        if (!string_1.isEmpty()) {
            String string_2 = string_1;
            Slot slot_1 = this.container.getSlot(2);
            if (slot_1 != null && slot_1.hasStack() && !slot_1.getStack().hasCustomName() && string_1.equals(slot_1.getStack().getName().getString())) {
                string_2 = "";
            }

            this.container.setNewItemName(string_2);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeString(string_2);
            ClientSidePacketRegistry.INSTANCE.sendToServer(Materialisation.MATERIALISING_TABLE_RENAME, buf);
        }
    }

    @Override
    protected void drawBackground(float v, int i, int i1) {
        this.minecraft.getTextureManager().bindTexture(BG_TEX);
        int int_3 = x;
        int int_4 = y;
        this.blit(int_3, int_4, 0, 0, this.containerWidth, this.containerHeight);
        this.blit(int_3 + 34, int_4 + 20, 0, this.containerHeight + (this.container.getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((this.container.getSlot(0).hasStack() || this.container.getSlot(1).hasStack()) && !this.container.getSlot(2).hasStack()) {
            this.blit(int_3 + 99, int_4 + 45, this.containerWidth, 0, 28, 21);
        }
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        renderBackground();
        super.render(int_1, int_2, float_1);
        RenderSystem.disableBlend();
        this.nameField.render(int_1, int_2, float_1);
        this.drawMouseoverTooltip(int_1, int_2);
    }

    @Override
    public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
        this.onContainerSlotUpdate(container, 2, container.getSlot(2).getStack());
    }

    @Override
    public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
        if (i == 2) {
            this.nameField.setChangedListener(null);
            this.nameField.setText(!container.getSlot(i).hasStack() ? "" : itemStack.getName().getString());
            this.nameField.setEditable(!itemStack.isEmpty());
            this.nameField.setChangedListener(this::onChangeName);
        }
    }

    @Override
    public void onContainerPropertyUpdate(Container container, int i, int i1) {

    }

}

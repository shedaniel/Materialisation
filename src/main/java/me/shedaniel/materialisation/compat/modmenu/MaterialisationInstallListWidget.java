package me.shedaniel.materialisation.compat.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.Rect2i;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

import java.util.Collections;
import java.util.List;

public class MaterialisationInstallListWidget extends DynamicElementListWidget<MaterialisationInstallListWidget.Entry> {
    private PackEntry selected;

    public MaterialisationInstallListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }

    @Override
    public int getItemWidth() {
        return width - 11;
    }

    @Override
    protected int getScrollbarPosition() {
        return width - 6;
    }

    @Override
    public int addItem(Entry item) {
        return super.addItem(item);
    }

    public void clearItemsPublic() {
        clearItems();
    }

    public static class PackEntry extends Entry {
        private OnlinePack onlinePack;
        private MaterialisationInstallListWidget listWidget;
        private Rect2i bounds;
        private ButtonWidget clickWidget;

        public PackEntry(MaterialisationInstallListWidget listWidget, OnlinePack onlinePack) {
            this.listWidget = listWidget;
            this.onlinePack = onlinePack;
            this.clickWidget = new ButtonWidget(0, 0, 100, 20, I18n.translate("config.button.materialisation.download"), var1 -> {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                MinecraftClient.getInstance().openScreen(new ConfirmChatLinkScreen(t -> {
                    if (t)
                        SystemUtil.getOperatingSystem().open(onlinePack.download);
                    MinecraftClient.getInstance().openScreen(screen);
                }, onlinePack.download, true));
            });
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            this.bounds = new Rect2i(x, y, entryWidth, entryHeight);
            if (listWidget.visible && listWidget.selected == this) {
                int itemMinX = listWidget.left + listWidget.width / 2 - listWidget.getItemWidth() / 2;
                int itemMaxX = itemMinX + listWidget.getItemWidth();
                int itemY = y;
                GlStateManager.disableTexture();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBufferBuilder();
                float float_2 = listWidget.isFocused() ? 1.0F : 0.5F;
                GlStateManager.color4f(float_2, float_2, float_2, 1.0F);
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex((double) itemMinX, (double) (itemY + getItemHeight() + 2), 0.0D).next();
                buffer.vertex((double) itemMaxX, (double) (itemY + getItemHeight() + 2), 0.0D).next();
                buffer.vertex((double) itemMaxX, (double) (itemY - 2), 0.0D).next();
                buffer.vertex((double) itemMinX, (double) (itemY - 2), 0.0D).next();
                tessellator.draw();
                GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex((double) (itemMinX + 1), (double) (itemY + getItemHeight() + 1), 0.0D).next();
                buffer.vertex((double) (itemMaxX - 1), (double) (itemY + getItemHeight() + 1), 0.0D).next();
                buffer.vertex((double) (itemMaxX - 1), (double) (itemY - 1), 0.0D).next();
                buffer.vertex((double) (itemMinX + 1), (double) (itemY - 1), 0.0D).next();
                tessellator.draw();
                GlStateManager.enableTexture();
            }
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            drawString(font, "§l§n" + onlinePack.displayName, x + 5, y + 5, 16777215);
            int i = 0;
            if (onlinePack.description != null)
                for (String string : MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(onlinePack.description, entryWidth)) {
                    drawString(font, "§7" + string, x + 5, y + 7 + 9 + i * 9, 16777215);
                    i++;
                    if (i > 1)
                        break;
                }
            clickWidget.x = x + entryWidth - 110;
            clickWidget.y = y + entryHeight / 2 - 10;
            clickWidget.render(mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            boolean a = super.mouseClicked(double_1, double_2, int_1);
            if (bounds.contains((int) double_1, (int) double_2) && int_1 == 0) {
                if (!a)
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                listWidget.selected = this;
            }
            return a;
        }

        @Override
        public int getItemHeight() {
            return 39;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(clickWidget);
        }
    }

    public static class LoadingEntry extends Entry {
        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            String string_3;
            switch ((int) (SystemUtil.getMeasuringTimeMs() / 300L % 4L)) {
                case 0:
                default:
                    string_3 = "O o o";
                    break;
                case 1:
                case 3:
                    string_3 = "o O o";
                    break;
                case 2:
                    string_3 = "o o O";
            }
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            drawCenteredString(font, I18n.translate("config.text.materialisation.loading_packs"), x + entryWidth / 2, y + 5, 16777215);
            drawCenteredString(font, string_3, x + entryWidth / 2, y + 5 + 9, 8421504);
        }

        @Override
        public int getItemHeight() {
            return 20;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    public static class FailedEntry extends Entry {
        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            drawCenteredString(font, I18n.translate("config.text.materialisation.failed"), x + entryWidth / 2, y + 5, 16777215);
        }

        @Override
        public int getItemHeight() {
            return 11;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    public static class EmptyEntry extends Entry {
        private int height;

        public EmptyEntry(int height) {
            this.height = height;
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {

        }

        @Override
        public int getItemHeight() {
            return height;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    public static abstract class Entry extends DynamicElementListWidget.ElementEntry<Entry> {

    }
}

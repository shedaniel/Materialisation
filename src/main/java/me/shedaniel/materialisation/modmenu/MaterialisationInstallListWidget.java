package me.shedaniel.materialisation.modmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.materialisation.config.ConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.TextCollector;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
            this.clickWidget = new ButtonWidget(0, 0, 100, 20, new TranslatableText("config.button.materialisation.download"), var1 -> {
                MaterialisationInstallScreen screen = (MaterialisationInstallScreen) MinecraftClient.getInstance().currentScreen;
                MinecraftClient.getInstance().openScreen(new MaterialisationDownloadingScreen(new TranslatableText("message.materialisation.fetching_file_data"), downloadingScreen -> {
                    long size;
                    String textSize;
                    String name;
                    URL url;
                    File file;
                    try {
                        url = new URL(onlinePack.download);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("HEAD");
                        size = connection.getContentLengthLong();
                        name = FilenameUtils.getName(url.getPath());
                        if (size <= 0) textSize = "0B";
                        else {
                            final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
                            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
                            textSize = new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
                        }
                        file = new File(ConfigHelper.MATERIALS_DIRECTORY, name);
                        if (file.exists()) throw new FileAlreadyExistsException("File already exists!");
                    } catch (Throwable e) {
                        downloadingScreen.queueNewScreen(new MaterialisationErrorInstallScreen(screen.getParent(), e));
                        return;
                    }
                    downloadingScreen.queueNewScreen(new ConfirmScreen(t -> {
                        if (t) {
                            MinecraftClient.getInstance().openScreen(new MaterialisationDownloadingScreen(new TranslatableText("message.materialisation.file_is_downloading"), screen1 -> {
                                try {
                                    FileUtils.copyURLToFile(url, file);
                                    screen1.queueNewScreen(new MaterialisationSimpleMessageScreen(screen.getParent(), new TranslatableText("message.materialisation.file_downloaded"), I18n.translate("message.materialisation.file_is_downloaded")));
                                } catch (Exception e) {
                                    screen1.queueNewScreen(new MaterialisationErrorInstallScreen(screen.getParent(), e));
                                }
                            }));
                            return;
                        }
                        MinecraftClient.getInstance().openScreen(screen);
                    }, new TranslatableText("message.materialisation.do_you_want_to_download"), new TranslatableText("message.materialisation.download_file_details", name, textSize)));
                }));
            });
        }
        
        @Override
        public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            this.bounds = new Rect2i(x, y, entryWidth, entryHeight);
            if (listWidget.selectionVisible && listWidget.selected == this) {
                int itemMinX = listWidget.left + listWidget.width / 2 - listWidget.getItemWidth() / 2;
                int itemMaxX = itemMinX + listWidget.getItemWidth();
                RenderSystem.disableTexture();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                float float_2 = listWidget.isFocused() ? 1.0F : 0.5F;
                RenderSystem.color4f(float_2, float_2, float_2, 1.0F);
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex(itemMinX, y + getItemHeight() + 2, 0.0D).next();
                buffer.vertex(itemMaxX, y + getItemHeight() + 2, 0.0D).next();
                buffer.vertex(itemMaxX, y - 2, 0.0D).next();
                buffer.vertex(itemMinX, y - 2, 0.0D).next();
                tessellator.draw();
                RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex(itemMinX + 1, y + getItemHeight() + 1, 0.0D).next();
                buffer.vertex(itemMaxX - 1, y + getItemHeight() + 1, 0.0D).next();
                buffer.vertex(itemMaxX - 1, y - 1, 0.0D).next();
                buffer.vertex(itemMinX + 1, y - 1, 0.0D).next();
                tessellator.draw();
                RenderSystem.enableTexture();
            }
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            font.draw(stack, "§l§n" + onlinePack.displayName, x + 5, y + 5, 16777215);
            int i = 0;
            if (onlinePack.description != null)
                for (StringRenderable text : MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(new LiteralText(onlinePack.description), entryWidth)) {
                    font.draw(stack, MaterialisationCloth.color(text, Formatting.GRAY), x + 5, y + 7 + 9 + i * 9, 16777215);
                    i++;
                    if (i > 1)
                        break;
                }
            clickWidget.x = x + entryWidth - 110;
            clickWidget.y = y + entryHeight / 2 - 10;
            clickWidget.render(stack, mouseX, mouseY, delta);
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
        public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            String string_3;
            switch ((int) (Util.getMeasuringTimeMs() / 300L % 4L)) {
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
            drawCenteredText(stack, font, new TranslatableText("config.text.materialisation.loading_packs"), x + entryWidth / 2, y + 5, 16777215);
            drawCenteredString(stack, font, string_3, x + entryWidth / 2, y + 5 + 9, 8421504);
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
        public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            drawCenteredText(stack, font, new TranslatableText("config.text.materialisation.failed"), x + entryWidth / 2, y + 5, 16777215);
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
        public void render(MatrixStack matrixStack, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
            
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

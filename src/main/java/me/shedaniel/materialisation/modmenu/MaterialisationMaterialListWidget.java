package me.shedaniel.materialisation.modmenu;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"CanBeFinal", "unused"})
public class MaterialisationMaterialListWidget extends DynamicElementListWidget<MaterialisationMaterialListWidget.Entry> {
    public MaterialisationMaterialListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }

    @Override
    public int getItemWidth() {
        return width - 11;
    }

    @Override
    protected int getScrollbarPosition() {
        return left + width - 6;
    }

    @Override
    public int addItem(Entry item) {
        return super.addItem(item);
    }

    public abstract static class PackEntry extends Entry {
        private PackWidget widget;
        private ConfigPackInfo packInfo;

        public PackEntry(ConfigPackInfo packInfo) {
            this.widget = new PackWidget();
            this.packInfo = packInfo;
        }

        @Override
        public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            widget.bounds = new Rect2i(x, y, entryWidth, getItemHeight());
            widget.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public int getItemHeight() {
            return 40;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(widget);
        }

        public abstract void onClick();

        public class PackWidget implements Element, Drawable {
            private Rect2i bounds;
            private boolean focused;

            @Override
            public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {

                fill(stack, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0x15FFFFFF);
                boolean isHovered = focused || bounds.contains(mouseX, mouseY);
                MinecraftClient.getInstance().textRenderer.draw(stack, (isHovered ? Formatting.UNDERLINE.toString() : "") + packInfo.getDisplayName(), bounds.getX() + 5, bounds.getY() + 6, 16777215);
                Iterator<OrderedText> var7 = MinecraftClient.getInstance().textRenderer.wrapLines(new LiteralText(trimEndNewlines(packInfo.getDescription())), bounds.getWidth() - 10).stream().limit(2).iterator();
                int int_2 = bounds.getY() + 6 + 11;
                for (int lolWot = 0; var7.hasNext(); int_2 += 9) {
                    OrderedText string_2 = var7.next();
                    float float_1 = (float) (bounds.getX() + 5);
                    if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
                        int int_5 = MinecraftClient.getInstance().textRenderer.getWidth(string_2);
                        float_1 += (float) (bounds.getWidth() - 10 - int_5);
                    }
                    MinecraftClient.getInstance().textRenderer.draw(stack, string_2, float_1, (float) int_2, 0xEEFFFFFF);
                }
            }

            @Override
            public boolean mouseClicked(double double_1, double double_2, int int_1) {
                if (int_1 == 0) {
                    boolean boolean_1 = bounds.contains((int) double_1, (int) double_2);
                    if (boolean_1) {
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        onClick();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean keyPressed(int int_1, int int_2, int int_3) {
                if (int_1 != 257 && int_1 != 32 && int_1 != 335) {
                    return false;
                } else {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    onClick();
                    return true;
                }
            }

            private String trimEndNewlines(String s) {
                while (s != null && s.endsWith("\n")) {
                    s = s.substring(0, s.length() - 1);
                }

                return s;
            }

            @Override
            public boolean changeFocus(boolean boolean_1) {
                this.focused = !this.focused;
                return this.focused;
            }
        }
    }

    public static abstract class MaterialEntry extends Entry {
        private MaterialWidget widget;
        private PartMaterial partMaterial;

        public MaterialEntry(PartMaterial partMaterial) {
            this.widget = new MaterialWidget();
            this.partMaterial = partMaterial;
        }

        @Override
        public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            widget.bounds = new Rect2i(x, y, entryWidth, getItemHeight());
            widget.render(stack, mouseX, mouseY, delta);
        }

        @Override
        public int getItemHeight() {
            return 17;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(widget);
        }

        public abstract void onClick();

        public class MaterialWidget implements Element, Drawable {
            private Rect2i bounds;
            private boolean focused;

            @Override
            public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
                boolean isHovered = focused || bounds.contains(mouseX, mouseY);
                MinecraftClient.getInstance().textRenderer.draw(stack, (isHovered ? Formatting.UNDERLINE.toString() : "") + I18n.translate(partMaterial.getMaterialTranslateKey()), bounds.getX() + 5, bounds.getY() + 5, 16777215);
            }

            @Override
            public boolean mouseClicked(double double_1, double double_2, int int_1) {
                if (int_1 == 0) {
                    boolean boolean_1 = bounds.contains((int) double_1, (int) double_2);
                    if (boolean_1) {
                        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        onClick();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean keyPressed(int int_1, int int_2, int int_3) {
                if (int_1 != 257 && int_1 != 32 && int_1 != 335) {
                    return false;
                } else {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    onClick();
                    return true;
                }
            }

            @Override
            public boolean changeFocus(boolean boolean_1) {
                this.focused = !this.focused;
                return this.focused;
            }
        }
    }

    public static abstract class Entry extends DynamicElementListWidget.ElementEntry<Entry> {

    }
}

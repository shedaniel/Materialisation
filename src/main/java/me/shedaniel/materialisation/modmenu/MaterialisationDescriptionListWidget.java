package me.shedaniel.materialisation.modmenu;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialisationDescriptionListWidget extends DynamicElementListWidget<MaterialisationDescriptionListWidget.Entry> {
    public MaterialisationDescriptionListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
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

    public void addPack(ConfigPackInfo packInfo, MaterialsPack materialsPack) {
        clearItems();
        addItem(new TextEntry(new LiteralText(packInfo.getDisplayName()).formatted(Formatting.UNDERLINE, Formatting.BOLD)));
        addItem(new EmptyEntry(5));
        if (packInfo.getAuthors().isEmpty())
            addItem(new TextEntry(new TranslatableText("config.text.materialisation.author", I18n.translate("config.text.materialisation.no_one")).formatted(Formatting.GRAY)));
        else
            addItem(new TextEntry(new TranslatableText("config.text.materialisation.author", String.join(", ", packInfo.getAuthors())).formatted(Formatting.GRAY)));
        addItem(new TextEntry(new TranslatableText("config.text.materialisation.version", packInfo.getVersion().getFriendlyString()).formatted(Formatting.GRAY)));
        addItem(new TextEntry(new TranslatableText("config.text.materialisation.identifier", packInfo.getIdentifier().toString()).formatted(Formatting.GRAY)));
        if (!packInfo.getDescription().isEmpty()) {
            for (String string : MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(packInfo.getDescription(), getItemWidth())) {
                addItem(new TextEntry(new LiteralText(string).formatted(Formatting.GRAY)));
            }
        }
        addItem(new EmptyEntry(11));
        for (String string : MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(new TranslatableText("config.text.materialisation.materials", materialsPack.getKnownMaterials().count(), materialsPack.getKnownMaterials().map(PartMaterial::getMaterialTranslateKey).map(I18n::translate).collect(Collectors.joining(", "))).asFormattedString(), getItemWidth())) {
            addItem(new TextEntry(new LiteralText(string).formatted(Formatting.GRAY)));
        }
    }

    public void addMaterial(PartMaterial partMaterial) {
        clearItems();
        addItem(new TextEntry(new TranslatableText(partMaterial.getMaterialTranslateKey()).formatted(Formatting.UNDERLINE, Formatting.BOLD)));
        addItem(new EmptyEntry(5));
        DecimalFormat df = new DecimalFormat("#.##");
        addItem(new ColorEntry(I18n.translate("config.text.materialisation.color"), partMaterial.isBright() ? new Color(partMaterial.getToolColor()) : new Color(partMaterial.getToolColor()).darker().darker()));
        addItem(new TextEntry(new TranslatableText("config.text.materialisation.identifier", partMaterial.getIdentifier().toString()).formatted(Formatting.GRAY)));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.enchantability", partMaterial.getEnchantability())));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.durability", partMaterial.getToolDurability())));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.mining_level", partMaterial.getMiningLevel())));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.tool_speed", df.format(partMaterial.getToolSpeed()))));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.attack_damage", df.format(partMaterial.getAttackDamage()))));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.tool_speed_multiplier", df.format(partMaterial.getBreakingSpeedMultiplier()))));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.durability_multiplier", df.format(partMaterial.getDurabilityMultiplier()))));
    }

    public static class ColorEntry extends Entry {
        private String s;
        private Color color;

        public ColorEntry(String s, Color color) {
            this.s = s;
            this.color = color;
        }

        public ColorEntry(Text text, Color color) {
            this.s = text.asFormattedString();
            this.color = color;
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            int i = MinecraftClient.getInstance().textRenderer.drawWithShadow(s, x, y, 16777215);
            fillGradient(i + 1, y + 1, i + 1 + entryHeight, y + 1 + entryHeight, color.getRGB(), color.getRGB());
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

    public static class TextEntry extends Entry {
        private String s;

        public TextEntry(String s) {
            this.s = s;
        }

        public TextEntry(Text text) {
            this.s = text.asFormattedString();
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(s, x, y, 16777215);
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

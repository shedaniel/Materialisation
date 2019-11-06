package me.shedaniel.materialisation.compat.modmenu;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
        return left + width - 6;
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

    public void addMaterial(MaterialisationMaterialsScreen og, PartMaterial partMaterial) {
        clearItems();
        addItem(new TitleMaterialOverrideEntry(og, partMaterial, new TranslatableText(partMaterial.getMaterialTranslateKey()).formatted(Formatting.UNDERLINE, Formatting.BOLD)));
        DecimalFormat df = new DecimalFormat("#.##");
        addItem(new ColorEntry(I18n.translate("config.text.materialisation.color"), partMaterial.isBright() ? partMaterial.getToolColor() : darkerColor(darkerColor(partMaterial.getToolColor()))));
        addItem(new TextEntry(new TranslatableText("config.text.materialisation.identifier", partMaterial.getIdentifier().toString()).formatted(Formatting.GRAY)));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.enchantability", partMaterial.getEnchantability())));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.durability", partMaterial.getToolDurability())));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.mining_level", partMaterial.getMiningLevel())));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.tool_speed", df.format(partMaterial.getToolSpeed()))));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.attack_damage", df.format(partMaterial.getAttackDamage()))));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.tool_speed_multiplier", df.format(partMaterial.getMiningSpeedMultiplier()))));
        addItem(new TextEntry(I18n.translate("config.text.materialisation.durability_multiplier", df.format(partMaterial.getDurabilityMultiplier()))));
    }

    public int darkerColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int a = (color >> 24) & 0xFF;
        return ((a & 0xFF) << 24) |
                ((Math.max((int) (r * 0.7), 0) & 0xFF) << 16) |
                ((Math.max((int) (g * 0.7), 0) & 0xFF) << 8) |
                ((Math.max((int) (b * 0.7), 0) & 0xFF) << 0);
    }

    public static class ColorEntry extends Entry {
        private String s;
        private int color;

        public ColorEntry(String s, int color) {
            this.s = s;
            this.color = color;
        }

        public ColorEntry(Text text, int color) {
            this.s = text.asFormattedString();
            this.color = color;
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            int i = MinecraftClient.getInstance().textRenderer.drawWithShadow(s, x, y, 16777215);
            fillGradient(i + 1, y + 1, i + 1 + entryHeight, y + 1 + entryHeight, color, color);
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

    public static class TitleMaterialOverrideEntry extends Entry {
        protected String s;
        private ButtonWidget overrideButton;

        public TitleMaterialOverrideEntry(MaterialisationMaterialsScreen og, PartMaterial partMaterial, Text text) {
            this.s = text.asFormattedString();
            String btnText = I18n.translate("config.button.materialisation.create_override");
            overrideButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(btnText) + 10, 20, btnText, widget -> {
                MinecraftClient.getInstance().openScreen(new MaterialisationCreateOverrideNameScreen(og, MinecraftClient.getInstance().currentScreen, partMaterial));
            });
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(s, x, y + 10, 16777215);
            overrideButton.x = x + entryWidth - overrideButton.getWidth();
            overrideButton.y = y;
            overrideButton.render(mouseX, mouseY, delta);
        }

        @Override
        public int getItemHeight() {
            return 21;
        }

        @Override
        public List<? extends Element> children() {
            return Collections.singletonList(overrideButton);
        }
    }

    public static class TextEntry extends Entry {
        protected String s;

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

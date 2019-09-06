package me.shedaniel.materialisation.modmenu;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class MaterialisationCreateOverrideListWidget extends DynamicElementListWidget<MaterialisationCreateOverrideListWidget.EditEntry> {

    public MaterialisationCreateOverrideListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }

    @Override
    public int getItemWidth() {
        return width - 40;
    }

    @Override
    protected int getScrollbarPosition() {
        return left + width - 6;
    }

    @Override
    public int addItem(MaterialisationCreateOverrideListWidget.EditEntry item) {
        return super.addItem(item);
    }

    public void clearItemsPublic() {
        clearItems();
    }

    public static abstract class EditEntry extends DynamicElementListWidget.ElementEntry<EditEntry> {
        protected String s;
        private String display;
        private boolean edited = false;

        public EditEntry(String s) {
            this.s = s;
            this.display = I18n.translate("config.materialisation.value." + s);
        }

        public boolean isEdited() {
            return edited;
        }

        public void setEdited(boolean edited) {
            this.edited = edited;
        }

        @Override
        public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(isEdited() && !isValid() ? "§c§o" + display : isEdited() ? "§o" + display : "§7" + display, x, y + 5, 16777215);
        }

        @Override
        public int getItemHeight() {
            return 24;
        }

        public String getFieldName() {
            return s;
        }

        public String getDisplay() {
            return display;
        }

        public abstract String getDefaultValueString();

        public abstract String getValueString();

        public abstract Object getValue();

        public abstract boolean isValid();
    }

}

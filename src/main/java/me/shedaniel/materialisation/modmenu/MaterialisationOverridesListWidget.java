package me.shedaniel.materialisation.modmenu;

import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class MaterialisationOverridesListWidget extends DynamicElementListWidget<MaterialisationOverridesListWidget.Entry> {
    public MaterialisationOverridesListWidget(MinecraftClient client, int width, int height, int top, int bottom, Identifier backgroundLocation) {
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
    public int addItem(Entry item) {
        return super.addItem(item);
    }

    @SuppressWarnings("unused")
    public void clearItemsPublic() {
        clearItems();
    }
    
    @SuppressWarnings("CanBeFinal")
    public static class TextEntry extends MaterialisationOverridesListWidget.Entry {
        protected Text s;
        
        public TextEntry(Text text) {
            this.s = text;
        }
        
        @Override
        public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(stack, s, x, y, 16777215);
        }
        
        @Override
        public int getItemHeight() {
            return 11;
        }

        @Override
        public List<? extends Selectable> narratables() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }
    
    public static abstract class Entry extends DynamicElementListWidget.ElementEntry<Entry> {
        
    }
    
}

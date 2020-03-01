package me.shedaniel.materialisation.modmenu.entries;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.modmenu.MaterialisationCreateOverrideListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.List;

public class BooleanEditEntry extends MaterialisationCreateOverrideListWidget.EditEntry {
    
    private boolean defaultValue;
    private boolean value;
    private ButtonWidget buttonWidget, resetButton;
    private List<Element> widgets;
    
    public BooleanEditEntry(String s, boolean defaultValue) {
        super(s);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, "", widget -> {
            BooleanEditEntry.this.value = !BooleanEditEntry.this.value;
            BooleanEditEntry.this.setEdited(true);
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate("text.cloth-config.reset_value")) + 6, 20, I18n.translate("text.cloth-config.reset_value"), widget -> {
            BooleanEditEntry.this.value = BooleanEditEntry.this.defaultValue;
            BooleanEditEntry.this.setEdited(false);
        });
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.resetButton.y = y;
        this.buttonWidget.y = y;
        this.resetButton.x = x + entryWidth - resetButton.getWidth();
        this.buttonWidget.x = x + entryWidth - 150;
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        this.buttonWidget.setMessage(value ? "§aYes" : "§cNo");
        resetButton.render(mouseX, mouseY, delta);
        buttonWidget.render(mouseX, mouseY, delta);
    }
    
    @Override
    public String getDefaultValueString() {
        return defaultValue ? "§aYes" : "§cNo";
    }
    
    @Override
    public String getValueString() {
        return value ? "§aYes" : "§cNo";
    }
    
    @Override
    public Boolean getValue() {
        return value;
    }
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
}

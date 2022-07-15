package me.shedaniel.materialisation.modmenu.entries;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.modmenu.MaterialisationCreateOverrideListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.List;

@SuppressWarnings("CanBeFinal")
public class BooleanEditEntry extends MaterialisationCreateOverrideListWidget.EditEntry {

    private boolean defaultValue;
    private boolean value;
    private ButtonWidget buttonWidget, resetButton;
    private List<Element> widgets;
    
    public BooleanEditEntry(String s, boolean defaultValue) {
        super(s);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, NarratorManager.EMPTY, widget -> {
            BooleanEditEntry.this.value = !BooleanEditEntry.this.value;
            BooleanEditEntry.this.setEdited(true);
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(new TranslatableText("text.cloth-config.reset_value")) + 6, 20, new TranslatableText("text.cloth-config.reset_value"), widget -> {
            BooleanEditEntry.this.value = BooleanEditEntry.this.defaultValue;
            BooleanEditEntry.this.setEdited(false);
        });
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(stack, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.resetButton.y = y;
        this.buttonWidget.y = y;
        this.resetButton.x = x + entryWidth - resetButton.getWidth();
        this.buttonWidget.x = x + entryWidth - 150;
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        this.buttonWidget.setMessage(new LiteralText(value ? "§aYes" : "§cNo"));
        resetButton.render(stack, mouseX, mouseY, delta);
        buttonWidget.render(stack, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Selectable> narratables() {
        return null;
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

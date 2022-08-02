package me.shedaniel.materialisation.modmenu.entries;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.modmenu.MaterialisationCreateOverrideListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("CanBeFinal")
public class StringEditEntry extends MaterialisationCreateOverrideListWidget.EditEntry {
    
    private String defaultValue;
    private Pattern validation;
    private TextFieldWidget buttonWidget;
    private ButtonWidget resetButton;
    private List<Element> widgets;
    
    public StringEditEntry(String s, String defaultValue) {
        this(s, defaultValue, null);
    }
    
    public StringEditEntry(String s, String defaultValue, Pattern validation) {
        super(s);
        this.defaultValue = defaultValue;
        this.validation = validation;
        this.buttonWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 16, NarratorManager.EMPTY) {
            @Override
            public void render(MatrixStack stack, int int_1, int int_2, float float_1) {
                setEditableColor(isValid() ? 0xe0e0e0 : 0xff5555);
                super.render(stack, int_1, int_2, float_1);
            }
        };
        buttonWidget.setMaxLength(1000);
        buttonWidget.setText(defaultValue);
        buttonWidget.setChangedListener(ss -> StringEditEntry.this.setEdited(!ss.equals(defaultValue)));
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(new TranslatableText("text.cloth-config.reset_value")) + 6, 20, new TranslatableText("text.cloth-config.reset_value"), widget -> {
            buttonWidget.setText(StringEditEntry.this.defaultValue);
            StringEditEntry.this.setEdited(false);
        });
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(stack, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.resetButton.y = y;
        this.buttonWidget.y = y + 2;
        this.resetButton.x = x + entryWidth - resetButton.getWidth();
        this.buttonWidget.x = x + entryWidth - 150 + 2;
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2 - 4);
        resetButton.render(stack, mouseX, mouseY, delta);
        buttonWidget.render(stack, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Selectable> narratables() {
        return Collections.emptyList();
    }

    @Override
    public String getDefaultValueString() {
        return defaultValue;
    }
    
    @Override
    public String getValueString() {
        return buttonWidget.getText();
    }
    
    @Override
    public String getValue() {
        return buttonWidget.getText();
    }
    
    @Override
    public boolean isValid() {
        return validation == null || validation.matcher(getValue()).matches();
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
}

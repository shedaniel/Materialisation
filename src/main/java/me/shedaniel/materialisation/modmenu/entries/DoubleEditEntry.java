package me.shedaniel.materialisation.modmenu.entries;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.modmenu.MaterialisationCreateOverrideListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.List;

public class DoubleEditEntry extends MaterialisationCreateOverrideListWidget.EditEntry {
    
    private double defaultValue;
    private TextFieldWidget buttonWidget;
    private ButtonWidget resetButton;
    private List<Element> widgets;
    private ParsePosition parsePosition = new ParsePosition(0);
    private static final DecimalFormat DF = new DecimalFormat("#.##");
    
    public DoubleEditEntry(String s, double defaultValue) {
        super(s);
        this.defaultValue = defaultValue;
        this.buttonWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 150, 16, NarratorManager.EMPTY) {
            @Override
            public void render(MatrixStack stack, int int_1, int int_2, float float_1) {
                setEditableColor(isValid() ? 0xe0e0e0 : 0xff5555);
                super.render(stack, int_1, int_2, float_1);
            }
        };
        buttonWidget.setMaxLength(1000);
        buttonWidget.setText(DF.format(defaultValue));
        buttonWidget.setChangedListener(ss -> {
            DoubleEditEntry.this.setEdited(!ss.equals(DF.format(defaultValue)));
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(new TranslatableText("text.cloth-config.reset_value")) + 6, 20, new TranslatableText("text.cloth-config.reset_value"), widget -> {
            buttonWidget.setText(DF.format(defaultValue));
            DoubleEditEntry.this.setEdited(false);
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
    public String getDefaultValueString() {
        return DF.format(defaultValue);
    }
    
    @Override
    public String getValueString() {
        return DF.format(getValue());
    }
    
    @Override
    public Double getValue() {
        parsePosition.setIndex(0);
        Number value = DF.parse(buttonWidget.getText(), parsePosition);
        if (parsePosition.getIndex() != 0) {
            return value.doubleValue();
        }
        return defaultValue;
    }
    
    @Override
    public boolean isValid() {
        parsePosition.setIndex(0);
        String text = buttonWidget.getText();
        DF.parse(text, parsePosition);
        return parsePosition.getIndex() == text.length();
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
}

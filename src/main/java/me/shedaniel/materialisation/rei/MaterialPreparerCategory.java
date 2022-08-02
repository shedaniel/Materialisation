package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.gui.MaterialPreparerScreen;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

public class MaterialPreparerCategory implements DisplayCategory<MaterialPreparerDisplay> {
    
    public static final EntryStack<?> LOGO = EntryStacks.of(Materialisation.MATERIAL_PREPARER);
    
    @Override
    public Identifier getIdentifier() {
        return getCategoryIdentifier().getIdentifier();
    }

    @Override
    public CategoryIdentifier<? extends MaterialPreparerDisplay> getCategoryIdentifier() {
        return MaterialisationREIPlugin.MATERIAL_PREPARER;
    }

    @Override
    public Renderer getIcon() {
        return LOGO;
    }

    @Override
    public Text getTitle() {
        return new TranslatableText("category.materialisation.material_preparer");
    }

    @Override
    public List<Widget> setupDisplay(MaterialPreparerDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = Lists.newArrayList(Widgets.createCategoryBase(bounds));
        widgets.add(Widgets.createTexturedWidget(MaterialPreparerScreen.getTEXTURE(), startPoint.x, startPoint.y, 0, 221, 82, 26));
        widgets.add(Widgets.createSlot(new Point(startPoint.x - 18, startPoint.y + 5)).entry(display.getFirst()).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + 5)).entries(display.getSecond()).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 5)).entry(display.getResult()).disableBackground().markOutput());
        return widgets;
    }
    
    @Override
    public int getDisplayHeight() {
        return 36;
    }

}

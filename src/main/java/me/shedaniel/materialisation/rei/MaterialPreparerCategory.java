package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MaterialPreparerCategory implements RecipeCategory<MaterialPreparerDisplay> {
    
    public static final EntryStack LOGO = EntryStack.create(Materialisation.MATERIAL_PREPARER);
    
    @NotNull
    @Override
    public Identifier getIdentifier() {
        return MaterialisationREIPlugin.MATERIAL_PREPARER;
    }
    
    @NotNull
    @Override
    public String getCategoryName() {
        return I18n.translate("category.materialisation.material_preparer");
    }
    
    @NotNull
    @Override
    public EntryStack getLogo() {
        return LOGO;
    }
    
    @NotNull
    @Override
    public List<Widget> setupDisplay(MaterialPreparerDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = Lists.newArrayList(Widgets.createCategoryBase(bounds));
        widgets.add(Widgets.createTexturedWidget(DefaultPlugin.getDisplayTexture(), startPoint.x, startPoint.y, 0, 221, 82, 26));
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

package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.LabelWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.ScreenHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class MaterialisationModifiersCategory implements RecipeCategory<MaterialisationModifiersDisplay> {
    @Override
    public Identifier getIdentifier() {
        return MaterialisationREIPlugin.MODIFIERS;
    }

    @Override
    public String getCategoryName() {
        return I18n.translate("category.materialisation.modifiers");
    }

    @Override
    public int getDisplayHeight() {
        return 140;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getFixedRecipesPerPage() {
        return 1;
    }

    @Override
    public List<Widget> setupDisplay(Supplier<MaterialisationModifiersDisplay> recipeDisplaySupplier, Rectangle bounds) {
        List<Widget> widgets = new LinkedList<>();
        MaterialisationModifiersDisplay display = recipeDisplaySupplier.get();
        Identifier modifierId = display.getModifierId();
        int level = display.getLevel();
        widgets.add(EntryWidget.create(bounds.x + 2, bounds.y + 2).entries(display.getInputEntries().isEmpty() ? Collections.emptyList() : display.getInputEntries().get(0)));
        String title;
        if (level != 1)
            title = I18n.translate("modifier." + modifierId.getNamespace() + "." + modifierId.getPath()) + " " + level;
        else title = I18n.translate("modifier." + modifierId.getNamespace() + "." + modifierId.getPath());
        widgets.add(new LabelWidget(new Point(bounds.x + 22, bounds.y + 6), title).color(ScreenHelper.isDarkModeEnabled() ? 0xFFBBBBBB : 0xFF404040).leftAligned());
        Modifier modifier = display.getModifier();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int currentY = 24;
        for (String descLine : modifier.getModifierDescription(level)) {
            for (String s : textRenderer.wrapStringToWidthAsList(descLine, bounds.width - 4)) {
                widgets.add(new LabelWidget(new Point(bounds.x + 2, bounds.y + currentY), s).leftAligned().color(ScreenHelper.isDarkModeEnabled() ? 0xFFBBBBBB : 0xFF404040).noShadow());
                currentY += 9;
            }
        }
        return widgets;
    }
}

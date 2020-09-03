package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.modmenu.MaterialisationCloth;
import me.shedaniel.materialisation.utils.RomanNumber;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MaterialisationModifiersCategory implements RecipeCategory<MaterialisationModifiersDisplay> {
    @NotNull
    @Override
    public Identifier getIdentifier() {
        return MaterialisationREIPlugin.MODIFIERS;
    }
    
    @NotNull
    @Override
    public String getCategoryName() {
        return I18n.translate("category.materialisation.modifiers");
    }
    
    @Override
    public int getDisplayHeight() {
        return 140;
    }
    
    @Override
    public int getFixedRecipesPerPage() {
        return 1;
    }
    
    @NotNull
    @Override
    public List<Widget> setupDisplay(MaterialisationModifiersDisplay display, Rectangle bounds) {
        List<Widget> widgets = new LinkedList<>();
        Identifier modifierId = display.getModifierId();
        int level = display.getLevel();
        widgets.add(Widgets.createSlot(new Point(bounds.x + 2, bounds.y + 2)).entries(display.getInputEntries().isEmpty() ? Collections.emptyList() : display.getInputEntries().get(0)).markInput());
        String title;
        if (level != 1 || Objects.requireNonNull(display.getModifier().getGraphicalDescriptionRange()).getLeft() != 1 || display.getModifier().getGraphicalDescriptionRange().getRight() != 1)
            title = I18n.translate("modifier." + modifierId.getNamespace() + "." + modifierId.getPath()) + " " + RomanNumber.toRoman(level);
        else title = I18n.translate("modifier." + modifierId.getNamespace() + "." + modifierId.getPath());
        widgets.add(Widgets.createLabel(new Point(bounds.x + 22, bounds.y + 6), new LiteralText(title)).color(0xFF404040, 0xFFBBBBBB).leftAligned());
        Modifier modifier = display.getModifier();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int currentY = 24;
        for (Text descLine : modifier.getModifierDescription(level)) {
            for (OrderedText s : textRenderer.wrapLines(descLine, bounds.width - 4)) {
                widgets.add(Widgets.createLabel(new Point(bounds.x + 2, bounds.y + currentY), MaterialisationCloth.wrap((Text)s)).leftAligned().color(0xFF404040, 0xFFBBBBBB).noShadow());
                currentY += 9;
            }
        }
        return widgets;
    }
}

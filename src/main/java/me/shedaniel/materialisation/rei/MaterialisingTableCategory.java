package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.CategoryBaseWidget;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public class MaterialisingTableCategory implements RecipeCategory<MaterialisingTableDisplay> {

    public static final EntryStack LOGO = EntryStack.create(Materialisation.MATERIALISING_TABLE);

    @Override
    public Identifier getIdentifier() {
        return MaterialisationREIPlugin.MATERIALISING_TABLE;
    }

    @Override
    public String getCategoryName() {
        return I18n.translate("category.materialisation.materialising_table");
    }

    @Override
    public EntryStack getLogo() {
        return LOGO;
    }

    @Override
    public List<Widget> setupDisplay(Supplier<MaterialisingTableDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = Lists.newArrayList(new CategoryBaseWidget(bounds) {
            @Override
            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                GuiLighting.disable();
                MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultPlugin.getDisplayTexture());
                this.blit(startPoint.x, startPoint.y, 0, 221, 82, 26);
            }
        });
        MaterialisingTableDisplay display = recipeDisplaySupplier.get();
        widgets.add(EntryWidget.create(startPoint.x - 18, startPoint.y + 5).entry(display.getFirst()));
        widgets.add(EntryWidget.create(startPoint.x + 4, startPoint.y + 5).entries(display.getSecond()));
        widgets.add(EntryWidget.create(startPoint.x + 61, startPoint.y + 5).entry(display.getResult()).noBackground());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

}

package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.Renderer;
import me.shedaniel.rei.gui.renderers.ItemStackRenderer;
import me.shedaniel.rei.gui.widget.CategoryBaseWidget;
import me.shedaniel.rei.gui.widget.SlotWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public class MaterialPreparerCategory implements RecipeCategory<MaterialPreparerDisplay> {

    public static final ItemStackRenderer ICON = Renderer.fromItemStack(new ItemStack(Materialisation.MATERIAL_PREPARER));

    @Override
    public Identifier getIdentifier() {
        return MaterialisationREIPlugin.MATERIAL_PREPARER;
    }

    @Override
    public String getCategoryName() {
        return I18n.translate("category.materialisation.material_preparer");
    }

    @Override
    public Renderer getIcon() {
        return ICON;
    }

    @Override
    public boolean checkTags() {
        return true;
    }

    @Override
    public List<Widget> setupDisplay(Supplier<MaterialPreparerDisplay> recipeDisplaySupplier, Rectangle bounds) {
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
        MaterialPreparerDisplay display = recipeDisplaySupplier.get();
        widgets.add(new SlotWidget(startPoint.x + 4 - 22, startPoint.y + 5, Renderer.fromItemStack(display.getFirst()), true, true, true));
        widgets.add(new SlotWidget(startPoint.x + 4, startPoint.y + 5, Renderer.fromItemStacks(display.getSecond()), true, true, true));
        widgets.add(new SlotWidget(startPoint.x + 61, startPoint.y + 5, Renderer.fromItemStack(display.getResult()), false, true, true));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

}

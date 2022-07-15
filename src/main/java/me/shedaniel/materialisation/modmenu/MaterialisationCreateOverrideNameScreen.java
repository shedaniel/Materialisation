package me.shedaniel.materialisation.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.materialisation.api.PartMaterial;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.lwjgl.opengl.GL11;

import java.util.Locale;
import java.util.UUID;

import static me.shedaniel.materialisation.modmenu.MaterialisationMaterialsScreen.overlayBackground;


@SuppressWarnings("CanBeFinal")
public class MaterialisationCreateOverrideNameScreen extends Screen {
    private MaterialisationMaterialsScreen og;
    private Screen parent;
    private PartMaterial partMaterial;
    private TextFieldWidget fileName, priority;
    private String randomFileName;
    private ButtonWidget continueButton;
    
    public MaterialisationCreateOverrideNameScreen(MaterialisationMaterialsScreen og, Screen parent, PartMaterial partMaterial) {
        super(new TranslatableText("config.title.materialisation.new_override"));
        this.og = og;
        this.parent = parent;
        this.partMaterial = partMaterial;
        String s = UUID.randomUUID().toString();
        this.randomFileName = partMaterial.getIdentifier().getPath() + "+" + s.substring(s.lastIndexOf('-') + 1) + ".json";
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256 && this.shouldCloseOnEsc()) {
            assert client != null;
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }
    
    @Override
    protected void init() {
        super.init();
        addSelectableChild(new ButtonWidget(4, 4, 75, 20, new TranslatableText("gui.back"), var1 -> {
            assert client != null;
            client.setScreen(parent);
        }));
        addSelectableChild(continueButton = new ButtonWidget(width - 79, 4, 75, 20, new TranslatableText("config.button.materialisation.continue"), var1 -> {
            assert client != null;
            client.setScreen(new MaterialisationCreateOverrideScreen(og, this, partMaterial, fileName.getText().isEmpty() ? randomFileName : fileName.getText(), priority.getText().isEmpty() ? 0 : Double.parseDouble(priority.getText())));
        }));
        assert client != null;
        addSelectableChild(fileName = new TextFieldWidget(client.textRenderer, width / 4, 50, width / 2, 18, fileName, NarratorManager.EMPTY) {
            @Override
            public void render(MatrixStack stack, int int_1, int int_2, float float_1) {
                if (getText().isEmpty())
                    setSuggestion(randomFileName);
                else setSuggestion(null);
                setEditableColor(!getText().isEmpty() && !getText().toLowerCase(Locale.ROOT).endsWith(".json") ? 16733525 : 14737632);
                super.render(stack, int_1, int_2, float_1);
            }
        });
        addSelectableChild(priority = new TextFieldWidget(client.textRenderer, width / 4, 118, width / 2, 18, priority, NarratorManager.EMPTY) {
            @Override
            public void render(MatrixStack stack, int int_1, int int_2, float float_1) {
                if (getText().isEmpty())
                    setSuggestion("0");
                else setSuggestion(null);
                if (!priority.getText().isEmpty())
                    try {
                        Double.parseDouble(getText());
                        setEditableColor(14737632);
                    } catch (NumberFormatException e) {
                        setEditableColor(16733525);
                    }
                super.render(stack, int_1, int_2, float_1);
            }
        });
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        continueButton.active = fileName.getText().isEmpty() || fileName.getText().toLowerCase(Locale.ROOT).endsWith(".json");
        if (continueButton.active && !priority.getText().isEmpty()) {
            try {
                Double.parseDouble(priority.getText());
            } catch (NumberFormatException e) {
                continueButton.active = false;
            }
        }
        overlayBackground(0, 0, width, 28, 64, 64, 64, 255, 255);
        overlayBackground(0, 28, width, height, 32, 32, 32, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(7425);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(0, 28 + 4, 0.0D).color(0, 0, 0, 0).texture(0.0F, 1.0F).next();
        buffer.vertex(this.width, 28 + 4, 0.0D).color(0, 0, 0, 0).texture(1.0F, 1.0F).next();
        buffer.vertex(this.width, 28, 0.0D).color(0, 0, 0, 255).texture(1.0F, 0.0F).next();
        buffer.vertex(0, 28, 0.0D).color(0, 0, 0, 255).texture(0.0F, 0.0F).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        GL11.glShadeModel(7424);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderSystem.disableBlend();
        drawCenteredText(stack, textRenderer, title, width / 2, 10, 16777215);
        drawTextWithShadow(stack, textRenderer, new TranslatableText("config.text.materialisation.override_json_file_name"), width / 4, 36, -6250336);
        drawTextWithShadow(stack, textRenderer, new TranslatableText("config.text.materialisation.override_json_file_saved"), width / 4, 74, -6250336);
        drawTextWithShadow(stack, textRenderer, new TranslatableText("config.text.materialisation.priority"), width / 4, 104, -6250336);
        super.render(stack, mouseX, mouseY, delta);
    }
}

package me.shedaniel.materialisation.modmenu;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "CanBeFinal"})
public class MaterialisationCreateOverrideConfirmationScreen extends Screen {
    private MaterialisationMaterialsScreen og;
    private Screen parent;
    private PartMaterial partMaterial;
    private File file;
    private MaterialisationOverridesListWidget listWidget;
    private List<MaterialisationCreateOverrideListWidget.EditEntry> editedEntries;
    
    public MaterialisationCreateOverrideConfirmationScreen(MaterialisationMaterialsScreen og, Screen parent, PartMaterial partMaterial, String fileName, double priority, List<MaterialisationCreateOverrideListWidget.EditEntry> entries) {
        super(new TranslatableText("config.title.materialisation.override"));
        this.og = og;
        this.parent = parent;
        this.partMaterial = partMaterial;
        this.file = new File(ConfigHelper.MATERIALS_DIRECTORY, fileName);
        this.editedEntries = entries.stream().filter(MaterialisationCreateOverrideListWidget.EditEntry::isEdited).collect(Collectors.toList());
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
        addSelectableChild(new ButtonWidget(width - 79, 4, 75, 20, new TranslatableText("config.button.materialisation.confirm"), var1 -> {
            if (!ConfigHelper.loading) {
                try {
                    FileWriter fileWriter = new FileWriter(file, false);
                    JsonObject object = new JsonObject();
                    object.addProperty("type", "override");
                    object.addProperty("name", partMaterial.getIdentifier().toString());
                    for (MaterialisationCreateOverrideListWidget.EditEntry entry : editedEntries) {
                        if (entry.getValue() instanceof String)
                            object.addProperty(entry.getFieldName(), (String) entry.getValue());
                        else if (entry.getValue() instanceof Double)
                            object.addProperty(entry.getFieldName(), (Double) entry.getValue());
                        else if (entry.getValue() instanceof Boolean)
                            object.addProperty(entry.getFieldName(), (Boolean) entry.getValue());
                        else System.out.println(entry.getValue().getClass() + " can't be saved");
                    }
                    fileWriter.write(ConfigHelper.GSON.toJson(object));
                    fileWriter.close();
                    MinecraftClient.getInstance().setScreen(new MaterialisationLoadingConfigScreen(og));
                    ConfigHelper.loadConfigAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
        List<MaterialisationOverridesListWidget.Entry> entries = Lists.newArrayList();
        if (listWidget != null) {
            entries = listWidget.children();
        } else {
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new LiteralText(" ")));
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new TranslatableText("config.text.materialisation.create_for", new TranslatableText(partMaterial.getMaterialTranslateKey()))));
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new LiteralText(" ")));
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new TranslatableText("config.text.materialisation.create.changed_amount", editedEntries.size())));
            for (MaterialisationCreateOverrideListWidget.EditEntry entry : editedEntries) {
                entries.add(new MaterialisationOverridesListWidget.TextEntry(new TranslatableText("config.text.materialisation.create.edited", entry.getDisplay(), entry.getDefaultValueString(), entry.getValueString())));
            }
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new LiteralText(" ")));
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new TranslatableText("config.text.materialisation.create.save_to")));
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new LiteralText("  " + file.getName()).formatted(Formatting.GRAY)));
            if (file.exists()) {
                entries.add(new MaterialisationOverridesListWidget.TextEntry(new LiteralText(" ")));
                entries.add(new MaterialisationOverridesListWidget.TextEntry(new TranslatableText("config.text.materialisation.create.file_already_exist_1")));
                entries.add(new MaterialisationOverridesListWidget.TextEntry(new TranslatableText("config.text.materialisation.create.file_already_exist_2")));
            }
            entries.add(new MaterialisationOverridesListWidget.TextEntry(new LiteralText(" ")));
        }
        addDrawableChild(listWidget = new MaterialisationOverridesListWidget(client, width, height, 28, height, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE));
        for (MaterialisationOverridesListWidget.Entry entry : entries) {
            listWidget.addItem(entry);
        }
    }
    
    @Override
    public void render(MatrixStack stack, int int_1, int int_2, float float_1) {
        listWidget.render(stack, int_1, int_2, float_1);
        super.render(stack, int_1, int_2, float_1);
        drawCenteredText(stack, textRenderer, title, width / 2, 10, 16777215);
    }
}

package me.shedaniel.materialisation.modmenu;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.modmenu.entries.BooleanEditEntry;
import me.shedaniel.materialisation.modmenu.entries.DoubleEditEntry;
import me.shedaniel.materialisation.modmenu.entries.IntEditEntry;
import me.shedaniel.materialisation.modmenu.entries.StringEditEntry;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@SuppressWarnings("CanBeFinal")
public class MaterialisationCreateOverrideScreen extends Screen {
    public static final Pattern HEX_COLOR = Pattern.compile("^#([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$");
    private MaterialisationMaterialsScreen og;
    private Screen parent;
    private final PartMaterial partMaterial;
    private String fileName;
    private double priority;
    private MaterialisationCreateOverrideListWidget listWidget;
    private ButtonWidget createButton;
    
    public MaterialisationCreateOverrideScreen(MaterialisationMaterialsScreen og, Screen parent, PartMaterial partMaterial, String fileName, double priority) {
        super(new TranslatableText("config.title.materialisation.override"));
        this.og = og;
        this.parent = parent;
        this.partMaterial = partMaterial;
        this.fileName = fileName;
        this.priority = priority;
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
        addSelectableChild(createButton = new ButtonWidget(width - 79, 4, 75, 20, new TranslatableText("config.button.materialisation.create"), var1 -> {
            assert client != null;
            client.setScreen(new MaterialisationCreateOverrideConfirmationScreen(og, this, partMaterial, fileName, priority, listWidget.children()));
        }));
        List<MaterialisationCreateOverrideListWidget.EditEntry> entries = Lists.newArrayList();
        if (listWidget != null) {
            entries = listWidget.children();
        } else {
            entries.add(new BooleanEditEntry("enabled", true));
            entries.add(new StringEditEntry("toolColor", "#" + toStringColor(partMaterial.getToolColor()), HEX_COLOR));
            entries.add(new IntEditEntry("toolDurability", partMaterial.getToolDurability()));
            entries.add(new IntEditEntry("miningLevel", partMaterial.getMiningLevel()));
            entries.add(new IntEditEntry("enchantability", partMaterial.getEnchantability()));
            entries.add(new DoubleEditEntry("durabilityMultiplier", partMaterial.getDurabilityMultiplier()));
            entries.add(new DoubleEditEntry("breakingSpeedMultiplier", partMaterial.getBreakingSpeedMultiplier()));
            entries.add(new DoubleEditEntry("toolSpeed", partMaterial.getToolSpeed()));
            entries.add(new DoubleEditEntry("attackDamage", partMaterial.getAttackDamage()));
            entries.add(new StringEditEntry("materialTranslationKey", partMaterial.getMaterialTranslateKey()));
            entries.add(new BooleanEditEntry("bright", partMaterial.isBright()));
            entries.add(new IntEditEntry("fullAmount", partMaterial.getFullAmount()));
        }
        addDrawableChild(listWidget = new MaterialisationCreateOverrideListWidget(client, width, height, 28, height, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE));
        for (MaterialisationCreateOverrideListWidget.EditEntry entry : entries) {
            listWidget.addItem(entry);
        }
    }
    
    private String toStringColor(int toolColor) {
        String r = Integer.toHexString((toolColor >> 16) & 0xFF);
        String g = Integer.toHexString((toolColor >> 8) & 0xFF);
        String b = Integer.toHexString((toolColor) & 0xFF);
        String a = Integer.toHexString((toolColor >> 24) & 0xFF);
        if (r.length() == 1)
            r = "0" + r;
        if (g.length() == 1)
            g = "0" + g;
        if (b.length() == 1)
            b = "0" + b;
        if (a.length() == 1)
            a = "0" + a;
        return (a + r + g + b).toUpperCase(Locale.ROOT);
    }
    
    @Override
    public void render(MatrixStack stack, int int_1, int int_2, float float_1) {
        super.render(stack, int_1, int_2, float_1);
        createButton.active = !listWidget.children().isEmpty();
        for (MaterialisationCreateOverrideListWidget.EditEntry child : listWidget.children()) {
            if (!createButton.active)
                break;
            if (child.isEdited() && !child.isValid())
                createButton.active = false;
        }
        boolean edited = false;
        for (MaterialisationCreateOverrideListWidget.EditEntry child : listWidget.children()) {
            if (child.isEdited()) {
                edited = true;
                break;
            }
        }
        if (!edited)
            createButton.active = false;
        listWidget.render(stack, int_1, int_2, float_1);
        drawCenteredText(stack, textRenderer, title, width / 2, 10, 16777215);
    }
}

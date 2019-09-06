package me.shedaniel.materialisation.modmenu;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.shedaniel.materialisation.config.ConfigHelper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.shedaniel.materialisation.modmenu.MaterialisationMaterialsScreen.overlayBackground;

public class MaterialisationInstallScreen extends Screen {
    public static final List<OnlinePack> ONLINE_PACKS = Lists.newArrayList();
    public static boolean loaded = false;
    public static ExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Materialisation"));
    public boolean loading = false;
    private Screen parent;
    private MaterialisationInstallListWidget listWidget;

    protected MaterialisationInstallScreen(Screen parent) {
        super(new TranslatableText("config.title.materialisation.install_new"));
        this.parent = parent;
    }

    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256 && this.shouldCloseOnEsc()) {
            minecraft.openScreen(parent);
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }

    @Override
    protected void init() {
        super.init();
        children.add(listWidget = new MaterialisationInstallListWidget(minecraft, width, height - 28 * 2, 28, height - 28, DrawableHelper.BACKGROUND_LOCATION));
        if (!loaded) {
            loaded = true;
            refresh();
        } else if (loading) {
            setUpRefresh();
        } else if (ONLINE_PACKS.isEmpty()) {
            listWidget.clearItemsPublic();
            listWidget.addItem(new MaterialisationInstallListWidget.EmptyEntry(10));
            listWidget.addItem(new MaterialisationInstallListWidget.FailedEntry());
        } else {
            listWidget.clearItemsPublic();
            listWidget.addItem(new MaterialisationInstallListWidget.EmptyEntry(10));
            for (OnlinePack onlinePack : ONLINE_PACKS) {
                listWidget.addItem(new MaterialisationInstallListWidget.PackEntry(listWidget, onlinePack));
            }
        }
        addButton(new ButtonWidget(4, 4, 100, 20, I18n.translate("config.button.materialisation.refresh"), var1 -> {
            if (!loading)
                refresh();
        }));
        addButton(new ButtonWidget(4, height - 24, 100, 20, I18n.translate("gui.back"), var1 -> {
            minecraft.openScreen(parent);
        }));
        addButton(new ButtonWidget(width - 104, 4, 100, 20, I18n.translate("config.button.materialisation.open_folder"), var1 -> {
            SystemUtil.getOperatingSystem().open(ConfigHelper.MATERIALS_DIRECTORY);
        }));
    }

    public void refresh() {
        loading = true;
        setUpRefresh();
        executorService.shutdown();
        executorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Materialisation"));
        executorService.submit(() -> {
            ONLINE_PACKS.clear();
            try {
                URL url = new URL("https://raw.githubusercontent.com/shedaniel/MaterialisationData/master/packs.json");
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                JsonArray array = gson.fromJson(new InputStreamReader(url.openStream()), JsonArray.class);
                for (JsonElement jsonElement : array) {
                    OnlinePack onlinePack = gson.fromJson(jsonElement, OnlinePack.class);
                    ONLINE_PACKS.add(onlinePack);
                }
                listWidget.clearItemsPublic();
                listWidget.addItem(new MaterialisationInstallListWidget.EmptyEntry(10));
                for (OnlinePack onlinePack : ONLINE_PACKS) {
                    listWidget.addItem(new MaterialisationInstallListWidget.PackEntry(listWidget, onlinePack));
                }
            } catch (Exception e) {
                ONLINE_PACKS.clear();
                e.printStackTrace();
                listWidget.clearItemsPublic();
                listWidget.addItem(new MaterialisationInstallListWidget.EmptyEntry(10));
                listWidget.addItem(new MaterialisationInstallListWidget.FailedEntry());
            }
            loading = false;
        });
    }

    public void setUpRefresh() {
        listWidget.clearItemsPublic();
        listWidget.addItem(new MaterialisationInstallListWidget.EmptyEntry(10));
        listWidget.addItem(new MaterialisationInstallListWidget.LoadingEntry());
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderDirtBackground(0);
        listWidget.render(mouseX, mouseY, delta);
        overlayBackground(0, height - 28, width, height, 64, 64, 64, 255, 255);
        drawCenteredString(font, title.asFormattedString(), width / 2, 10, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}

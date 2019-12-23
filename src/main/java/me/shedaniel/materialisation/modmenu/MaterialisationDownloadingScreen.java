package me.shedaniel.materialisation.modmenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MaterialisationDownloadingScreen extends Screen {

    public static ExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Materialisation"));
    private Screen newScreen;

    public MaterialisationDownloadingScreen(Text title, Consumer<MaterialisationDownloadingScreen> consumer) {
        super(title);
        executorService.shutdown();
        executorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Materialisation"));
        executorService.submit(() -> consumer.accept(this));
    }

    public void queueNewScreen(Screen screen) {
        newScreen = screen;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        if (newScreen != null) {
            minecraft.openScreen(newScreen);
            newScreen = null;
            return;
        }
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, title.asFormattedString(), this.width / 2, this.height / 2 - 50, 16777215);
        String string_3;
        switch ((int) (Util.getMeasuringTimeMs() / 300L % 4L)) {
            case 0:
            default:
                string_3 = "O o o";
                break;
            case 1:
            case 3:
                string_3 = "o O o";
                break;
            case 2:
                string_3 = "o o O";
        }
        this.drawCenteredString(this.font, string_3, this.width / 2, this.height / 2 - 41, 8421504);
        super.render(int_1, int_2, float_1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
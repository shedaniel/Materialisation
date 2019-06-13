package me.shedaniel.materialisation.modmenu;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.materialisation.ModReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SystemUtil;

import java.util.function.Function;

public class MaterialisationModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return ModReference.MOD_ID;
    }
    
    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> new ConfirmChatLinkScreen(t -> {
            if (t)
                SystemUtil.getOperatingSystem().open("https://shedaniel.me/MaterialisationData/");
            MinecraftClient.getInstance().openScreen(screen);
        }, "https://shedaniel.me/MaterialisationData/", true);
    }
}

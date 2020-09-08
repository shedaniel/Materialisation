package me.shedaniel.materialisation.modmenu;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.materialisation.ModReference;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class MaterialisationModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2"))
            return null;
        try {
            return (ConfigScreenFactory<?>) Class.forName("me.shedaniel.materialisation.modmenu.MaterialisationCloth").getDeclaredField("config").get(null);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}

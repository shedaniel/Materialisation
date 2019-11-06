package me.shedaniel.materialisation.compat.modmenu;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.materialisation.Materialisation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class MaterialisationModMenu implements ModMenuApi {
    @Override
    public String getModId() {
        return Materialisation.MOD_ID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2"))
            return null;
        try {
            return (Function<Screen, ? extends Screen>) Class.forName("me.shedaniel.materialisation.compat.modmenu.MaterialisationCloth").getDeclaredField("config").get(null);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package me.shedaniel.materialisation.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

import javax.annotation.Nullable;

public class MaterialisationModMenu implements ModMenuApi {
    @Override
    @Nullable
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

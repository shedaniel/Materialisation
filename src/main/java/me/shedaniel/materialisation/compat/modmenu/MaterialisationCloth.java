package me.shedaniel.materialisation.compat.modmenu;

import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class MaterialisationCloth {

    public static Function<Screen, Screen> config = screen -> {
        return new MaterialisationMaterialsScreen(screen);
    };

}

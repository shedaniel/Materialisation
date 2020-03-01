package me.shedaniel.materialisation.modmenu;

import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class MaterialisationCloth {
    
    @SuppressWarnings("Convert2MethodRef") public static Function<Screen, Screen> config = screen -> new MaterialisationMaterialsScreen(screen);
    
}

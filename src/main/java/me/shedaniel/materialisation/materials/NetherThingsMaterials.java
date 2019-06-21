package me.shedaniel.materialisation.materials;

import me.shedaniel.materialisation.api.DefaultMaterialSupplier;
import me.shedaniel.materialisation.api.PartMaterial;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Collections;
import java.util.List;

public class NetherThingsMaterials implements DefaultMaterialSupplier {
    
    @Override
    public List<PartMaterial> getMaterials() {
        if (!FabricLoader.getInstance().isModLoaded("netherthings"))
            return Collections.emptyList();
        try {
            return List.class.cast(Class.forName("me.shedaniel.materialisation.materials.NetherThingsMaterialsGetter").getDeclaredMethod("get").invoke(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
}

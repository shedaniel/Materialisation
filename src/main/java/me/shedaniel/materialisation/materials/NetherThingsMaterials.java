package me.shedaniel.materialisation.materials;

import me.shedaniel.materialisation.api.DefaultMaterialSupplier;
import me.shedaniel.materialisation.api.MaterialsPack;

import java.util.Collections;
import java.util.List;

public class NetherThingsMaterials implements DefaultMaterialSupplier {
    
    @Override
    public List<MaterialsPack> getMaterialPacks() {
        try {
            //noinspection unchecked,rawtypes
            return (List) Class.forName("me.shedaniel.materialisation.materials.NetherThingsMaterialsGetter").getDeclaredMethod("get").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
}

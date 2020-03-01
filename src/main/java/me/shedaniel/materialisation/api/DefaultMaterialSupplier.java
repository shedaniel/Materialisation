package me.shedaniel.materialisation.api;

import com.google.common.collect.Lists;

import java.util.List;

public interface DefaultMaterialSupplier {
    default List<PartMaterial> getMaterials() {
        return Lists.newArrayList();
    }
    
    default List<MaterialsPack> getMaterialPacks() {
        return Lists.newArrayList();
    }
}

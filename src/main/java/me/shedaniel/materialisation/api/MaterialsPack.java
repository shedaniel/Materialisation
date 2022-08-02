package me.shedaniel.materialisation.api;

import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.stream.Stream;

public interface MaterialsPack {
    
    ConfigPackInfo getConfigPackInfo();
    
    Identifier getIdentifier();

    @SuppressWarnings("unused")
    String getDisplayName();

    @SuppressWarnings("unused")
    PartMaterial getMaterial(String str);
    
    Stream<PartMaterial> getKnownMaterials();
    
    Map<String, PartMaterial> getKnownMaterialMap();
    
}

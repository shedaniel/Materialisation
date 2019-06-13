package me.shedaniel.materialisation.api;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.stream.Stream;

public class PartMaterials {
    
    private static Map<String, PartMaterial> materials = Maps.newLinkedHashMap();
    
    public static GeneratedMaterial getNewMaterial(String name) {
        return new GeneratedMaterial(name);
    }
    
    public static PartMaterial registerMaterial(PartMaterial material) {
        return materials.put(new Identifier(material.getName()).toString(), material);
    }
    
    public static Stream<PartMaterial> getKnownMaterials() {
        return materials.entrySet().stream().map(Map.Entry::getValue);
    }
    
    public static void clearMaterials() {
        materials.clear();
    }
    
}

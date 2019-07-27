package me.shedaniel.materialisation.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.materialisation.config.ConfigPack;
import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartMaterials {

    private static final Map<String, MaterialsPack> MATERIALS;

    static {
        MATERIALS = Maps.newLinkedHashMap();
        clearMaterials();
    }

    public static GeneratedMaterial getNewMaterial(String name) {
        return new GeneratedMaterial(new Identifier(name));
    }

    @Deprecated
    public static PartMaterial registerMaterial(PartMaterial material) {
        return getDefaultPack().getMaterialMap().put(material.getIdentifier().toString(), material);
    }

    public static ConfigPack getDefaultPack() {
        return (ConfigPack) MATERIALS.get("default:default");
    }

    public static MaterialsPack registerPack(MaterialsPack materialsPack) {
        return MATERIALS.put(materialsPack.getIdentifier().toString(), materialsPack);
    }

    public static Stream<PartMaterial> getKnownMaterials() {
        List<PartMaterial> list = Lists.newArrayList();
        MATERIALS.entrySet().stream().map(Map.Entry::getValue).map(MaterialsPack::getKnownMaterials).map(stream -> stream.collect(Collectors.toList())).forEach(list::addAll);
        return list.stream();
    }

    public static Stream<MaterialsPack> getMaterialPacks() {
        return MATERIALS.entrySet().stream().map(Map.Entry::getValue);
    }

    public static void clearMaterials() {
        MATERIALS.clear();
        ConfigPackInfo packInfo = new ConfigPackInfo("Default Pack", "default:default", Collections.emptyList(), Collections.emptyList(), "0.1.0");
        MATERIALS.put("default:default", new ConfigPack(packInfo.withDescription("The default material pack that materials with no origin goes to."), Maps.newLinkedHashMap()));
    }

}

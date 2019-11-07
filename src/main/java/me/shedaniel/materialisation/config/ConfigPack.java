package me.shedaniel.materialisation.config;

import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ConfigPack implements MaterialsPack {

    private ConfigPackInfo configPackInfo;
    private Map<String, PartMaterial> materialMap;
    private AtomicInteger overrides = new AtomicInteger(0);
    private AtomicInteger modifiers = new AtomicInteger(0);

    public ConfigPack(ConfigPackInfo configPackInfo, Map<String, PartMaterial> materialMap) {
        this.configPackInfo = configPackInfo;
        this.materialMap = materialMap;
    }

    @Override
    public ConfigPackInfo getConfigPackInfo() {
        return configPackInfo;
    }

    public AtomicInteger getOverrides() {
        return overrides;
    }

    public AtomicInteger getModifiers() {
        return modifiers;
    }

    @Override
    public Identifier getIdentifier() {
        return configPackInfo.getIdentifier();
    }

    @Override
    public String getDisplayName() {
        return configPackInfo.getDisplayName();
    }

    @Override
    public PartMaterial getMaterial(String str) {
        return materialMap.get(str);
    }

    @Override
    public Stream<PartMaterial> getKnownMaterials() {
        return materialMap.entrySet().stream().map(Map.Entry::getValue);
    }

    @Override
    public Map<String, PartMaterial> getKnownMaterialMap() {
        return materialMap;
    }

}

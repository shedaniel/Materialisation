package me.shedaniel.materialisation.config;

import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ConfigPack implements MaterialsPack {
    
    @SuppressWarnings("CanBeFinal")
    private ConfigPackInfo configPackInfo;
    @SuppressWarnings("CanBeFinal")
    private Map<String, PartMaterial> materialMap;
    @SuppressWarnings("CanBeFinal")
    private AtomicInteger overrides = new AtomicInteger(0);
    @SuppressWarnings("CanBeFinal")
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
        return materialMap.values().stream();
    }
    
    @Override
    public Map<String, PartMaterial> getKnownMaterialMap() {
        return materialMap;
    }
    
}

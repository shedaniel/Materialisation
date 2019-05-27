package me.shedaniel.materialisation.config;

import com.google.gson.JsonObject;

import java.util.List;

public class MaterialisationConfig {
    
    public List<ConfigMaterial> materials;
    
    public static class ConfigMaterial {
        public int toolHandleColor;
        public int pickaxeHeadColor;
        public int pickaxeHeadDurability;
        public JsonObject ingredient;
        public String name;
        public boolean bright;
        public float handleDurabilityMultiplier;
        public float handleBreakingSpeedMultiplier;
        public float pickaxeHeadSpeed;
        public float miningLevel;
    }
    
}

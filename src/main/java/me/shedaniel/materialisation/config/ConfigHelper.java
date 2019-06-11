package me.shedaniel.materialisation.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.KnownMaterials;
import me.shedaniel.materialisation.config.MaterialisationConfig.ConfigIngredients;
import me.shedaniel.materialisation.config.MaterialisationConfig.ConfigMaterial;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ConfigHelper {
    
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Materialisation"));
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIRECTORY = new File(FabricLoader.getInstance().getConfigDirectory(), "materialisation");
    private static final File MATERIALS_DIRECTORY = new File(CONFIG_DIRECTORY, "materials");
    
    public static void loadDefault() throws IOException {
        if (!CONFIG_DIRECTORY.exists() || !MATERIALS_DIRECTORY.exists())
            fillDefaultConfigs();
    }
    
    public static void loadConfig() {
        try {
            KnownMaterials.clearMaterials();
            for(File file : MATERIALS_DIRECTORY.listFiles())
                if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                    Materialisation.LOGGER.info("[Materialisation] Loading material file: " + file.getName());
                    try {
                        ConfigMaterial material = GSON.fromJson(new FileReader(file), ConfigMaterial.class);
                        KnownMaterials.registerMaterial(material);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void saveConfig(ConfigMaterial material) throws IOException {
        File file = new File(MATERIALS_DIRECTORY, material.getName().toLowerCase(Locale.ROOT) + ".json");
        FileWriter writer = new FileWriter(file, false);
        writer.write(GSON.toJson(material));
        writer.close();
    }
    
    private static void fillDefaultConfigs() throws IOException {
        MATERIALS_DIRECTORY.mkdirs();
        saveConfig(new ConfigMaterial(KnownMaterials.WOOD));
        saveConfig(new ConfigMaterial(KnownMaterials.STONE));
        saveConfig(new ConfigMaterial(KnownMaterials.IRON));
        saveConfig(new ConfigMaterial(KnownMaterials.GOLD));
    }
    
    public static List<ConfigIngredients> fromMap(Map<BetterIngredient, Float> map) {
        return map.entrySet().stream().map(entry -> new ConfigIngredients(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
    
    public static Map<BetterIngredient, Float> fromJson(List<ConfigIngredients> jsonObjects) {
        LinkedHashMap<BetterIngredient, Float> map = Maps.newLinkedHashMap();
        jsonObjects.forEach(configIngredients -> map.put(configIngredients.ingredient, configIngredients.multiplier));
        return map;
    }
    
}

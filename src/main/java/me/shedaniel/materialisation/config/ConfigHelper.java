package me.shedaniel.materialisation.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.DefaultMaterialSupplier;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.config.MaterialisationConfig.ConfigIngredients;
import me.shedaniel.materialisation.config.MaterialisationConfig.ConfigMaterial;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ConfigHelper {
    
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Materialisation"));
    public static final File CONFIG_DIRECTORY = new File(FabricLoader.getInstance().getConfigDirectory(), "materialisation");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File MATERIALS_DIRECTORY = new File(CONFIG_DIRECTORY, "material");
    private static final File OLD_MATERIALS_DIRECTORY = new File(CONFIG_DIRECTORY, "materials");
    
    public static void loadDefault() throws IOException {
        if (OLD_MATERIALS_DIRECTORY.exists())
            OLD_MATERIALS_DIRECTORY.renameTo(new File(CONFIG_DIRECTORY, "materials_old"));
        if (!CONFIG_DIRECTORY.exists() || !MATERIALS_DIRECTORY.exists())
            fillDefaultConfigs();
    }
    
    public static void loadConfig() {
        List<PartMaterial> defaultMaterials = Lists.newArrayList();
        List<ConfigMaterial> knownMaterials = Lists.newArrayList();
        List<JsonObject> overrides = Lists.newArrayList();
        try {
            FabricLoader.getInstance().getEntrypoints("materialisation_default", DefaultMaterialSupplier.class).stream().map(supplier -> {
                try {
                    return supplier.getMaterials();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return (List<PartMaterial>) Collections.EMPTY_LIST;
                }
            }).forEach(defaultMaterials::addAll);
            for(PartMaterial partMaterial : defaultMaterials) {
                ConfigMaterial material = new ConfigMaterial(partMaterial);
                knownMaterials.add(material);
                Materialisation.LOGGER.info("[Materialisation] Loading default material: " + material.getIdentifier().toString());
            }
            for(File file : MATERIALS_DIRECTORY.listFiles())
                if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".json")) {
                    try {
                        JsonObject object = GSON.fromJson(new FileReader(file), JsonObject.class);
                        if (!object.has("type") || object.get("type").getAsString().equalsIgnoreCase("material")) {
                            Materialisation.LOGGER.info("[Materialisation] Loading material file: " + file.getName());
                            knownMaterials.add(GSON.fromJson(object, ConfigMaterial.class));
                        } else {
                            String type = object.get("type").getAsString();
                            if (type.equalsIgnoreCase("override")) {
                                Materialisation.LOGGER.info("[Materialisation] Loading override file: " + file.getName());
                                overrides.add(object);
                            } else {
                                Materialisation.LOGGER.warn("[Materialisation] Cancelled loading unknown file: " + file.getName());
                            }
                        }
                    } catch (Exception e) {
                        Materialisation.LOGGER.error("[Materialisation] Failed to load material.", e);
                    }
                }
            overrides.sort(Comparator.comparingDouble(value -> value.has("priority") ? value.get("priority").getAsDouble() : 0d));
            for(JsonObject override : overrides)
                try {
                    Identifier identifier = new Identifier(override.get("name").getAsString());
                    for(Map.Entry<String, JsonElement> entry : override.entrySet())
                        if (!entry.getKey().equalsIgnoreCase("type") && !entry.getKey().equalsIgnoreCase("name") && !entry.getKey().equalsIgnoreCase("priority")) {
                            Optional<ConfigMaterial> any = knownMaterials.stream().filter(material -> new Identifier(material.name).equals(identifier)).findAny();
                            if (!any.isPresent())
                                throw new NullPointerException("Material " + identifier.toString() + " not found!");
                            ConfigMaterial material = any.get();
                            String key = entry.getKey();
                            boolean replaced = false;
                            for(Field declaredField : ConfigMaterial.class.getDeclaredFields())
                                if (Modifier.isPublic(declaredField.getModifiers()) && !Modifier.isTransient(declaredField.getModifiers()))
                                    if (declaredField.getName().equalsIgnoreCase(key)) {
                                        declaredField.setAccessible(true);
                                        declaredField.set(material, GSON.fromJson(entry.getValue(), Object.class));
                                        replaced = true;
                                        break;
                                    }
                            if (!replaced)
                                throw new NullPointerException("Failed to place field '" + key + "' of material " + material.getIdentifier().toString() + "!");
                        }
                } catch (Exception e) {
                    Materialisation.LOGGER.error("[Materialisation] Failed to load override.", e);
                }
        } catch (Exception e) {
            Materialisation.LOGGER.error("[Materialisation] Failed to load config.", e);
        }
        PartMaterials.clearMaterials();
        for(ConfigMaterial knownMaterial : knownMaterials)
            if (knownMaterial.enabled) {
                PartMaterials.registerMaterial(knownMaterial);
                Materialisation.LOGGER.info("[Materialisation] Finished loading material: " + knownMaterial.getIdentifier().toString());
            }
        Materialisation.LOGGER.info("[Materialisation] Finished loading materials: " + PartMaterials.getKnownMaterials().map(PartMaterial::getIdentifier).map(Identifier::toString).collect(Collectors.joining(", ")));
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            File autoGen = new File(CONFIG_DIRECTORY, "materialisation-dev-autogen");
            autoGen.mkdirs();
            for(File file : CONFIG_DIRECTORY.listFiles())
                if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".json"))
                    file.delete();
            for(PartMaterial defaultMaterial : defaultMaterials)
                try {
                    String s = GSON.toJson(new ConfigMaterial(defaultMaterial));
                    FileWriter writer = new FileWriter(new File(autoGen, defaultMaterial.getIdentifier().toString() + ".json"), false);
                    writer.write(s);
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
    
    private static void fillDefaultConfigs() throws IOException {
        MATERIALS_DIRECTORY.mkdirs();
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

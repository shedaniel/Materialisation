package me.shedaniel.materialisation.config;

import java.util.List;

@SuppressWarnings("unused")
public class ConfigPackInfoJson {
    private String displayName;
    private String identifier;
    private List<String> requiredMods;
    private List<String> authors;
    private String version;
    private String description;
    private transient ConfigPackInfo info;
    
    public ConfigPackInfo toInfo() {
        if (info == null) {
            info = new ConfigPackInfo(displayName, identifier, requiredMods, authors, version).withDescription(description);
        }
        return info;
    }
}

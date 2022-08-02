package me.shedaniel.materialisation.config;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigPackInfo {
    
    @SuppressWarnings("CanBeFinal")
    private String displayName;
    @SuppressWarnings("CanBeFinal")
    private String identifier;
    @SuppressWarnings("CanBeFinal")
    private Consumer<ConfigPackInfo> predicate;
    @SuppressWarnings("CanBeFinal")
    private List<String> authors;
    @SuppressWarnings("CanBeFinal")
    private String version;
    private String description;
    private transient Identifier identifierObject;
    private transient Version versionObject;
    
    public ConfigPackInfo(String displayName, String identifier, List<String> requiredMods, List<String> authors, String version) {
        this(displayName, identifier, (info) -> {
            List<String> modsNotLoaded = new ArrayList<>();
            for (String requiredMod : requiredMods) {
                if (!FabricLoader.getInstance().isModLoaded(requiredMod)) {
                    modsNotLoaded.add(requiredMod);
                }
            }
            if (!modsNotLoaded.isEmpty())
                throw new IllegalStateException("Config Pack " + info.getIdentifier().toString() + " is not loaded because " + modsNotLoaded.size() + " mods are not present: " + String.join(", ", modsNotLoaded.toArray(new String[0])));
        }, authors, version);
    }
    
    public ConfigPackInfo(String displayName, String identifier, Consumer<ConfigPackInfo> predicate, List<String> authors, String version) {
        this.displayName = displayName;
        this.identifier = identifier;
        this.predicate = predicate;
        this.authors = authors;
        this.version = version;
    }
    
    public ConfigPackInfo withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public String getDescription() {
        return description == null ? "" : description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Identifier getIdentifier() {
        if (identifierObject == null) {
            try {
                identifierObject = Identifier.tryParse(identifier);
                if (identifierObject == null)
                    throw new InvalidIdentifierException("");
                return identifierObject;
            } catch (InvalidIdentifierException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return identifierObject;
    }
    
    public Consumer<ConfigPackInfo> getPredicate() {
        return predicate;
    }
    
    public List<String> getAuthors() {
        return authors;
    }
    
    public Version getVersion() {
        if (versionObject == null) {
            try {
                return versionObject = Version.parse(version);
            } catch (VersionParsingException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return versionObject;
    }
    
}

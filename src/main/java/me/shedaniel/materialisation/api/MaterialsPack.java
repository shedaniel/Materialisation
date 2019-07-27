package me.shedaniel.materialisation.api;

import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.util.Identifier;

import java.util.stream.Stream;

public interface MaterialsPack {

    ConfigPackInfo getConfigPackInfo();

    Identifier getIdentifier();

    String getDisplayName();

    PartMaterial getMaterial(String str);

    Stream<PartMaterial> getKnownMaterials();

}

package me.shedaniel.materialisation.materials;

import com.google.common.collect.Maps;
import me.shedaniel.materialisation.api.GeneratedMaterial;
import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigPack;
import me.shedaniel.materialisation.config.ConfigPackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.shedaniel.materialisation.api.BetterIngredient.fromItem;
import static me.shedaniel.materialisation.api.PartMaterials.getNewMaterial;
import static net.minecraft.util.Identifier.tryParse;

public class NetherThingsMaterialsGetter {
    
    public static List<MaterialsPack> get() {
        Map<String, PartMaterial> materials = Maps.newLinkedHashMap();
        put(materials, newMat("nether").wEnch(77).aIngr(fromItem(Objects.requireNonNull(tryParse("minecraft:nether_brick"))), 1).aIngr(fromItem(Objects.requireNonNull(tryParse("minecraft:nether_bricks"))), 4).wAtta(.5f).wFull(100).wSpeed(5f).wDuraMulti(.8f).wSpeedMulti(.7f).setBright(false).wColor(0xff824741).setToolDurability(280).setMiningLevel(1));
        put(materials, newMat("glowstone").wEnch(12).aIngr(fromItem(Objects.requireNonNull(tryParse("minecraft:glowstone_dust"))), .5f).aIngr(fromItem(Objects.requireNonNull(tryParse("minecraft:glowstone"))), 2).wAtta(1.2f).wFull(120).wSpeed(5f).wDuraMulti(.9f).wSpeedMulti(.9f).setBright(true).wColor(0xfffcc367).setToolDurability(442).setMiningLevel(2));
        put(materials, newMat("vibranium").wEnch(7).aIngr(fromItem(Objects.requireNonNull(tryParse("netherthings:vibranium"))), 2).aIngr(fromItem(Objects.requireNonNull(tryParse("netherthings:vibranium_block"))), 18).wAtta(8).wFull(5463).wSpeed(22).wDuraMulti(1f).wSpeedMulti(1.2f).setBright(false).wColor(0xff8971c4).setToolDurability(21850).setMiningLevel(3));
        ConfigPackInfo packInfo = new ConfigPackInfo("Nether Things Materials", "netherthings:material", Collections.singletonList("netherthings"), Collections.singletonList("Danielshe"), "0.1.0");
        return Collections.singletonList(new ConfigPack(packInfo.withDescription("Adds the materials from Nether Things."), materials));
    }
    
    public static void put(Map<String, PartMaterial> map, PartMaterial material) {
        map.put(material.getIdentifier().toString(), material);
    }
    
    private static GeneratedMaterial newMat(String s) {
        return getNewMaterial("netherthings:" + s);
    }
    
}

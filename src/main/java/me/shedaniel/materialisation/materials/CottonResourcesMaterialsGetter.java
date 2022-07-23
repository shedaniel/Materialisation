package me.shedaniel.materialisation.materials;

import com.google.common.collect.Maps;
import me.shedaniel.materialisation.api.GeneratedMaterial;
import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigPack;
import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.shedaniel.materialisation.api.BetterIngredient.fromTag;
import static me.shedaniel.materialisation.api.PartMaterials.getNewMaterial;
import static net.minecraft.util.Identifier.tryParse;

public class CottonResourcesMaterialsGetter {
    
    public static List<MaterialsPack> get() {
        Map<String, PartMaterial> materials = Maps.newLinkedHashMap();
        //put(materials, newMat("copper").wEnch(12).aIngr(fromTag(Objects.requireNonNull(tryParse("c:copper_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:copper_blocks"))), 18).wAtta(1).wFull(100).wSpeed(5.3f).wDuraMulti(1.05f).wSpeedMulti(1f).setBright(true).wColor(0xffffa21f).setToolDurability(275).setMiningLevel(1));
        put(materials, newMat("silver").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:silver_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:silver_blocks"))), 18).wAtta(3).wFull(100).wSpeed(5f).wDuraMulti(.9f).wSpeedMulti(1f).setBright(true).wColor(0xff9387ff).setToolDurability(325).setMiningLevel(1));
        put(materials, newMat("lead").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:lead_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:lead_blocks"))), 18).wAtta(1.5f).wFull(100).wSpeed(5.25f).wDuraMulti(.7f).wSpeedMulti(.6f).setBright(false).wColor(0xffaf96cc).setToolDurability(375).setMiningLevel(1));
        put(materials, newMat("zinc").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:zinc_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:zinc_blocks"))), 18).wAtta(1).wFull(100).wSpeed(4.75f).wDuraMulti(.8f).wSpeedMulti(.5f).setBright(true).wColor(0xffd1fff7).setToolDurability(200).setMiningLevel(1));
        put(materials, newMat("aluminum").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:aluminum_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:aluminum_blocks"))), 18).wAtta(2).wFull(100).wSpeed(5f).wDuraMulti(.85f).wSpeedMulti(1.3f).setBright(true).wColor(0xfff7b2d6).setToolDurability(300).setMiningLevel(1));
        put(materials, newMat("cobalt").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:cobalt_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:cobalt_blocks"))), 18).wAtta(4).wFull(200).wSpeed(12).wDuraMulti(1.1f).wSpeedMulti(1f).setBright(false).wColor(0xff172fd1).setToolDurability(800).setMiningLevel(3));
        put(materials, newMat("tin").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:tin_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:tin_blocks"))), 18).wAtta(2).wFull(100).wSpeed(5.1f).wDuraMulti(.9f).wSpeedMulti(1.1f).setBright(true).wColor(0xff63aeff).setToolDurability(290).setMiningLevel(1));
        put(materials, newMat("titanium").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:titanium_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:titanium_blocks"))), 18).wAtta(2.7f).wFull(150).wSpeed(7).wDuraMulti(.9f).wSpeedMulti(1).setBright(true).wColor(0xff777777).setToolDurability(600).setMiningLevel(2));
        put(materials, newMat("tungsten").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:tungsten_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:tungsten_blocks"))), 18).wAtta(3).wFull(175).wSpeed(8).wDuraMulti(1).wSpeedMulti(1).setBright(true).wColor(0xff2f2d44).setToolDurability(700).setMiningLevel(3));
        put(materials, newMat("platinum").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:platinum_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:platinum_blocks"))), 18).wAtta(2).wFull(50).wSpeed(10).wDuraMulti(.5f).wSpeedMulti(.7f).setBright(true).wColor(0xffffffff).setToolDurability(150).setMiningLevel(2));
        put(materials, newMat("palladium").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:palladium_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:palladium_blocks"))), 18).wAtta(3.5f).wFull(25).wSpeed(16).wDuraMulti(.4f).wSpeedMulti(.3f).setBright(true).wColor(0xffe1b4e8).setToolDurability(100).setMiningLevel(3));
        put(materials, newMat("osmium").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:osmium_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:osmium_blocks"))), 18).wAtta(2).wFull(125).wSpeed(4.25f).wDuraMulti(1.1f).wSpeedMulti(1).setBright(true).wColor(0xffc2dfed).setToolDurability(500).setMiningLevel(2));
        put(materials, newMat("iridium").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:iridium_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:iridium_blocks"))), 18).wAtta(5).wFull(150).wSpeed(6.5f).wDuraMulti(.9f).wSpeedMulti(1.2f).setBright(true).wColor(0xffb0f2b7).setToolDurability(600).setMiningLevel(3));
        put(materials, newMat("steel").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:steel_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:steel_blocks"))), 18).wAtta(3).wFull(150).wSpeed(8).wDuraMulti(1.1f).wSpeedMulti(.8f).setBright(false).wColor(0xff777777).setToolDurability(600).setMiningLevel(2));
        put(materials, newMat("brass").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:brass_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:brass_blocks"))), 18).wAtta(2.5f).wFull(100).wSpeed(5.5f).wDuraMulti(.9f).wSpeedMulti(1.1f).setBright(true).wColor(0xffffd000).setToolDurability(400).setMiningLevel(2));
        put(materials, newMat("electrum").wEnch(16).aIngr(fromTag(Objects.requireNonNull(tryParse("c:electrum_ingots"))), 2).aIngr(fromTag(Objects.requireNonNull(tryParse("c:electrum_blocks"))), 18).wAtta(3).wFull(110).wSpeed(6).wDuraMulti(.9f).wSpeedMulti(1f).setBright(true).wColor(0xffead470).setToolDurability(440).setMiningLevel(2));
        ConfigPackInfo packInfo = new ConfigPackInfo("Cotton Resources Materials", "cotton-resources:material", info -> {
            String[] supportedMods = new String[]{
                    "cotton-resources",
                    "techreborn",
                    "astromine"
            };
            for (String supportedMod : supportedMods) {
                if (FabricLoader.getInstance().isModLoaded(supportedMod))
                    return;
            }
            throw new IllegalStateException("Config Pack " + info.getIdentifier().toString() + " is not loaded because no supported mods are present: " + String.join(", ", supportedMods));
        }, Collections.singletonList("shedaniel"), "1.0.0");
        return Collections.singletonList(new ConfigPack(packInfo.withDescription("Adds the materials from Cotton Resources."), materials));
    }
    
    public static void put(Map<String, PartMaterial> map, PartMaterial material) {
        map.put(material.getIdentifier().toString(), material);
    }
    
    private static GeneratedMaterial newMat(String s) {
        return getNewMaterial("common:" + s);
    }
    
}

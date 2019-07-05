package me.shedaniel.materialisation.materials;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.api.GeneratedMaterial;
import me.shedaniel.materialisation.api.PartMaterial;

import java.util.List;

import static me.shedaniel.materialisation.api.BetterIngredient.fromItem;
import static me.shedaniel.materialisation.api.PartMaterials.getNewMaterial;
import static net.minecraft.util.Identifier.tryParse;

public class NetherThingsMaterialsGetter {
    
    public static List<PartMaterial> get() {
        List<PartMaterial> materials = Lists.newArrayList();
        materials.add(newMat("nether").wEnch(77).aIngr(fromItem(tryParse("minecraft:nether_brick")), 1).aIngr(fromItem(tryParse("minecraft:nether_bricks")), 4).wAtta(.5f).wFull(100).wSpeed(5f).wDuraMulti(.8f).wSpeedMulti(.7f).setBright(false).wColor(0xff824741).setToolDurability(280).setMiningLevel(1));
        materials.add(newMat("glowstone").wEnch(12).aIngr(fromItem(tryParse("minecraft:glowstone_dust")), .5f).aIngr(fromItem(tryParse("minecraft:glowstone")), 2).wAtta(1.2f).wFull(120).wSpeed(5f).wDuraMulti(.9f).wSpeedMulti(.9f).setBright(true).wColor(0xfffcc367).setToolDurability(442).setMiningLevel(2));
        materials.add(newMat("vibranium").wEnch(7).aIngr(fromItem(tryParse("netherthings:vibranium")), 2).aIngr(fromItem(tryParse("netherthings:vibranium_block")), 18).wAtta(8).wFull(5463).wSpeed(22).wDuraMulti(1f).wSpeedMulti(1.2f).setBright(false).wColor(0xff8971c4).setToolDurability(21850).setMiningLevel(3));
        return materials;
    }
    
    private static GeneratedMaterial newMat(String s) {
        return getNewMaterial("netherthings:" + s);
    }
    
}

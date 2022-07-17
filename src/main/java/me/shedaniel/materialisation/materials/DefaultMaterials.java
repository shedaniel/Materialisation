package me.shedaniel.materialisation.materials;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.materialisation.api.DefaultMaterialSupplier;
import me.shedaniel.materialisation.api.MaterialsPack;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.config.ConfigPack;
import me.shedaniel.materialisation.config.ConfigPackInfo;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static me.shedaniel.materialisation.api.BetterIngredient.fromItem;
import static me.shedaniel.materialisation.api.BetterIngredient.fromTag;
import static me.shedaniel.materialisation.api.PartMaterials.getNewMaterial;

public class DefaultMaterials implements DefaultMaterialSupplier {
    
    public static final PartMaterial WOOD;
    public static final PartMaterial STONE;
    public static final PartMaterial FLINT;
    public static final PartMaterial IRON;
    public static final PartMaterial COPPER;
    public static final PartMaterial GOLD;
    public static final PartMaterial DIAMOND;
    public static final PartMaterial NETHERITE;

    static {
        WOOD = getNewMaterial("wood")
                .wEnch(15)
                .aIngr(fromTag(ItemTags.PLANKS), 1)
                .aIngr(fromItem(Items.STICK), .5f)
                .aIngr(fromTag(ItemTags.LOGS), 4)
                .autoTex()
                .setFullAmount(100).setToolSpeed(2f).wDuraMulti(1.1f).wColor(0xffffa21f).setToolDurability(59);
        STONE = getNewMaterial("stone")
                .wEnch(5)
                .aIngr(fromItem(Items.COBBLESTONE), 1)
                .autoTex()
                .wAtta(1)
                .setFullAmount(100).setToolSpeed(4f).wDuraMulti(0.4f).wSpeedMulti(0.9f).wColor(-2960686).setToolDurability(131).setMiningLevel(1);
        FLINT = getNewMaterial("flint")
                .wEnch(10)
                .aIngr(fromItem(Items.FLINT), 1)
                .autoTex()
                .wAtta(1.5f)
                .setFullAmount(100).setToolSpeed(5f).wDuraMulti(0.4f).wSpeedMulti(0.9f).wColor(-11842744).setToolDurability(171).setMiningLevel(1);
        COPPER = getNewMaterial("copper")
                .wEnch(10)
                .aIngr(fromItem(Items.COPPER_INGOT), 2)
                .aIngr(fromItem(Items.COPPER_BLOCK), 18)
                .autoTex()
                .wAtta(1.5F).setFullAmount(100).setBright(true).setToolSpeed(6f).wDuraMulti(1f).wSpeedMulti(1.0f).wColor(0xFFD27416).setToolDurability(200).setMiningLevel(2);
        IRON = getNewMaterial("iron")
                .wEnch(14)
                .aIngr(fromItem(Items.IRON_INGOT), 2)
                .aIngr(fromItem(Items.IRON_BLOCK), 18)
                .autoTex()
                .wAtta(2).setFullAmount(100).setBright(true).setToolSpeed(6f).wDuraMulti(1f).wSpeedMulti(1.1f).wColor(0xFFFFFFFF).setToolDurability(250).setMiningLevel(2);
        GOLD = getNewMaterial("gold")
                .wEnch(22)
                .aIngr(fromItem(Items.GOLD_INGOT), 2)
                .aIngr(fromItem(Items.GOLD_BLOCK), 18)
                .autoTex()
                .setFullAmount(10).setBright(true).setToolSpeed(12f).wDuraMulti(0.2f).wSpeedMulti(0.4f).wColor(0xffffef3d).setToolDurability(32);
        DIAMOND = getNewMaterial("diamond")
                .wEnch(15)
                .aIngr(fromItem(Items.DIAMOND), 2)
                .aIngr(fromItem(Items.DIAMOND_BLOCK), 18)
                .autoTex()
                .wAtta(3).setFullAmount(250).setBright(true).setToolSpeed(9f).wDuraMulti(0.8f).wSpeedMulti(1.0f).wColor(0xff68e8d9).setToolDurability(2031).setMiningLevel(3);
        NETHERITE = getNewMaterial("netherite")
                .wEnch(20)
                .aIngr(fromItem(Items.NETHERITE_INGOT), 2)
                .aIngr(fromItem(Items.NETHERITE_BLOCK), 18)
                .autoTex()
                .wAtta(4.2f).setFullAmount(300).setBright(true).setToolSpeed(10f).wDuraMulti(0.8f).wSpeedMulti(1.0f).wColor(0xFF4F3C3E).setToolDurability(2501).setMiningLevel(4);
    }
    
    @Override
    public List<MaterialsPack> getMaterialPacks() {
        ConfigPackInfo packInfo = new ConfigPackInfo("Vanilla Materials", "minecraft:vanilla", Collections.emptyList(), Collections.singletonList("Danielshe"), "0.1.1");
        Map<String, PartMaterial> map = Maps.newLinkedHashMap();
        getOursMaterials().forEach(partMaterial -> map.put(partMaterial.getIdentifier().toString(), partMaterial));
        return Collections.singletonList(new ConfigPack(packInfo.withDescription("The four basic material for Minecraft vanilla."), map));
    }
    
    public List<PartMaterial> getOursMaterials() {
        return Lists.newArrayList(WOOD, STONE, FLINT, COPPER, IRON, GOLD, DIAMOND, NETHERITE);
    }
    
}

package me.shedaniel.materialisation;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.DefaultMaterialSupplier;
import me.shedaniel.materialisation.api.PartMaterial;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;

import java.util.List;

import static me.shedaniel.materialisation.api.PartMaterials.getNewMaterial;

public class MaterialisationDefaultMaterials implements DefaultMaterialSupplier {
    
    public static final PartMaterial WOOD;
    public static final PartMaterial STONE;
    public static final PartMaterial IRON;
    public static final PartMaterial GOLD;
    
    static {
        WOOD = getNewMaterial("wood").setEnchantability(15).addIngredient(BetterIngredient.fromTag(ItemTags.PLANKS), 1).addIngredient(BetterIngredient.fromItem(Items.STICK), .5f).addIngredient(BetterIngredient.fromTag(ItemTags.LOGS), 4).setFullAmount(100).setToolSpeed(2f).setDurabilityMultiplier(1.1f).setToolColor(33530399).setToolDurability(59);
        STONE = getNewMaterial("stone").setEnchantability(5).addIngredient(BetterIngredient.fromItem(Items.COBBLESTONE), 1).setAttackDamage(1).setFullAmount(100).setToolSpeed(4f).setDurabilityMultiplier(0.4f).setBreakingSpeedMultiplier(0.9f).setToolColor(-2960686).setToolDurability(131).setMiningLevel(1);
        IRON = getNewMaterial("iron").setEnchantability(14).addIngredient(BetterIngredient.fromItem(Items.IRON_INGOT), 2).setAttackDamage(2).addIngredient(BetterIngredient.fromItem(Items.IRON_BLOCK), 18).setFullAmount(100).setBright(true).setToolSpeed(6f).setDurabilityMultiplier(0.9f).setBreakingSpeedMultiplier(1f).setToolColor(0xFFFFFFFF).setToolDurability(250).setMiningLevel(2);
        GOLD = getNewMaterial("gold").setEnchantability(22).addIngredient(BetterIngredient.fromItem(Items.GOLD_INGOT), 2).addIngredient(BetterIngredient.fromItem(Items.GOLD_BLOCK), 18).setFullAmount(10).setBright(true).setToolSpeed(12f).setDurabilityMultiplier(0.2f).setBreakingSpeedMultiplier(0.4f).setToolColor(0xffffef3d).setToolDurability(32);
    }
    
    @Override
    public List<PartMaterial> getMaterials() {
        return Lists.newArrayList(WOOD, STONE, IRON, GOLD);
    }
    
}

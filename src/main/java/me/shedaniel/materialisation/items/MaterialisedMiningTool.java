package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.PartMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface MaterialisedMiningTool {
    String getInternalName();

    default List<PartMaterial> getPartMaterials(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        List<PartMaterial> materials = new ArrayList<>();
        if (!tag.containsKey("mt_" + getInternalName() + "_head_material") || !tag.containsKey("mt_handle_material")) {
            if (!tag.containsKey("mt_0_material") || !tag.containsKey("mt_1_material")) {
                Materialisation.LOGGER.error("A tool with no materials! " + stack);
                return Collections.emptyList();
            }
            materials.add(MaterialisationUtils.getMaterialFromString(tag.getString("mt_0_material")));
            materials.add(MaterialisationUtils.getMaterialFromString(tag.getString("mt_1_material")));
            return materials;
        }
        materials.add(MaterialisationUtils.getMaterialFromString(tag.getString("mt_handle_material")));
        materials.add(MaterialisationUtils.getMaterialFromString(tag.getString("mt_" + getInternalName() + "_head_material")));
        return materials;
    }

    default int getDefaultModifierSlotsCount(ItemStack stack) {
        return getPartMaterials(stack).stream().mapToInt(PartMaterial::getModifierSlotsCount).sum();
    }

    boolean canEffectivelyBreak(ItemStack itemStack, BlockState state);

    int getEnchantability(ItemStack stack);

    float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state);

    double getAttackSpeed();
}

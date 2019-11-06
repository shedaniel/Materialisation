package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class MaterialisedSwordItem extends SwordItem implements MaterialisedMiningTool {

    public MaterialisedSwordItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -2.4F, settings.maxDamage(0));
        this.init();
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack stack, BlockState state) {
        Block block_1 = state.getBlock();
        if (block_1 == Blocks.COBWEB) {
            return 15.0F;
        } else {
            Material material_1 = state.getMaterial();
            return material_1 != Material.PLANT && material_1 != Material.REPLACEABLE_PLANT && material_1 != Material.UNUSED_PLANT && !state.matches(BlockTags.LEAVES) && material_1 != Material.PUMPKIN ? 1.0F : 1.5F;
        }
    }

    @Override
    public double getAttackSpeed() {
        return -2.4f;
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack itemStack, BlockState state) {
        return state.getBlock() == Blocks.COBWEB;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return MaterialisedMiningTool.super.postHit(stack, target, attacker);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return MaterialisedMiningTool.super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public int getEnchantability(ItemStack stack) {
        return MaterialisedMiningTool.super.getEnchantability(stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        MaterialisedMiningTool.super.appendTooltip(stack, world, tooltip, context);
    }
}

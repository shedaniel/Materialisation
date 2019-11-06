package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
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
import net.minecraft.item.PickaxeItem;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class MaterialisedPickaxeItem extends PickaxeItem implements MaterialisedMiningTool {

    public MaterialisedPickaxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -2.8F, settings.maxDamage(0));
        this.init();
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack stack, BlockState state) {
        Block block_1 = state.getBlock();
        int int_1 = MaterialisationUtils.getToolMiningLevel(stack);
        if (block_1 == Blocks.OBSIDIAN) {
            return int_1 >= 3;
        } else if (block_1 != Blocks.DIAMOND_BLOCK && block_1 != Blocks.DIAMOND_ORE && block_1 != Blocks.EMERALD_ORE && block_1 != Blocks.EMERALD_BLOCK && block_1 != Blocks.GOLD_BLOCK && block_1 != Blocks.GOLD_ORE && block_1 != Blocks.REDSTONE_ORE) {
            if (block_1 != Blocks.IRON_BLOCK && block_1 != Blocks.IRON_ORE && block_1 != Blocks.LAPIS_BLOCK && block_1 != Blocks.LAPIS_ORE) {
                Material material_1 = state.getMaterial();
                return material_1 == Material.STONE || material_1 == Material.METAL || material_1 == Material.ANVIL;
            } else
                return int_1 >= 1;
        } else {
            return int_1 >= 2;
        }
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        return material != Material.METAL && material != Material.ANVIL && material != Material.STONE ? (((MiningToolItemAccessor) stack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisationUtils.getToolBreakingSpeed(stack) : 1.0F) : MaterialisationUtils.getToolBreakingSpeed(stack);
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
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

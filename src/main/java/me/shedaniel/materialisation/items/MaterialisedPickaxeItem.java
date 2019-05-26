package me.shedaniel.materialisation.items;

import com.google.common.collect.ImmutableSet;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public class MaterialisedPickaxeItem extends PickaxeItem {
    
    public MaterialisedPickaxeItem(Settings settings) {
        super(MaterialisedToolUtils.DUMMY_MATERIAL, 0, -2.8F, settings.durability(0));
        addProperty(new Identifier(ModReference.MOD_ID, "handle_isbright"), (itemStack, world, livingEntity) -> {
            return itemStack.getOrCreateTag().containsKey("mt_handle_bright") ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "handle_isnotbright"), (itemStack, world, livingEntity) -> {
            return !itemStack.getOrCreateTag().containsKey("mt_handle_bright") ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "pickaxe_head_isbright"), (itemStack, world, livingEntity) -> {
            return itemStack.getOrCreateTag().containsKey("mt_pickaxe_head_bright") ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "pickaxe_head_isnotbright"), (itemStack, world, livingEntity) -> {
            return !itemStack.getOrCreateTag().containsKey("mt_pickaxe_head_bright") ? 1f : 0f;
        });
    }
    
    public static boolean canEffectivelyBreak(ItemStack stack, BlockState state) {
        Block block_1 = state.getBlock();
        int int_1 = MaterialisedToolUtils.getToolMiningLevel(stack);
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
    
    public static float getToolBlockBreakingSpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        return material != Material.METAL && material != Material.ANVIL && material != Material.STONE ? (((MiningToolItemAccessor) stack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisedToolUtils.getToolBreakingSpeed(stack) : 1.0F) : MaterialisedToolUtils.getToolBreakingSpeed(stack);
    }
    
    @Override
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }
    
    @Override
    public boolean onEntityDamaged(ItemStack stack, LivingEntity livingEntity_1, LivingEntity livingEntity_2) {
        if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
            if (MaterialisedToolUtils.getToolDurability(stack) > 0)
                if (MaterialisedToolUtils.applyDamage(stack, 2, livingEntity_1.getRand())) {
                    livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    Item item_1 = stack.getItem();
                    stack.subtractAmount(1);
                    if (livingEntity_1 instanceof PlayerEntity) {
                        ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                    }
                    MaterialisedToolUtils.setToolDurability(stack, 0);
                }
        return true;
    }
    
    @Override
    public boolean onBlockBroken(ItemStack stack, World world_1, BlockState blockState_1, BlockPos blockPos_1, LivingEntity livingEntity_1) {
        if (!world_1.isClient && blockState_1.getHardness(world_1, blockPos_1) != 0.0F)
            if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
                if (MaterialisedToolUtils.getToolDurability(stack) > 0)
                    if (MaterialisedToolUtils.applyDamage(stack, 1, livingEntity_1.getRand())) {
                        livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        Item item_1 = stack.getItem();
                        stack.subtractAmount(1);
                        if (livingEntity_1 instanceof PlayerEntity) {
                            ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                        }
                        MaterialisedToolUtils.setToolDurability(stack, 0);
                    }
        return true;
    }
    
    @Override
    public void buildTooltip(ItemStack stack, World world_1, List<Component> list_1, TooltipContext tooltipContext_1) {
        int toolDurability = MaterialisedToolUtils.getToolDurability(stack);
        int maxDurability = MaterialisedToolUtils.getToolMaxDurability(stack);
        list_1.add(new TranslatableComponent("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0)
            list_1.add(new TranslatableComponent("text.materialisation.durability", toolDurability, MaterialisedToolUtils.TWO_DECIMAL_FORMATTER.format(toolDurability / (float) maxDurability * 100)));
        else
            list_1.add(new TranslatableComponent("text.materialisation.broken"));
        list_1.add(new TranslatableComponent("text.materialisation.breaking_speed", MaterialisedToolUtils.TWO_DECIMAL_FORMATTER.format(MaterialisedToolUtils.getToolBreakingSpeed(stack))));
        list_1.add(new TranslatableComponent("text.materialisation.mining_level", MaterialisedToolUtils.getToolMiningLevel(stack)));
    }
    
}

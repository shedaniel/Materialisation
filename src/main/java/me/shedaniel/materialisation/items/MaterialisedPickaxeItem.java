package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.ToolType;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;
import static me.shedaniel.materialisation.MaterialisationUtils.isHeadBright;

public class MaterialisedPickaxeItem extends PickaxeItem implements MaterialisedMiningTool {

    public MaterialisedPickaxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -2.8F, settings.maxDamage(0));
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "handle_isbright"), (itemStack, world, livingEntity) -> {
            return isHandleBright(itemStack) ? 1f : 0f;
        });
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "pickaxe_head_isbright"), (itemStack, world, livingEntity) -> {
            return isHeadBright(itemStack) ? 1f : 0f;
        });
    }

    @Override
    public int getEnchantability(ItemStack stack) {
        if (!stack.getOrCreateTag().containsKey("mt_pickaxe_head_material") || !stack.getOrCreateTag().containsKey("mt_handle_material")) {
            if (!stack.getOrCreateTag().containsKey("mt_0_material") || !stack.getOrCreateTag().containsKey("mt_1_material"))
                return 0;
            PartMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_0_material"));
            PartMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_1_material"));
            return (handle.getEnchantability() + head.getEnchantability()) / 2;
        }
        PartMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_handle_material"));
        PartMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_pickaxe_head_material"));
        return (handle.getEnchantability() + head.getEnchantability()) / 2;
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
    public ToolType getToolType() {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity livingEntity_1, LivingEntity livingEntity_2) {
        if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
            if (MaterialisationUtils.getToolDurability(stack) > 0)
                if (MaterialisationUtils.applyDamage(stack, 2, livingEntity_1.getRand())) {
                    livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    Item item_1 = stack.getItem();
                    stack.decrement(1);
                    if (livingEntity_1 instanceof PlayerEntity) {
                        ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                    }
                    MaterialisationUtils.setToolDurability(stack, 0);
                }
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world_1, BlockState blockState_1, BlockPos blockPos_1, LivingEntity livingEntity_1) {
        if (!world_1.isClient && blockState_1.getHardness(world_1, blockPos_1) != 0.0F)
            if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    if (MaterialisationUtils.applyDamage(stack, 1, livingEntity_1.getRand())) {
                        livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        Item item_1 = stack.getItem();
                        stack.decrement(1);
                        if (livingEntity_1 instanceof PlayerEntity) {
                            ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                        }
                        MaterialisationUtils.setToolDurability(stack, 0);
                    }
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        MaterialisationUtils.appendToolTooltip(stack, this, world_1, list_1, tooltipContext_1);
    }

}

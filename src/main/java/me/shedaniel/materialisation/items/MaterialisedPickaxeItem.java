package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.ToolType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class MaterialisedPickaxeItem extends PickaxeItem implements MaterialisedMiningTool {
    public MaterialisedPickaxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -2.8F, settings.maxDamage(0));
    }
    
    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return MaterialisationUtils.getToolDurability(stack) <= 0 ? -1 : super.getMiningSpeedMultiplier(stack, state);
    }
    
    @Nonnull
    @Override
    public ToolType getToolType() {
        return ToolType.PICKAXE;
    }
    
    @Override
    public boolean canRepair(ItemStack itemStack, ItemStack ingredient) {
        return false;
    }
    
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.world.isClient && (!(target instanceof PlayerEntity) || !((PlayerEntity) target).abilities.creativeMode)) {
            if (MaterialisationUtils.getToolDurability(stack) > 0) {
                if (MaterialisationUtils.applyDamage(stack, 2, target.getRandom())) {
                    target.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    Item item_1 = stack.getItem();
                    stack.decrement(1);
                    if (target instanceof PlayerEntity) {
                        ((PlayerEntity) target).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                    }
                    MaterialisationUtils.setToolDurability(stack, 0);
                }
            }
        }
        Materialisation.LOGGER.log(Level.INFO, Integer.toString(MaterialisationUtils.getToolDurability(stack)));
        return true;
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity miner) {
        if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F) {
            if (!miner.world.isClient && (!(miner instanceof PlayerEntity) || !((PlayerEntity) miner).abilities.creativeMode)) {
                if (MaterialisationUtils.getToolDurability(stack) > 0) {
                    if (MaterialisationUtils.applyDamage(stack, 1, miner.getRandom())) {
                        miner.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        Item item_1 = stack.getItem();
                        stack.decrement(1);
                        if (miner instanceof PlayerEntity) {
                            ((PlayerEntity) miner).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                        }
                        MaterialisationUtils.setToolDurability(stack, 0);
                    }
                }
            }
        }
        Materialisation.LOGGER.log(Level.INFO, Integer.toString(MaterialisationUtils.getToolDurability(stack)));
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        MaterialisationUtils.appendToolTooltip(stack, this, world, tooltip, tooltipContext);
    }
}

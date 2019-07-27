package me.shedaniel.materialisation.items;

import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;

public class MaterialisedShovelItem extends ShovelItem implements MaterialisedMiningTool {

    public MaterialisedShovelItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.0F, settings.maxDamage(0));
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "handle_isbright"), (itemStack, world, livingEntity) -> {
            return isHandleBright(itemStack) ? 1f : 0f;
        });
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "handle_isnotbright"), (itemStack, world, livingEntity) -> {
            return !isHandleBright(itemStack) ? 1f : 0f;
        });
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "shovel_head_isbright"), (itemStack, world, livingEntity) -> {
            return isHeadBright(itemStack) ? 1f : 0f;
        });
        addPropertyGetter(new Identifier(ModReference.MOD_ID, "shovel_head_isnotbright"), (itemStack, world, livingEntity) -> {
            return !isHeadBright(itemStack) ? 1f : 0f;
        });
    }

    public boolean isHeadBright(ItemStack itemStack) {
        if (itemStack.getOrCreateTag().containsKey("mt_shovel_head_bright"))
            return true;
        if (itemStack.getOrCreateTag().containsKey("mt_1_material"))
            return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::isBright).orElse(false);
        return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_shovel_head_material")).map(PartMaterial::isBright).orElse(false);
    }

    @Override
    public int getEnchantability(ItemStack stack) {
        if (!stack.getOrCreateTag().containsKey("mt_shovel_head_material") || !stack.getOrCreateTag().containsKey("mt_handle_material")) {
            if (!stack.getOrCreateTag().containsKey("mt_0_material") || !stack.getOrCreateTag().containsKey("mt_1_material"))
                return 0;
            PartMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_0_material"));
            PartMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_1_material"));
            return (handle.getEnchantability() + head.getEnchantability()) / 2;
        }
        PartMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_handle_material"));
        PartMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_shovel_head_material"));
        return (handle.getEnchantability() + head.getEnchantability()) / 2;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack itemStack = context.getStack();
        if (MaterialisationUtils.getToolDurability(itemStack) > 0)
            if (context.getSide() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
                BlockState blockState = (BlockState) PATH_BLOCKSTATES.get(world.getBlockState(blockPos).getBlock());
                if (blockState != null) {
                    PlayerEntity playerEntity_1 = context.getPlayer();
                    world.playSound(playerEntity_1, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    if (!world.isClient) {
                        world.setBlockState(blockPos, blockState, 11);
                        if (playerEntity_1 != null)
                            if (!playerEntity_1.world.isClient && (!(playerEntity_1 instanceof PlayerEntity) || !(playerEntity_1.abilities.creativeMode)))
                                if (MaterialisationUtils.applyDamage(itemStack, 1, playerEntity_1.getRand())) {
                                    playerEntity_1.sendToolBreakStatus(context.getHand());
                                    Item item_1 = itemStack.getItem();
                                    itemStack.decrement(1);
                                    playerEntity_1.incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                                    MaterialisationUtils.setToolDurability(itemStack, 0);
                                }
                    }
                    return ActionResult.SUCCESS;
                }
            }
        return ActionResult.PASS;
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack itemStack, BlockState state) {
        return ((MiningToolItemAccessor) itemStack.getItem()).getEffectiveBlocks().contains(state.getBlock()) || isEffectiveOn(state);
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack itemStack, BlockState state) {
        return ((MiningToolItemAccessor) itemStack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisationUtils.getToolBreakingSpeed(itemStack) : 1f;
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
        int toolDurability = MaterialisationUtils.getToolDurability(stack);
        int maxDurability = MaterialisationUtils.getToolMaxDurability(stack);
        list_1.add(new TranslatableText("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0) {
            float percentage = toolDurability / (float) maxDurability * 100;
            Formatting coloringPercentage = MaterialisationUtils.getColoringPercentage(percentage);
            list_1.add(new TranslatableText("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(percentage) + Formatting.WHITE.toString()));
        } else
            list_1.add(new TranslatableText("text.materialisation.broken"));
        list_1.add(new TranslatableText("text.materialisation.breaking_speed", MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(MaterialisationUtils.getToolBreakingSpeed(stack))));
        list_1.add(new TranslatableText("text.materialisation.mining_level", MaterialisationUtils.getToolMiningLevel(stack)));
    }

}

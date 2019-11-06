package me.shedaniel.materialisation.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.api.MaterialisedMiningTool;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;

public class MaterialisedMegaAxeItem extends AxeItem implements MaterialisedMiningTool {

    private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(
            Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS,
            Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS,
            Blocks.BOOKSHELF, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD,
            Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD,
            Blocks.DARK_OAK_WOOD, Blocks.OAK_LOG, Blocks.SPRUCE_LOG,
            Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG,
            Blocks.DARK_OAK_LOG, Blocks.CHEST, Blocks.PUMPKIN,
            Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON,
            Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON,
            Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON,
            Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.OAK_PRESSURE_PLATE,
            Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE,
            Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE);

    public MaterialisedMegaAxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.65F, settings.maxDamage(0));
        addPropertyGetter(new Identifier(Materialisation.MOD_ID, "handle_isbright"),
                (itemStack, world, livingEntity) -> isHandleBright(itemStack) ? 1f : 0f);
        addPropertyGetter(new Identifier(Materialisation.MOD_ID, "megaaxe_head_isbright"),
                (itemStack, world, livingEntity) -> isHeadBright(itemStack) ? 1f : 0f);
    }

    public boolean isHeadBright(ItemStack itemStack) {
        return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::isBright).orElse(false);
    }

    @Override
    public boolean canMine(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient)
            return true;
        ItemStack mainHandStack = player.getMainHandStack();
        if (player.isSneaking() || MaterialisationUtils.getToolDurability(mainHandStack) <= 0 || !isLogs(blockState))
            return true;
        Block log = blockState.getBlock();
        AtomicReference<Block> leaves = new AtomicReference<>(null);
        List<BlockPos> posList = Lists.newArrayList();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    BlockPos add = pos.add(x, y, z);
                    if (x != 0 || y != 0 || z != 0)
                        tryBreak(posList, world, add, player, mainHandStack, pos, log, leaves);
                }
        return true;
    }

    public void tryBreak(List<BlockPos> posList, World world, BlockPos blockPos, PlayerEntity player, ItemStack stack, BlockPos ogPos, Block log, AtomicReference<Block> leaves) {
        if (posList.contains(blockPos) || blockPos.getManhattanDistance(ogPos) > 14 || MaterialisationUtils.getToolDurability(stack) <= 0)
            return;
        posList.add(blockPos);
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();
        if (Objects.isNull(leaves.get()) && isLeaves(state))
            leaves.set(block);
        boolean equalsLog = block.equals(log);
        if (!equalsLog && !block.equals(leaves.get()))
            return;
        if (equalsLog && canBreak(stack, state)) {
            world.breakBlock(blockPos, !player.isCreative());
            takeDamage(world, state, blockPos, player, stack);
        }
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    BlockPos add = blockPos.add(x, y, z);
                    if (x != 0 || y != 0 || z != 0)
                        tryBreak(posList, world, add, player, stack, ogPos, log, leaves);
                }
    }

    private boolean canBreak(ItemStack stack, BlockState state) {
        TriState triState = MaterialisationUtils.mt_handleIsEffectiveOn(stack, state);
        if (triState != TriState.DEFAULT) {
            // If we are dealing with 3rd party blocks
            return triState.get();
        } else {
            // Lastly if we are not dealing with 3rd party blocks with durability left
            return ((MaterialisedMegaAxeItem) stack.getItem()).canEffectivelyBreak(stack, state);
        }
    }

    private void takeDamage(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity, ItemStack stack) {
        if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F)
            if (!playerEntity.world.isClient && (!(playerEntity instanceof PlayerEntity) || !playerEntity.abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    MaterialisationUtils.applyDamage(stack, 1, playerEntity.getRand());
    }

    private boolean isLogs(BlockState state) {
        return BlockTags.LOGS.contains(state.getBlock());
    }

    private boolean isLeaves(BlockState state) {
        return BlockTags.LEAVES.contains(state.getBlock());
    }

    @Override
    public int getEnchantability(ItemStack stack) {
        if (!stack.getOrCreateTag().containsKey("mt_0_material") || !stack.getOrCreateTag().containsKey("mt_1_material"))
            return 0;
        PartMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_0_material"));
        PartMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_1_material"));
        return (handle.getEnchantability() + head.getEnchantability()) / 2;
    }

    @Override
    public float getToolBlockBreakingSpeed(ItemStack stack, BlockState state) {
        Material material_1 = state.getMaterial();
        return material_1 != Material.WOOD && material_1 != Material.PLANT && material_1 != Material.REPLACEABLE_PLANT && material_1 != Material.BAMBOO ? (((MiningToolItemAccessor) stack.getItem()).getEffectiveBlocks().contains(state.getBlock()) ? MaterialisationUtils.getToolBreakingSpeed(stack) : 1.0F) : MaterialisationUtils.getToolBreakingSpeed(stack);
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public String getInternalName() {
        return "megaaxe";
    }

    @Override
    public boolean canEffectivelyBreak(ItemStack itemStack, BlockState state) {
        return EFFECTIVE_BLOCKS.contains(state.getBlock());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack itemStack = context.getStack();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = STRIPPED_BLOCKS.get(blockState.getBlock());
        if (MaterialisationUtils.getToolDurability(itemStack) > 0 && block != null) {
            PlayerEntity playerEntity_1 = context.getPlayer();
            world.playSound(playerEntity_1, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient) {
                world.setBlockState(blockPos, block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
                if (playerEntity_1 != null) {
                    if (!playerEntity_1.world.isClient && (!(playerEntity_1 instanceof PlayerEntity) || !(playerEntity_1.abilities.creativeMode)))
                        if (MaterialisationUtils.applyDamage(itemStack, 1, playerEntity_1.getRand())) {
                            playerEntity_1.sendToolBreakStatus(context.getHand());
                            Item item_1 = itemStack.getItem();
                            itemStack.decrement(1);
                            playerEntity_1.incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                            MaterialisationUtils.setToolDurability(itemStack, 0);
                        }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
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
    public void appendTooltip(ItemStack stack, World world_1, List<Text> tooltip, TooltipContext tooltipContext_1) {
        int toolDurability = MaterialisationUtils.getToolDurability(stack);
        int maxDurability = MaterialisationUtils.getToolMaxDurability(stack);
        tooltip.add(new TranslatableText("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0) {
            float percentage = toolDurability / (float) maxDurability * 100;
            Formatting coloringPercentage = MaterialisationUtils.getColoringPercentage(percentage);
            tooltip.add(new TranslatableText("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(percentage) + Formatting.WHITE.toString()));
        } else
            tooltip.add(new TranslatableText("text.materialisation.broken"));
        tooltip.add(new TranslatableText("text.materialisation.breaking_speed", MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(MaterialisationUtils.getToolBreakingSpeed(stack))));
        tooltip.add(new TranslatableText("text.materialisation.mining_level", MaterialisationUtils.getToolMiningLevel(stack)));
        tooltip.add(new TranslatableText("text.materialisation.modifier_slots_count", MaterialisationUtils.getModifierSlotsCount(stack)));
    }

}

package me.shedaniel.materialisation.items;

import com.google.common.collect.Sets;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.mixin.MiningToolItemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

import static me.shedaniel.materialisation.MaterialisationUtils.isHandleBright;

public class MaterialisedAxeItem extends AxeItem implements MaterialisedMiningTool {
    
    private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet(new Block[]{Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BOOKSHELF, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.CHEST, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.OAK_PRESSURE_PLATE, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.BIRCH_PRESSURE_PLATE, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.ACACIA_PRESSURE_PLATE});
    
    public MaterialisedAxeItem(Settings settings) {
        super(MaterialisationUtils.DUMMY_MATERIAL, 0, -3.1F, settings.durability(0));
        addProperty(new Identifier(ModReference.MOD_ID, "handle_isbright"), (itemStack, world, livingEntity) -> {
            return isHandleBright(itemStack) ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "handle_isnotbright"), (itemStack, world, livingEntity) -> {
            return !isHandleBright(itemStack) ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "axe_head_isbright"), (itemStack, world, livingEntity) -> {
            return isHeadBright(itemStack) ? 1f : 0f;
        });
        addProperty(new Identifier(ModReference.MOD_ID, "axe_head_isnotbright"), (itemStack, world, livingEntity) -> {
            return !isHeadBright(itemStack) ? 1f : 0f;
        });
    }
    
    public boolean isHeadBright(ItemStack itemStack) {
        if (itemStack.getOrCreateTag().containsKey("mt_axe_head_bright"))
            return true;
        if (itemStack.getOrCreateTag().containsKey("mt_1_material"))
            return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_1_material")).map(PartMaterial::isBright).orElse(false);
        return MaterialisationUtils.getMatFromString(itemStack.getOrCreateTag().getString("mt_axe_head_material")).map(PartMaterial::isBright).orElse(false);
    }
    
    @Override
    public int getEnchantability(ItemStack stack) {
        if (!stack.getOrCreateTag().containsKey("mt_axe_head_material") || !stack.getOrCreateTag().containsKey("mt_handle_material"))
            return 0;
        PartMaterial handle = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_handle_material"));
        PartMaterial head = MaterialisationUtils.getMaterialFromString(stack.getOrCreateTag().getString("mt_axe_head_material"));
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
    public boolean canEffectivelyBreak(ItemStack itemStack, BlockState state) {
        return EFFECTIVE_BLOCKS.contains(state.getBlock());
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack itemStack = context.getItemStack();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = (Block) BLOCK_TRANSFORMATIONS_MAP.get(blockState.getBlock());
        if (MaterialisationUtils.getToolDurability(itemStack) > 0 && block != null) {
            PlayerEntity playerEntity_1 = context.getPlayer();
            world.playSound(playerEntity_1, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClient) {
                world.setBlockState(blockPos, (BlockState) block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
                if (playerEntity_1 != null) {
                    if (!playerEntity_1.world.isClient && (!(playerEntity_1 instanceof PlayerEntity) || !(playerEntity_1.abilities.creativeMode)))
                        if (MaterialisationUtils.applyDamage(itemStack, 1, playerEntity_1.getRand())) {
                            playerEntity_1.sendToolBreakStatus(context.getHand());
                            Item item_1 = itemStack.getItem();
                            itemStack.subtractAmount(1);
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
    public boolean onEntityDamaged(ItemStack stack, LivingEntity livingEntity_1, LivingEntity livingEntity_2) {
        if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
            if (MaterialisationUtils.getToolDurability(stack) > 0)
                if (MaterialisationUtils.applyDamage(stack, 2, livingEntity_1.getRand())) {
                    livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                    Item item_1 = stack.getItem();
                    stack.subtractAmount(1);
                    if (livingEntity_1 instanceof PlayerEntity) {
                        ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                    }
                    MaterialisationUtils.setToolDurability(stack, 0);
                }
        return true;
    }
    
    @Override
    public boolean onBlockBroken(ItemStack stack, World world_1, BlockState blockState_1, BlockPos blockPos_1, LivingEntity livingEntity_1) {
        if (!world_1.isClient && blockState_1.getHardness(world_1, blockPos_1) != 0.0F)
            if (!livingEntity_1.world.isClient && (!(livingEntity_1 instanceof PlayerEntity) || !((PlayerEntity) livingEntity_1).abilities.creativeMode))
                if (MaterialisationUtils.getToolDurability(stack) > 0)
                    if (MaterialisationUtils.applyDamage(stack, 1, livingEntity_1.getRand())) {
                        livingEntity_1.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        Item item_1 = stack.getItem();
                        stack.subtractAmount(1);
                        if (livingEntity_1 instanceof PlayerEntity) {
                            ((PlayerEntity) livingEntity_1).incrementStat(Stats.BROKEN.getOrCreateStat(item_1));
                        }
                        MaterialisationUtils.setToolDurability(stack, 0);
                    }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(ItemStack stack, World world_1, List<Component> list_1, TooltipContext tooltipContext_1) {
        int toolDurability = MaterialisationUtils.getToolDurability(stack);
        int maxDurability = MaterialisationUtils.getToolMaxDurability(stack);
        list_1.add(new TranslatableComponent("text.materialisation.max_durability", maxDurability));
        if (toolDurability > 0) {
            float percentage = toolDurability / (float) maxDurability * 100;
            ChatFormat coloringPercentage = MaterialisationUtils.getColoringPercentage(percentage);
            list_1.add(new TranslatableComponent("text.materialisation.durability", coloringPercentage.toString() + toolDurability, coloringPercentage.toString() + MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(percentage) + ChatFormat.WHITE.toString()));
        } else
            list_1.add(new TranslatableComponent("text.materialisation.broken"));
        list_1.add(new TranslatableComponent("text.materialisation.breaking_speed", MaterialisationUtils.TWO_DECIMAL_FORMATTER.format(MaterialisationUtils.getToolBreakingSpeed(stack))));
        list_1.add(new TranslatableComponent("text.materialisation.mining_level", MaterialisationUtils.getToolMiningLevel(stack)));
    }
    
}

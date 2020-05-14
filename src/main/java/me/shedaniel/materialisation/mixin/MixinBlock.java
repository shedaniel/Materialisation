package me.shedaniel.materialisation.mixin;

import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.materialisation.modifiers.DefaultModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mixin(Block.class)
public abstract class MixinBlock {
    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"), cancellable = true)
    private static void getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            int autoSmeltLevel = ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, DefaultModifiers.AUTO_SMELT);
            if (autoSmeltLevel > 0) {
                Collection<Recipe<Inventory>> recipes = world.getRecipeManager().getAllOfType(RecipeType.SMELTING).values();
                List<ItemStack> outputStacks = new ArrayList<>(cir.getReturnValue());
                List<ItemStack> stacks = cir.getReturnValue();
                for (int i = 0; i < stacks.size(); i++) {
                    ItemStack itemStack = stacks.get(i);
                    Optional<Recipe<Inventory>> first = recipes.stream().filter(
                            recipe -> recipe.getPreviewInputs().get(0).test(itemStack)
                    ).findFirst();
                    int finalI = i;
                    first.ifPresent(recipe -> {
                        outputStacks.set(finalI, recipe.getOutput().copy());
                    });
                }
                cir.setReturnValue(outputStacks);
            }
        }
    }
}

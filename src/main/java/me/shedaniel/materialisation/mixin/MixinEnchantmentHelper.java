package me.shedaniel.materialisation.mixin;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
    @Inject(method = "getFireAspect", at = @At("RETURN"), cancellable = true)
    private static void getFireAspect(LivingEntity entity, CallbackInfoReturnable<Integer> callbackInfo) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            int fireLevel = ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, "materialisation:fire");
            if (fireLevel != 0)
                callbackInfo.setReturnValue(callbackInfo.getReturnValue() + fireLevel * 2);
        }
    }
    
    @Inject(method = "getLooting", at = @At("RETURN"), cancellable = true)
    private static void getLooting(LivingEntity entity, CallbackInfoReturnable<Integer> callbackInfo) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            int luck = ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, "materialisation:luck");
            if (luck != 0)
                callbackInfo.setReturnValue(callbackInfo.getReturnValue() + luck);
        }
    }
    
    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void getFortune(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
        if (enchantment != Enchantments.FORTUNE) return;
        if (stack.getItem() instanceof MaterialisedMiningTool) {
            int luck = ((MaterialisedMiningTool) stack.getItem()).getModifierLevel(stack, "materialisation:luck");
            if (luck != 0)
                callbackInfo.setReturnValue(callbackInfo.getReturnValue() + luck);
        }
    }
    
    @Inject(method = "calculateRequiredExperienceLevel", at = @At("HEAD"), cancellable = true)
    private static void calculateRequiredExperienceLevel(Random random_1, int int_1, int int_2, ItemStack itemStack_1, CallbackInfoReturnable<Integer> callbackInfo) {
        if (itemStack_1.getItem() instanceof MaterialisedMiningTool) {
            MaterialisedMiningTool item_1 = (MaterialisedMiningTool) itemStack_1.getItem();
            int int_3 = item_1.getEnchantability(itemStack_1);
            if (int_3 <= 0) {
                callbackInfo.setReturnValue(0);
            } else {
                if (int_2 > 15)
                    int_2 = 15;
                int int_4 = random_1.nextInt(8) + 1 + (int_2 >> 1) + random_1.nextInt(int_2 + 1);
                if (int_1 == 0) {
                    callbackInfo.setReturnValue(Math.max(int_4 / 3, 1));
                } else {
                    callbackInfo.setReturnValue(int_1 == 1 ? int_4 * 2 / 3 + 1 : Math.max(int_4, int_2 * 2));
                }
            }
        }
    }
    
    @Inject(method = "generateEnchantments", at = @At("HEAD"), cancellable = true)
    private static void getEnchantments(Random random_1, ItemStack itemStack_1, int int_1, boolean boolean_1, CallbackInfoReturnable<List<EnchantmentLevelEntry>> callbackInfo) {
        if (itemStack_1.getItem() instanceof MaterialisedMiningTool) {
            MaterialisedMiningTool item_1 = (MaterialisedMiningTool) itemStack_1.getItem();
            List<EnchantmentLevelEntry> list_1 = Lists.newArrayList();
            int int_2 = item_1.getEnchantability(itemStack_1);
            if (int_2 > 0) {
                int_1 += 1 + random_1.nextInt(int_2 / 4 + 1) + random_1.nextInt(int_2 / 4 + 1);
                float float_1 = (random_1.nextFloat() + random_1.nextFloat() - 1.0F) * 0.15F;
                int_1 = MathHelper.clamp(Math.round((float) int_1 + (float) int_1 * float_1), 1, Integer.MAX_VALUE);
                List<EnchantmentLevelEntry> list_2 = EnchantmentHelper.getPossibleEntries(int_1, itemStack_1, boolean_1);
                if (!list_2.isEmpty()) {
                    list_1.add(WeightedPicker.getRandom(random_1, list_2));
                    
                    while (random_1.nextInt(50) <= int_1) {
                        int_1 = int_1 * 4 / 5 + 1;
                        list_2 = EnchantmentHelper.getPossibleEntries(int_1, itemStack_1, boolean_1);
                        
                        for (EnchantmentLevelEntry infoEnchantment : list_1) {
                            EnchantmentHelper.removeConflicts(list_2, infoEnchantment);
                        }
                        
                        if (list_2.isEmpty()) {
                            break;
                        }
                        
                        list_1.add(WeightedPicker.getRandom(random_1, list_2));
                        int_1 /= 2;
                    }
                }
                
            }
            callbackInfo.setReturnValue(list_1);
        }
    }
}

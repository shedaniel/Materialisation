package me.shedaniel.materialisation.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean hasEnchantments();

    @Shadow
    public abstract CompoundTag getTag();

    @Inject(at = @At("HEAD"), method = "isEffectiveOn", cancellable = true)
    public void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
        if (getItem() instanceof MaterialisedMiningTool) {
            ItemStack itemStack = (ItemStack) (Object) this;
            if (MaterialisationUtils.getToolDurability(itemStack) <= 0) {
                // If there is not durability left
                info.setReturnValue(false);
            } else {
                TriState triState = MaterialisationUtils.mt_handleIsEffectiveOn(itemStack, state);
                if (triState != TriState.DEFAULT) {
                    // If we are dealing with 3rd party blocks
                    info.setReturnValue(triState.get());
                } else {
                    // Lastly if we are not dealing with 3rd party blocks with durability left
                    info.setReturnValue(((MaterialisedMiningTool) getItem()).canEffectivelyBreak(itemStack, state));
                }
            }
        }
    }

    /**
     * Applies the block breaking speed of tools, using the fabric api, overrides the hook that fabric provides
     */
    @Inject(at = @At("HEAD"), method = "getMiningSpeed", cancellable = true)
    public void getBlockBreakingSpeed(BlockState state, CallbackInfoReturnable<Float> info) {
        if (this.getItem() instanceof MaterialisedMiningTool) {
            ItemStack itemStack = (ItemStack) (Object) this;
            if (MaterialisationUtils.getToolDurability(itemStack) <= 0) {
                // If there is not durability left
                info.setReturnValue(-1f);
            } else {
                TriState triState = MaterialisationUtils.mt_handleIsEffectiveOn(itemStack, state);
                if (triState != TriState.DEFAULT) {
                    // If we are dealing with 3rd party blocks
                    info.setReturnValue(triState.get() ? MaterialisationUtils.getToolBreakingSpeed(itemStack) : 1.0F);
                } else {
                    // Lastly if we are not dealing with 3rd party blocks with durability left
                    info.setReturnValue(((MaterialisedMiningTool) getItem()).getToolBlockBreakingSpeed(itemStack, state));
                }
            }
        }
    }

    /**
     * Disable italic on tools
     */
    @Inject(method = "hasCustomName", at = @At("HEAD"), cancellable = true)
    public void hasDisplayName(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (getItem() instanceof MaterialisedMiningTool)
            callbackInfo.setReturnValue(false);
    }

    @Inject(method = "getAttributeModifiers", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/Item;getModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;",
            shift = At.Shift.BEFORE), cancellable = true)
    public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<String, EntityAttributeModifier>> callbackInfo) {
        if (getItem() instanceof MaterialisedMiningTool) {
            HashMultimap<String, EntityAttributeModifier> multimap = HashMultimap.create();
            if (slot == EquipmentSlot.MAINHAND) {
                multimap.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(MaterialisationUtils.getItemModifierSwingSpeed(), "Tool modifier", ((MaterialisedMiningTool) getItem()).getAttackSpeed(), EntityAttributeModifier.Operation.ADDITION));
                multimap.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(MaterialisationUtils.getItemModifierDamage(), "Tool modifier", MaterialisationUtils.getToolAttackDamage((ItemStack) (Object) this), EntityAttributeModifier.Operation.ADDITION));
            }
            callbackInfo.setReturnValue(multimap);
        }
    }

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    public void isEnchantable(CallbackInfoReturnable<Boolean> returnable) {
        if (getItem() instanceof MaterialisedMiningTool)
            returnable.setReturnValue(!hasEnchantments());
    }

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    public void isDamageable(CallbackInfoReturnable<Boolean> returnable) {
        if (getItem() instanceof MaterialisedMiningTool) {
            CompoundTag compoundTag_1 = getTag();
            returnable.setReturnValue(compoundTag_1 == null || !compoundTag_1.getBoolean("Unbreakable"));
        }
    }

    @Inject(method = "isDamaged", at = @At("HEAD"), cancellable = true)
    public void isDamaged(CallbackInfoReturnable<Boolean> returnable) {
        if (getItem() instanceof MaterialisedMiningTool)
            returnable.setReturnValue(false);
    }

    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    public void getDamage(CallbackInfoReturnable<Integer> returnable) {
        if (getItem() instanceof MaterialisedMiningTool) {
            int maxDurability = MaterialisationUtils.getToolMaxDurability((ItemStack) (Object) this);
            returnable.setReturnValue(maxDurability - MaterialisationUtils.getToolDurability((ItemStack) (Object) this) - 1);
        }
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> returnable) {
        if (getItem() instanceof MaterialisedMiningTool)
            returnable.setReturnValue(MaterialisationUtils.getToolMaxDurability((ItemStack) (Object) this) + 1);
    }

    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    public void setDamage(int damage, CallbackInfo info) {
        if (getItem() instanceof MaterialisedMiningTool) {
            int maxDurability = MaterialisationUtils.getToolMaxDurability((ItemStack) (Object) this);
            MaterialisationUtils.setToolDurability((ItemStack) (Object) this, maxDurability - MathHelper.clamp(damage, 0, maxDurability));
            //            MaterialisationUtils.setToolDurability((ItemStack) (Object) this, MathHelper.clamp(damage + 1, 1, maxDurability + 1));
            info.cancel();
        }
    }

}

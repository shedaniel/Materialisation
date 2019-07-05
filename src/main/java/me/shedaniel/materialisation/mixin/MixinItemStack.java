package me.shedaniel.materialisation.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    
    @Shadow
    public abstract Item getItem();
    
    @Shadow
    public abstract boolean hasEnchantments();
    
    @Shadow
    public abstract CompoundTag getTag();
    
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
    
}

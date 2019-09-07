package me.shedaniel.materialisation.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ExperienceOrbEntity.class)
public class MixinExperienceOrbEntity {
    @Inject(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;", ordinal = 0, shift = At.Shift.AFTER))
    public void onPlayerCollisionIsDamaged(PlayerEntity entity) {

    }
}

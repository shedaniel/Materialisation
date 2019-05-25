package me.shedaniel.materialisedtools.mixin;

import me.shedaniel.materialisedtools.MaterialisedTools;
import me.shedaniel.materialisedtools.items.MaterialisedToolUtils;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public class MixinItemColors {
    
    /**
     * Registers materialised tools using colors
     */
    @Inject(method = "create", at = @At(value = "RETURN"))
    private static void create(BlockColors blockColors, CallbackInfoReturnable<ItemColors> callbackInfo) {
        callbackInfo.getReturnValue().register(MaterialisedToolUtils::getColor, MaterialisedTools.MATERIALISED_PICKAXE);
    }
    
}

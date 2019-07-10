package me.shedaniel.materialisation;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MaterialisationMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }
    
    @Override
    public String getRefMapperConfig() {
        return null;
    }
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }
    
    @Override
    public List<String> getMixins() {
        Mixins.addConfiguration("materialisation.mixins.json");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
        if (FabricLoader.getInstance().isModLoaded("optifabric")) {
            System.out.println("[Materialisation] oh fuck...");
            Mixins.addConfiguration("materialisation.yesoptifine.mixins.json");
        } else {
            Mixins.addConfiguration("materialisation.nooptifine.mixins.json");
        }
        return Collections.emptyList();
    }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

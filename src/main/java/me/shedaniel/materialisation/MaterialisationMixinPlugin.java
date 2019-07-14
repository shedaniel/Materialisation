package me.shedaniel.materialisation;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.tree.*;
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
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            if (FabricLoader.getInstance().isModLoaded("optifabric")) {
                System.out.println("[Materialisation] oh f**k it's time to support optifine...");
                return Collections.singletonList("FakeItemRendererMixin");
            } else {
                Mixins.addConfiguration("materialisation.nooptifine.mixins.json");
            }
        return null;
    }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT || FabricLoader.getInstance().isModLoaded("optifabric")) {
            if (targetClassName.equals("net.minecraft.client.render.item.ItemRenderer") || targetClassName.equals("net.minecraft.class_918"))
                for(MethodNode method : targetClass.methods) {
                    if ((method.name.equals("renderGuiItemOverlay") || method.name.equals("method_4022")) && method.desc.contains("String")) {
                        InsnList instructions = method.instructions;
                        AbstractInsnNode first = instructions.get(0);
                        instructions.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 2)); // ItemStack
                        instructions.insertBefore(first, new VarInsnNode(Opcodes.ILOAD, 3)); // x
                        instructions.insertBefore(first, new VarInsnNode(Opcodes.ILOAD, 4)); // y
                        MethodInsnNode render;
                        if (targetClassName.equals("net.minecraft.class_918"))
                            render = new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/materialisation/optifine/RealItemRenderer", "renderGuiItemOverlay", "(Lnet/minecraft/class_1799;II)V", false);
                        else
                            render = new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/materialisation/optifine/RealItemRenderer", "renderGuiItemOverlay", "(Lnet/minecraft/item/ItemStack;II)V", false);
                        instructions.insertBefore(first, render);
                        break;
                    }
                }
        }
    }
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

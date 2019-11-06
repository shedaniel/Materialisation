package me.shedaniel.materialisation.compat.optifine;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
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
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String itemRenderer = mappingResolver.mapClassName("intermediary", "net.minecraft.class_918");
        String textRenderer = mappingResolver.mapClassName("intermediary", "net.minecraft.class_327");
        String itemStack = mappingResolver.mapClassName("intermediary", "net.minecraft.class_1799");
        String description = "(L" + textRenderer.replace('.', '/') + ";L" + itemStack.replace('.', '/') + ";IILjava/lang/String;)V";
        String renderGuiItemOverlay = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_918", "method_4022", "(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;IILjava/lang/String;)V");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT || FabricLoader.getInstance().isModLoaded("optifabric")) {
            if (targetClassName.equals(itemRenderer))
                for (MethodNode method : targetClass.methods) {
                    if (method.name.equals(renderGuiItemOverlay) && method.desc.equals(description)) {
                        InsnList instructions = method.instructions;
                        AbstractInsnNode first = instructions.get(0);
                        instructions.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 2)); // ItemStack
                        instructions.insertBefore(first, new VarInsnNode(Opcodes.ILOAD, 3)); // x
                        instructions.insertBefore(first, new VarInsnNode(Opcodes.ILOAD, 4)); // y
                        MethodInsnNode render = new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/materialisation/compat/optifine/RealItemRenderer", "renderGuiItemOverlay", "(L" + itemStack.replace('.', '/') + ";II)V", false);
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

package me.shedaniel.materialisation;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MaterialisationMixinPlugin implements IMixinConfigPlugin {
    private static final String[] OPTIFINE_MODIDS = {"optifabric"};
    
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
    
    private boolean isOptifineLoaded() {
        for (String modid : OPTIFINE_MODIDS) {
            if (FabricLoader.getInstance().isModLoaded(modid))
                return true;
        }
        return false;
    }
    
    @Override
    public List<String> getMixins() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            if (isOptifineLoaded()) {
                System.out.println("[Materialisation] oh f**k it's time to support optifine...");
                return Collections.singletonList("FakeItemRendererMixin");
            } else {
                Mixins.addConfiguration("materialisation.nooptifine.mixins.json");
            }
        return null;
    }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && isOptifineLoaded()) {
            MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
            String itemRenderer = mappingResolver.mapClassName("intermediary", "net.minecraft.class_918");
            String textRenderer = mappingResolver.mapClassName("intermediary", "net.minecraft.class_327");
            String itemStack = mappingResolver.mapClassName("intermediary", "net.minecraft.class_1799");
            String description = "(L" + textRenderer.replace('.', '/') + ";L" + itemStack.replace('.', '/') + ";IILjava/lang/String;)V";
            String renderGuiItemOverlay = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_918", "method_4022", "(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;IILjava/lang/String;)V");
            String isItemBarVisible = mappingResolver.mapMethodName("intermediary", "net.minecraft.class_1799", "method_31578", "()Z");
            if (targetClassName.equals(itemRenderer))
                for (MethodNode method : targetClass.methods) {
                    if (method.name.equals(renderGuiItemOverlay) && method.desc.equals(description)) {
                        InsnList instructions = method.instructions;
                        for (AbstractInsnNode instruction : instructions) {
                            if (instruction instanceof MethodInsnNode && ((MethodInsnNode) instruction).owner.equals(itemStack) && ((MethodInsnNode) instruction).name.equals(isItemBarVisible)) {
                                AbstractInsnNode current = instruction;
                                while (!(current instanceof LabelNode)) {
                                    current = current.getPrevious();
                                }
                                instructions.insertBefore(current, new LabelNode());
                                instructions.insertBefore(current, new VarInsnNode(Opcodes.ALOAD, 2)); // ItemStack
                                instructions.insertBefore(current, new VarInsnNode(Opcodes.ILOAD, 3)); // x
                                instructions.insertBefore(current, new VarInsnNode(Opcodes.ILOAD, 4)); // y
                                MethodInsnNode render = new MethodInsnNode(Opcodes.INVOKESTATIC, "me/shedaniel/materialisation/optifine/RealItemRenderer", "renderGuiItemOverlay", "(L" + itemStack.replace('.', '/') + ";II)V", false);
                                instructions.insertBefore(current, render);
                                break;
                            }
                        }
                        break;
                    }
                }
        }
    }
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

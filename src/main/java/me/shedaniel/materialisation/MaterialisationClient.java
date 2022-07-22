package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.api.ToolType;
import me.shedaniel.materialisation.gui.MaterialPreparerScreen;
import me.shedaniel.materialisation.gui.MaterialisingTableScreen;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class MaterialisationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(Materialisation.MATERIALISING_TABLE_SCREEN_HANDLER, MaterialisingTableScreen::new);
        ScreenRegistry.register(Materialisation.MATERIAL_PREPARER_SCREEN_HANDLER, MaterialPreparerScreen::new);
        ClientSidePacketRegistry.INSTANCE.register(Materialisation.MATERIALISING_TABLE_PLAY_SOUND, (packetContext, packetByteBuf) ->
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_USE, 1, 1))
        );
        Item[] colorableToolParts = {
                Materialisation.MEGAAXE_HEAD,
                Materialisation.HAMMER_HEAD,
                Materialisation.HANDLE,
                Materialisation.SWORD_BLADE,
                Materialisation.SHOVEL_HEAD,
                Materialisation.PICKAXE_HEAD,
                Materialisation.AXE_HEAD
        };
        UnclampedModelPredicateProvider brightProvider = (itemStack, world, livingEntity, seed) -> MaterialisationUtils.isHandleBright(itemStack) ? 1 : 0;
        ColorProviderRegistry.ITEM.register(MaterialisationUtils::getItemLayerColor, colorableToolParts);
        for (Item colorableToolPart : colorableToolParts) {
            ModelPredicateProviderRegistry.register(colorableToolPart, new Identifier(ModReference.MOD_ID, "bright"), brightProvider);
        }
        List<Identifier> identifiers = Stream.of(
                Materialisation.MATERIALISED_MEGAAXE,
                Materialisation.MATERIALISED_PICKAXE,
                Materialisation.MATERIALISED_SHOVEL,
                Materialisation.MATERIALISED_AXE,
                Materialisation.MATERIALISED_SWORD,
                Materialisation.MATERIALISED_HAMMER
        ).map(Registry.ITEM::getId).collect(Collectors.toList());
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        ModelLoadingRegistry.INSTANCE.registerModelProvider((resourceManager, consumer) -> {
            for (Identifier identifier : identifiers) {
                ModelIdentifier handleIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_handle"), "inventory");
                ModelIdentifier headIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_head"), "inventory");
                consumer.accept(handleIdentifier);
                consumer.accept(headIdentifier);
                ModelIdentifier brightHandleIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_handle_bright"), "inventory");
                ModelIdentifier brightHeadIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_head_bright"), "inventory");
                consumer.accept(brightHandleIdentifier);
                consumer.accept(brightHeadIdentifier);
            }
            for (Modifier modifier : Materialisation.MODIFIERS) {
                for (ToolType toolType : ToolType.values()) {
                    Identifier modelIdentifier = modifier.getModelIdentifier(toolType);
                    if (modelIdentifier != null) {
                        consumer.accept(new ModelIdentifier(modelIdentifier, "inventory"));
                    }
                }
            }
            PartMaterials.getKnownMaterials().forEach(partMaterial -> {
                for (Identifier value : partMaterial.getTexturedHeadIdentifiers().values()) {
                    consumer.accept(new ModelIdentifier(value, "inventory"));
                }
                for (Identifier value : partMaterial.getTexturedHandleIdentifiers().values()) {
                    consumer.accept(new ModelIdentifier(value, "inventory"));
                }
            });
        });
        RRPCallback.BEFORE_VANILLA.register(a -> {
            RuntimeResourcePack pack = RuntimeResourcePack.create(ModReference.MOD_ID + ":" + ModReference.MOD_ID);
            PartMaterials.getKnownMaterials().forEach(partMaterial -> {
                for (Identifier value : partMaterial.getTexturedHeadIdentifiers().values()) {
                    pack.addModel(JModel.model("item/generated").textures(new JTextures().layer0(value.getNamespace() + ":item/" + value.getPath())), new Identifier(value.getNamespace() + ":item/" + value.getPath()));
                }
                for (Identifier value : partMaterial.getTexturedHandleIdentifiers().values()) {
                    pack.addModel(JModel.model("item/generated").textures(new JTextures().layer0(value.getNamespace() + ":item/" + value.getPath())), new Identifier(value.getNamespace() + ":item/" + value.getPath()));
                }
            });
            a.add(pack);
        });
        
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> (modelIdentifier, modelProviderContext) -> {
            for (Identifier identifier : identifiers) {
                if (modelIdentifier.getNamespace().equals(identifier.getNamespace())
                    && modelIdentifier.getPath().equals(identifier.getPath())) {
                    return new UnbakedModel() {
                        @Override
                        public Collection<Identifier> getModelDependencies() {
                            return Collections.emptyList();
                        }
                        
                        @Override
                        public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> unresolvedTextureReferences) {
                            return Collections.emptyList();
                        }
                        
                        @Nullable
                        @Override
                        public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
                            return new DynamicToolBakedModel(identifier, Registry.ITEM.get(identifier));
                        }
                    };
                }
            }
            return null;
        });
    }
    
    public static class DynamicToolBakedModel implements BakedModel, FabricBakedModel {
        private final MaterialisedMiningTool tool;
        private final ModelIdentifier handleIdentifier;
        private final ModelIdentifier headIdentifier;
        private final ModelIdentifier brightHandleIdentifier;
        private final ModelIdentifier brightHeadIdentifier;
        private final Map<Modifier, Optional<ModelIdentifier>> modifierModelMap = new HashMap<>();
        
        public DynamicToolBakedModel(Identifier identifier, Item item) {
            this.handleIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_handle"), "inventory");
            this.headIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_head"), "inventory");
            this.brightHandleIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_handle_bright"), "inventory");
            this.brightHeadIdentifier = new ModelIdentifier(new Identifier(identifier.getNamespace(), identifier.getPath() + "_head_bright"), "inventory");
            this.tool = (MaterialisedMiningTool) item;
        }
        
        @Override
        public boolean isVanillaAdapter() {
            return false;
        }
        
        @Override
        public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
            
        }
        
        @Override
        public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
            BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
            PartMaterial handleMaterial = MaterialisationUtils.getMatFromString(stack.getOrCreateNbt().getString("mt_0_material")).orElseGet(() -> PartMaterials.getKnownMaterials().findFirst().get());
            PartMaterial headMaterial = MaterialisationUtils.getMatFromString(stack.getOrCreateNbt().getString("mt_1_material")).orElseGet(() -> PartMaterials.getKnownMaterials().findFirst().get());
            boolean headBright = headMaterial.isBright();
            int headColor = headMaterial.getToolColor();
            Optional<Identifier> texturedHandleIdentifier = handleMaterial.getTexturedHandleIdentifier(tool.getToolType());
            if (texturedHandleIdentifier.isPresent()) {
                ModelIdentifier modelIdentifier = new ModelIdentifier(texturedHandleIdentifier.get(), "inventory");
                BakedModel handleModel = modelManager.getModel(modelIdentifier);
                context.fallbackConsumer().accept(handleModel);
            } else {
                int handleColor = handleMaterial.getToolColor();
                boolean handleBright = handleMaterial.isBright();
                context.pushTransform(quad -> {
                    quad.nominalFace(GeometryHelper.lightFace(quad));
                    quad.spriteColor(0, handleColor, handleColor, handleColor, handleColor);
                    return true;
                });
                BakedModel handleModel = modelManager.getModel(handleBright ? brightHandleIdentifier : handleIdentifier);
                context.fallbackConsumer().accept(handleModel);
                context.popTransform();
            }
            Optional<Identifier> texturedHeadIdentifier = headMaterial.getTexturedHeadIdentifier(tool.getToolType());
            if (texturedHeadIdentifier.isPresent()) {
                ModelIdentifier modelIdentifier = new ModelIdentifier(texturedHeadIdentifier.get(), "inventory");
                BakedModel headModel = modelManager.getModel(modelIdentifier);
                context.fallbackConsumer().accept(headModel);
            } else {
                context.pushTransform(quad -> {
                    quad.nominalFace(GeometryHelper.lightFace(quad));
                    quad.spriteColor(0, headColor, headColor, headColor, headColor);
                    return true;
                });
                BakedModel headModel = modelManager.getModel(headBright ? brightHeadIdentifier : headIdentifier);
                context.fallbackConsumer().accept(headModel);
                context.popTransform();
            }
            for (Map.Entry<Modifier, Integer> entry : MaterialisationUtils.getToolModifiers(stack).entrySet()) {
                if (entry.getValue() > 0) {
                    ModelIdentifier modifierModelId = getModifierModel(entry.getKey());
                    if (modifierModelId != null) {
                        BakedModel modifierModel = modelManager.getModel(modifierModelId);
                        context.fallbackConsumer().accept(modifierModel);
                    }
                }
            }
        }
        
        public ModelIdentifier getModifierModel(Modifier modifier) {
            Optional<ModelIdentifier> identifier = modifierModelMap.get(modifier);
            if (identifier != null) return identifier.orElse(null);
            Identifier modelIdentifier = modifier.getModelIdentifier(tool.getToolType());
            modifierModelMap.put(modifier, Optional.ofNullable(modelIdentifier).map(id -> new ModelIdentifier(id, "inventory")));
            identifier = modifierModelMap.get(modifier);
            return identifier.orElse(null);
        }
        
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
            return Collections.emptyList();
        }
        
        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }
        
        @Override
        public boolean hasDepth() {
            return false;
        }
        
        @Override
        public boolean isSideLit() {
            return false;
        }
        
        @Override
        public boolean isBuiltin() {
            return false;
        }

        @Override
        public Sprite getParticleSprite() {
            return null;
        }
        
        private static final Lazy<ModelTransformation> ITEM_HANDHELD = new Lazy<>(() -> {
            try {
                Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("minecraft:models/item/handheld.json"));
                return JsonUnbakedModel.deserialize(new BufferedReader(new InputStreamReader(resource.getInputStream()))).getTransformations();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
        
        @Override
        public ModelTransformation getTransformation() {
            return ITEM_HANDHELD.get();
        }
        
        @Override
        public ModelOverrideList getOverrides() {
            return ModelOverrideList.EMPTY;
        }
    }
}

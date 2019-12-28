package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.PartMaterial;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.utils.CollectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MaterialisationREIPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier(ModReference.MOD_ID, "rei_plugin");
    public static final Identifier MATERIAL_PREPARER = new Identifier(ModReference.MOD_ID, "material_preparer");
    public static final Identifier MATERIALISING_TABLE = new Identifier(ModReference.MOD_ID, "materialising_table");
    public static final Identifier MODIFIERS = new Identifier(ModReference.MOD_ID, "modifiers");

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }

    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException {
        return SemanticVersion.parse("3.2.33");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new MaterialPreparerCategory());
        recipeHelper.registerCategory(new MaterialisingTableCategory());
        recipeHelper.registerCategory(new MaterialisationModifiersCategory());
        recipeHelper.removeAutoCraftButton(MODIFIERS);
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = CollectionUtils.map(CollectionUtils.map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            itemStacks.forEach(stack -> stack.setAmount(MathHelper.ceil(1f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.TOOL_HANDLE_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createToolHandle(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = CollectionUtils.map(CollectionUtils.map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            itemStacks.forEach(stack -> stack.setAmount(MathHelper.ceil(4f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.AXE_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createAxeHead(knownMaterial))));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.PICKAXE_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createPickaxeHead(knownMaterial))));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.SHOVEL_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createShovelHead(knownMaterial))));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.SWORD_BLADE_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createSwordBlade(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = CollectionUtils.map(CollectionUtils.map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            itemStacks.forEach(stack -> stack.setAmount(MathHelper.ceil(16f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.HAMMER_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createHammerHead(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = CollectionUtils.map(CollectionUtils.map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            itemStacks.forEach(stack -> stack.setAmount(MathHelper.ceil(64f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(EntryStack.create(Materialisation.MEGAAXE_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createMegaAxeHead(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(handle -> PartMaterials.getKnownMaterials().forEach(head -> {
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createAxeHead(head), MaterialisationUtils.createAxe(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createPickaxeHead(head), MaterialisationUtils.createPickaxe(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createShovelHead(head), MaterialisationUtils.createShovel(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createSwordBlade(head), MaterialisationUtils.createSword(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createHammerHead(head), MaterialisationUtils.createHammer(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createMegaAxeHead(head), MaterialisationUtils.createMegaAxe(handle, head)));
        }));
        for (Modifier modifier : Materialisation.MODIFIERS) {
            Pair<Integer, Integer> range = modifier.getGraphicalDescriptionRange();
            if (range != null && range.getLeft() <= range.getRight()) {
                for (int level = range.getLeft(); level <= range.getRight(); level++) {
                    recipeHelper.registerDisplay(MODIFIERS, new MaterialisationModifiersDisplay(Materialisation.MODIFIERS.getId(modifier), level));
                }
            }
        }
    }

    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        List<EntryStack> handle = Lists.newArrayList();
        List<EntryStack> pickaxe_head = Lists.newArrayList();
        List<EntryStack> axe_head = Lists.newArrayList();
        List<EntryStack> shovel_head = Lists.newArrayList();
        List<EntryStack> sword_head = Lists.newArrayList();
        List<EntryStack> hammer_head = Lists.newArrayList();
        List<EntryStack> megaaxe_head = Lists.newArrayList();
        List<EntryStack> pickaxe = Lists.newArrayList();
        List<EntryStack> axe = Lists.newArrayList();
        List<EntryStack> shovel = Lists.newArrayList();
        List<EntryStack> sword = Lists.newArrayList();
        List<EntryStack> hammer = Lists.newArrayList();
        List<EntryStack> megaaxe = Lists.newArrayList();
        for (PartMaterial material : PartMaterials.getKnownMaterialList()) {
            handle.add(EntryStack.create(MaterialisationUtils.createToolHandle(material)));
            pickaxe_head.add(EntryStack.create(MaterialisationUtils.createPickaxeHead(material)));
            axe_head.add(EntryStack.create(MaterialisationUtils.createAxeHead(material)));
            shovel_head.add(EntryStack.create(MaterialisationUtils.createShovelHead(material)));
            sword_head.add(EntryStack.create(MaterialisationUtils.createSwordBlade(material)));
            hammer_head.add(EntryStack.create(MaterialisationUtils.createHammerHead(material)));
            megaaxe_head.add(EntryStack.create(MaterialisationUtils.createMegaAxeHead(material)));

            pickaxe.add(EntryStack.create(MaterialisationUtils.createPickaxe(material, material)));
            axe.add(EntryStack.create(MaterialisationUtils.createAxe(material, material)));
            shovel.add(EntryStack.create(MaterialisationUtils.createShovel(material, material)));
            sword.add(EntryStack.create(MaterialisationUtils.createSwordBlade(material)));
            hammer.add(EntryStack.create(MaterialisationUtils.createHammer(material, material)));
            megaaxe.add(EntryStack.create(MaterialisationUtils.createMegaAxe(material, material)));
        }
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.HANDLE), handle);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.PICKAXE_HEAD), pickaxe_head);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.AXE_HEAD), axe_head);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.SHOVEL_HEAD), shovel_head);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.SWORD_BLADE), sword_head);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.HAMMER_HEAD), hammer_head);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MEGAAXE_HEAD), megaaxe_head);

        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MATERIALISED_PICKAXE), pickaxe);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MATERIALISED_AXE), axe);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MATERIALISED_SHOVEL), shovel);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MATERIALISED_SWORD), sword);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MATERIALISED_HAMMER), hammer);
        entryRegistry.queueRegisterEntryAfter(EntryStack.create(Materialisation.MATERIALISED_MEGAAXE), megaaxe);

        entryRegistry.getStacksList().removeIf(entry -> entry.getType() == EntryStack.Type.ITEM && entry.getItemStack().getItem() instanceof MaterialisedMiningTool && !entry.getItemStack().getOrCreateTag().contains("mt_done_tool") && !entry.getItemStack().getOrCreateTag().getBoolean("mt_done_tool"));
        entryRegistry.getStacksList().removeIf(entry -> entry.getType() == EntryStack.Type.ITEM && entry.getItemStack().getItem() instanceof ColoredItem && !entry.getItemStack().getOrCreateTag().contains("mt_0_material"));
    }

}

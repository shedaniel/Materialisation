package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.Modifier;
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
        return SemanticVersion.parse("3.2.28");
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
        EntryStack handle = EntryStack.create(Materialisation.HANDLE);
        EntryStack pickaxe_head = EntryStack.create(Materialisation.PICKAXE_HEAD);
        EntryStack axe_head = EntryStack.create(Materialisation.AXE_HEAD);
        EntryStack shovel_head = EntryStack.create(Materialisation.SHOVEL_HEAD);
        EntryStack sword_head = EntryStack.create(Materialisation.SWORD_BLADE);
        EntryStack hammer_head = EntryStack.create(Materialisation.HAMMER_HEAD);
        EntryStack megaaxe_head = EntryStack.create(Materialisation.MEGAAXE_HEAD);
        EntryStack pickaxe = EntryStack.create(Materialisation.MATERIALISED_PICKAXE);
        EntryStack axe = EntryStack.create(Materialisation.MATERIALISED_AXE);
        EntryStack shovel = EntryStack.create(Materialisation.MATERIALISED_SHOVEL);
        EntryStack sword = EntryStack.create(Materialisation.MATERIALISED_SWORD);
        EntryStack hammer = EntryStack.create(Materialisation.MATERIALISED_HAMMER);
        EntryStack megaaxe = EntryStack.create(Materialisation.MATERIALISED_MEGAAXE);
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createToolHandle).forEach(stack -> entryRegistry.registerEntriesAfter(handle, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createPickaxeHead).forEach(stack -> entryRegistry.registerEntriesAfter(pickaxe_head, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createAxeHead).forEach(stack -> entryRegistry.registerEntriesAfter(axe_head, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createShovelHead).forEach(stack -> entryRegistry.registerEntriesAfter(shovel_head, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createSwordBlade).forEach(stack -> entryRegistry.registerEntriesAfter(sword_head, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createHammerHead).forEach(stack -> entryRegistry.registerEntriesAfter(hammer_head, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createMegaAxeHead).forEach(stack -> entryRegistry.registerEntriesAfter(megaaxe_head, EntryStack.create(stack)));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerEntriesAfter(pickaxe, EntryStack.create(MaterialisationUtils.createPickaxe(material, material))));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerEntriesAfter(axe, EntryStack.create(MaterialisationUtils.createAxe(material, material))));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerEntriesAfter(shovel, EntryStack.create(MaterialisationUtils.createShovel(material, material))));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerEntriesAfter(sword, EntryStack.create(MaterialisationUtils.createSword(material, material))));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerEntriesAfter(hammer, EntryStack.create(MaterialisationUtils.createHammer(material, material))));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerEntriesAfter(megaaxe, EntryStack.create(MaterialisationUtils.createMegaAxe(material, material))));
        entryRegistry.getStacksList().removeIf(entry -> entry.getType() == EntryStack.Type.ITEM && entry.getItemStack().getItem() instanceof MaterialisedMiningTool && !entry.getItemStack().getOrCreateTag().contains("mt_done_tool") && !entry.getItemStack().getOrCreateTag().getBoolean("mt_done_tool"));
        entryRegistry.getStacksList().removeIf(entry -> entry.getType() == EntryStack.Type.ITEM && entry.getItemStack().getItem() instanceof ColoredItem && !entry.getItemStack().getOrCreateTag().contains("mt_0_material"));
    }

}

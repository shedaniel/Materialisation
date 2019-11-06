package me.shedaniel.materialisation.rei;

import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.rei.api.Entry;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class MaterialisationREIPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier(ModReference.MOD_ID, "rei_plugin");
    public static final Identifier MATERIAL_PREPARER = new Identifier(ModReference.MOD_ID, "material_preparer");
    public static final Identifier MATERIALISING_TABLE = new Identifier(ModReference.MOD_ID, "materialising_table");

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }

    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException {
        return SemanticVersion.parse("3.0-pre");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new MaterialPreparerCategory());
        recipeHelper.registerCategory(new MaterialisingTableCategory());
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<ItemStack> itemStacks = ingredient.getStacksList().stream().map(ItemStack::copy).collect(Collectors.toList());
            itemStacks.forEach(stack -> stack.setCount(MathHelper.ceil(1f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.TOOL_HANDLE_PATTERN), itemStacks, MaterialisationUtils.createToolHandle(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<ItemStack> itemStacks = ingredient.getStacksList().stream().map(ItemStack::copy).collect(Collectors.toList());
            itemStacks.forEach(stack -> stack.setCount(MathHelper.ceil(4f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.AXE_HEAD_PATTERN), itemStacks, MaterialisationUtils.createAxeHead(knownMaterial)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.PICKAXE_HEAD_PATTERN), itemStacks, MaterialisationUtils.createPickaxeHead(knownMaterial)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.SHOVEL_HEAD_PATTERN), itemStacks, MaterialisationUtils.createShovelHead(knownMaterial)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.SWORD_BLADE_PATTERN), itemStacks, MaterialisationUtils.createSwordBlade(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<ItemStack> itemStacks = ingredient.getStacksList().stream().map(ItemStack::copy).collect(Collectors.toList());
            itemStacks.forEach(stack -> stack.setCount(MathHelper.ceil(16f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.HAMMER_HEAD_PATTERN), itemStacks, MaterialisationUtils.createHammerHead(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<ItemStack> itemStacks = ingredient.getStacksList().stream().map(ItemStack::copy).collect(Collectors.toList());
            itemStacks.forEach(stack -> stack.setCount(MathHelper.ceil(64f / aFloat)));
            recipeHelper.registerDisplay(MATERIAL_PREPARER, new MaterialPreparerDisplay(new ItemStack(Materialisation.MEGAAXE_HEAD_PATTERN), itemStacks, MaterialisationUtils.createMegaAxeHead(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(handle -> PartMaterials.getKnownMaterials().forEach(head -> {
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createAxeHead(head), MaterialisationUtils.createAxe(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createPickaxeHead(head), MaterialisationUtils.createPickaxe(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createShovelHead(head), MaterialisationUtils.createShovel(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createSwordBlade(head), MaterialisationUtils.createSword(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createHammerHead(head), MaterialisationUtils.createHammer(handle, head)));
            recipeHelper.registerDisplay(MATERIALISING_TABLE, new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createMegaAxeHead(head), MaterialisationUtils.createMegaAxe(handle, head)));
        }));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createToolHandle).forEach(stack -> entryRegistry.registerItemStack(Materialisation.HANDLE, stack));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createPickaxeHead).forEach(stack -> entryRegistry.registerItemStack(Materialisation.PICKAXE_HEAD, stack));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createAxeHead).forEach(stack -> entryRegistry.registerItemStack(Materialisation.AXE_HEAD, stack));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createShovelHead).forEach(stack -> entryRegistry.registerItemStack(Materialisation.SHOVEL_HEAD, stack));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createSwordBlade).forEach(stack -> entryRegistry.registerItemStack(Materialisation.SWORD_BLADE, stack));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createHammerHead).forEach(stack -> entryRegistry.registerItemStack(Materialisation.HAMMER_HEAD, stack));
        PartMaterials.getKnownMaterials().map(MaterialisationUtils::createMegaAxeHead).forEach(stack -> entryRegistry.registerItemStack(Materialisation.MEGAAXE_HEAD, stack));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerItemStack(Materialisation.MATERIALISED_PICKAXE, MaterialisationUtils.createPickaxe(material, material)));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerItemStack(Materialisation.MATERIALISED_AXE, MaterialisationUtils.createAxe(material, material)));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerItemStack(Materialisation.MATERIALISED_SHOVEL, MaterialisationUtils.createShovel(material, material)));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerItemStack(Materialisation.MATERIALISED_SWORD, MaterialisationUtils.createSword(material, material)));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerItemStack(Materialisation.MATERIALISED_HAMMER, MaterialisationUtils.createHammer(material, material)));
        PartMaterials.getKnownMaterials().forEach(material -> entryRegistry.registerItemStack(Materialisation.MATERIALISED_MEGAAXE, MaterialisationUtils.createMegaAxe(material, material)));
        entryRegistry.getModifiableEntryList().removeIf(entry -> entry.getEntryType() == Entry.Type.ITEM && entry.getItemStack().getItem() instanceof MaterialisedMiningTool && !entry.getItemStack().getOrCreateTag().containsKey("mt_done_tool") && !entry.getItemStack().getOrCreateTag().getBoolean("mt_done_tool"));
        entryRegistry.getModifiableEntryList().removeIf(entry -> entry.getEntryType() == Entry.Type.ITEM && entry.getItemStack().getItem() instanceof ColoredItem && !entry.getItemStack().getOrCreateTag().containsKey("mt_0_material"));
    }

}

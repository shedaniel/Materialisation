package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Function;

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
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new MaterialPreparerCategory());
        recipeHelper.registerCategory(new MaterialisingTableCategory());
        recipeHelper.registerCategory(new MaterialisationModifiersCategory());
        recipeHelper.removeAutoCraftButton(MODIFIERS);
    }
    
    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                stack.setAmount(MathHelper.ceil(1f / aFloat));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.TOOL_HANDLE_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createToolHandle(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                stack.setAmount(MathHelper.ceil(4f / aFloat));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.AXE_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createAxeHead(knownMaterial))));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.PICKAXE_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createPickaxeHead(knownMaterial))));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.SHOVEL_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createShovelHead(knownMaterial))));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.SWORD_BLADE_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createSwordBlade(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                stack.setAmount(MathHelper.ceil(16f / aFloat));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.HAMMER_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createHammerHead(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStack::create), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                stack.setAmount(MathHelper.ceil(64f / aFloat));
            recipeHelper.registerDisplay(new MaterialPreparerDisplay(EntryStack.create(Materialisation.MEGAAXE_HEAD_PATTERN), itemStacks, EntryStack.create(MaterialisationUtils.createMegaAxeHead(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(handle -> PartMaterials.getKnownMaterials().forEach(head -> {
            recipeHelper.registerDisplay(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createAxeHead(head), MaterialisationUtils.createAxe(handle, head)));
            recipeHelper.registerDisplay(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createPickaxeHead(head), MaterialisationUtils.createPickaxe(handle, head)));
            recipeHelper.registerDisplay(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createShovelHead(head), MaterialisationUtils.createShovel(handle, head)));
            recipeHelper.registerDisplay(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createSwordBlade(head), MaterialisationUtils.createSword(handle, head)));
            recipeHelper.registerDisplay(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createHammerHead(head), MaterialisationUtils.createHammer(handle, head)));
            recipeHelper.registerDisplay(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createMegaAxeHead(head), MaterialisationUtils.createMegaAxe(handle, head)));
        }));
        for (Modifier modifier : Materialisation.MODIFIERS) {
            Pair<Integer, Integer> range = modifier.getGraphicalDescriptionRange();
            if (range != null && range.getLeft() <= range.getRight()) {
                for (int level = range.getLeft(); level <= range.getRight(); level++) {
                    recipeHelper.registerDisplay(new MaterialisationModifiersDisplay(Materialisation.MODIFIERS.getId(modifier), level));
                }
            }
        }
    }
    
    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> l = Lists.newArrayList();
        for (T t : list) {
            l.add(function.apply(t));
        }
        return l;
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        List<EntryStack> handles = Lists.newArrayList();
        List<EntryStack> pickaxeHeads = Lists.newArrayList();
        List<EntryStack> axeHeads = Lists.newArrayList();
        List<EntryStack> shovelHeads = Lists.newArrayList();
        List<EntryStack> swordHeads = Lists.newArrayList();
        List<EntryStack> hammerHeads = Lists.newArrayList();
        List<EntryStack> megaaxeHeads = Lists.newArrayList();
        List<EntryStack> pickaxes = Lists.newArrayList();
        List<EntryStack> axes = Lists.newArrayList();
        List<EntryStack> shovels = Lists.newArrayList();
        List<EntryStack> swords = Lists.newArrayList();
        List<EntryStack> hammers = Lists.newArrayList();
        List<EntryStack> megaaxes = Lists.newArrayList();
        PartMaterials.getKnownMaterials().forEach(material -> {
            handles.add(EntryStack.create(MaterialisationUtils.createToolHandle(material)));
            pickaxeHeads.add(EntryStack.create(MaterialisationUtils.createPickaxeHead(material)));
            axeHeads.add(EntryStack.create(MaterialisationUtils.createAxeHead(material)));
            shovelHeads.add(EntryStack.create(MaterialisationUtils.createShovelHead(material)));
            swordHeads.add(EntryStack.create(MaterialisationUtils.createSwordBlade(material)));
            hammerHeads.add(EntryStack.create(MaterialisationUtils.createHammerHead(material)));
            megaaxeHeads.add(EntryStack.create(MaterialisationUtils.createMegaAxeHead(material)));
        });
        PartMaterials.getKnownMaterials().forEach(firstMaterial -> {
            PartMaterials.getKnownMaterials().forEach(secondMaterial -> {
                pickaxes.add(EntryStack.create(MaterialisationUtils.createPickaxe(firstMaterial, secondMaterial)));
                axes.add(EntryStack.create(MaterialisationUtils.createAxe(firstMaterial, secondMaterial)));
                shovels.add(EntryStack.create(MaterialisationUtils.createShovel(firstMaterial, secondMaterial)));
                swords.add(EntryStack.create(MaterialisationUtils.createSword(firstMaterial, secondMaterial)));
                hammers.add(EntryStack.create(MaterialisationUtils.createHammer(firstMaterial, secondMaterial)));
                megaaxes.add(EntryStack.create(MaterialisationUtils.createMegaAxe(firstMaterial, secondMaterial)));
            });
        });
        for (EntryStack stack : handles)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : pickaxeHeads)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : axeHeads)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : shovelHeads)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : swordHeads)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : hammerHeads)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : megaaxeHeads)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : pickaxes)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : axes)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : shovels)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : swords)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : hammers)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        for (EntryStack stack : megaaxes)
            stack.setting(EntryStack.Settings.CHECK_TAGS, EntryStack.Settings.TRUE);
        
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.HANDLE), handles);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.PICKAXE_HEAD), pickaxeHeads);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.AXE_HEAD), axeHeads);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.SHOVEL_HEAD), shovelHeads);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.SWORD_BLADE), swordHeads);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.HAMMER_HEAD), hammerHeads);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MEGAAXE_HEAD), megaaxeHeads);
        
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MATERIALISED_PICKAXE), pickaxes);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MATERIALISED_AXE), axes);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MATERIALISED_SHOVEL), shovels);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MATERIALISED_SWORD), swords);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MATERIALISED_HAMMER), hammers);
        entryRegistry.registerEntriesAfter(EntryStack.create(Materialisation.MATERIALISED_MEGAAXE), megaaxes);
        
        entryRegistry.removeEntryIf(entry -> entry.getType() == EntryStack.Type.ITEM && entry.getItemStack().getItem() instanceof MaterialisedMiningTool && !entry.getItemStack().getOrCreateTag().contains("mt_done_tool") && !entry.getItemStack().getOrCreateTag().getBoolean("mt_done_tool"));
        entryRegistry.removeEntryIf(entry -> entry.getType() == EntryStack.Type.ITEM && entry.getItemStack().getItem() instanceof ColoredItem && !entry.getItemStack().getOrCreateTag().contains("mt_0_material"));
    }
    
}

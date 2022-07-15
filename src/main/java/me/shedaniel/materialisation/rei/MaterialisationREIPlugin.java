package me.shedaniel.materialisation.rei;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.Materialisation;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.ModReference;
import me.shedaniel.materialisation.api.BetterIngredient;
import me.shedaniel.materialisation.api.Modifier;
import me.shedaniel.materialisation.api.PartMaterials;
import me.shedaniel.materialisation.items.ColoredItem;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class MaterialisationREIPlugin implements REIClientPlugin {
    
    public static final Identifier PLUGIN = new Identifier(ModReference.MOD_ID, "rei_plugin");
    public static final Identifier MATERIAL_PREPARER = new Identifier(ModReference.MOD_ID, "material_preparer");
    public static final CategoryIdentifier MATERIALISING_TABLE = CategoryIdentifier.of(ModReference.MOD_ID, "materialising_table");
    public static final CategoryIdentifier MODIFIERS = CategoryIdentifier.of(ModReference.MOD_ID, "modifiers");
    
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }
    
    @Override
    public void registerCategories(CategoryRegistry recipeHelper) {
        recipeHelper.add(new MaterialPreparerCategory());
        recipeHelper.add(new MaterialisingTableCategory());
        recipeHelper.add(new MaterialisationModifiersCategory());
        recipeHelper.removePlusButton(MODIFIERS);
    }

    public static List<EntryIngredient> stackToIngredients(ItemStack stack) {
        return new ArrayList<>(){{add(EntryIngredient.of(EntryStacks.of(stack)));}};
    }

    @Override
    public void registerDisplays(DisplayRegistry recipeHelper) {
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.TOOL_HANDLE_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createToolHandle(knownMaterial)));
            //List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStacks::of), EntryStack::copy);
            //recipeHelper.add(new MaterialPreparerDisplay(EntryStacks.of(Materialisation.TOOL_HANDLE_PATTERN), itemStacks, EntryStacks.of(MaterialisationUtils.createToolHandle(knownMaterial))));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStacks::of), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                ((ItemStack) stack.getValue()).setCount(MathHelper.ceil(4f / aFloat));
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.AXE_HEAD_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createAxeHead(knownMaterial)));
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.PICKAXE_HEAD_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createPickaxeHead(knownMaterial)));
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.SHOVEL_HEAD_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createShovelHead(knownMaterial)));
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.SWORD_BLADE_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createSwordBlade(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStacks::of), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                ((ItemStack) stack.getValue()).setCount(MathHelper.ceil(16f / aFloat));
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.HAMMER_HEAD_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createHammerHead(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(knownMaterial -> knownMaterial.getIngredientMap().forEach((ingredient, aFloat) -> {
            List<EntryStack> itemStacks = map(map(ingredient.getStacksList(), EntryStacks::of), EntryStack::copy);
            for (EntryStack stack : itemStacks)
                ((ItemStack) stack.getValue()).setCount(MathHelper.ceil(64f / aFloat));
            recipeHelper.add(new MaterialPreparerDisplay(new ItemStack(Materialisation.MEGAAXE_HEAD_PATTERN), ingredient.getStacksList(), MaterialisationUtils.createMegaAxeHead(knownMaterial)));
        }));
        PartMaterials.getKnownMaterials().forEach(handle -> PartMaterials.getKnownMaterials().forEach(head -> {
            recipeHelper.add(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createAxeHead(head), MaterialisationUtils.createAxe(handle, head)));
            recipeHelper.add(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createPickaxeHead(head), MaterialisationUtils.createPickaxe(handle, head)));
            recipeHelper.add(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createShovelHead(head), MaterialisationUtils.createShovel(handle, head)));
            recipeHelper.add(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createSwordBlade(head), MaterialisationUtils.createSword(handle, head)));
            recipeHelper.add(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createHammerHead(head), MaterialisationUtils.createHammer(handle, head)));
            recipeHelper.add(new MaterialisingTableDisplay(MaterialisationUtils.createToolHandle(handle), MaterialisationUtils.createMegaAxeHead(head), MaterialisationUtils.createMegaAxe(handle, head)));
        }));
        for (Modifier modifier : Materialisation.MODIFIERS) {
            Pair<Integer, Integer> range = modifier.getGraphicalDescriptionRange();
            if (range != null && range.getLeft() <= range.getRight()) {
                for (int level = range.getLeft(); level <= range.getRight(); level++) {
                    recipeHelper.add(new MaterialisationModifiersDisplay(Materialisation.MODIFIERS.getId(modifier), level));
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

    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        List<EntryStack<ItemStack>> handles = Lists.newArrayList();
        List<EntryStack<ItemStack>> pickaxeHeads = Lists.newArrayList();
        List<EntryStack<ItemStack>> axeHeads = Lists.newArrayList();
        List<EntryStack<ItemStack>> shovelHeads = Lists.newArrayList();
        List<EntryStack<ItemStack>> swordHeads = Lists.newArrayList();
        List<EntryStack<ItemStack>> hammerHeads = Lists.newArrayList();
        List<EntryStack<ItemStack>> megaaxeHeads = Lists.newArrayList();
        List<EntryStack<ItemStack>> pickaxes = Lists.newArrayList();
        List<EntryStack<ItemStack>> axes = Lists.newArrayList();
        List<EntryStack<ItemStack>> shovels = Lists.newArrayList();
        List<EntryStack<ItemStack>> swords = Lists.newArrayList();
        List<EntryStack<ItemStack>> hammers = Lists.newArrayList();
        List<EntryStack<ItemStack>> megaaxes = Lists.newArrayList();
        PartMaterials.getKnownMaterials().forEach(material -> {
            handles.add(EntryStacks.of(MaterialisationUtils.createToolHandle(material)));
            pickaxeHeads.add(EntryStacks.of(MaterialisationUtils.createPickaxeHead(material)));
            axeHeads.add(EntryStacks.of(MaterialisationUtils.createAxeHead(material)));
            shovelHeads.add(EntryStacks.of(MaterialisationUtils.createShovelHead(material)));
            swordHeads.add(EntryStacks.of(MaterialisationUtils.createSwordBlade(material)));
            hammerHeads.add(EntryStacks.of(MaterialisationUtils.createHammerHead(material)));
            megaaxeHeads.add(EntryStacks.of(MaterialisationUtils.createMegaAxeHead(material)));
        });
        PartMaterials.getKnownMaterials().forEach(firstMaterial -> PartMaterials.getKnownMaterials().forEach(secondMaterial -> {
            pickaxes.add(EntryStacks.of(MaterialisationUtils.createPickaxe(firstMaterial, secondMaterial)));
            axes.add(EntryStacks.of(MaterialisationUtils.createAxe(firstMaterial, secondMaterial)));
            shovels.add(EntryStacks.of(MaterialisationUtils.createShovel(firstMaterial, secondMaterial)));
            swords.add(EntryStacks.of(MaterialisationUtils.createSword(firstMaterial, secondMaterial)));
            hammers.add(EntryStacks.of(MaterialisationUtils.createHammer(firstMaterial, secondMaterial)));
            megaaxes.add(EntryStacks.of(MaterialisationUtils.createMegaAxe(firstMaterial, secondMaterial)));
        }));
        /*
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

         */
        
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.HANDLE), handles);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.PICKAXE_HEAD), pickaxeHeads);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.AXE_HEAD), axeHeads);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.SHOVEL_HEAD), shovelHeads);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.SWORD_BLADE), swordHeads);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.HAMMER_HEAD), hammerHeads);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MEGAAXE_HEAD), megaaxeHeads);
        
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MATERIALISED_PICKAXE), pickaxes);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MATERIALISED_AXE), axes);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MATERIALISED_SHOVEL), shovels);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MATERIALISED_SWORD), swords);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MATERIALISED_HAMMER), hammers);
        entryRegistry.addEntriesAfter(EntryStacks.of(Materialisation.MATERIALISED_MEGAAXE), megaaxes);
        
        entryRegistry.removeEntryIf(entry -> entry.getType() == VanillaEntryTypes.ITEM && ((ItemStack) entry.getValue()).getItem() instanceof MaterialisedMiningTool && !((ItemStack) entry.getValue()).getOrCreateNbt().contains("mt_done_tool") && !((ItemStack) entry.getValue()).getOrCreateNbt().getBoolean("mt_done_tool"));
        entryRegistry.removeEntryIf(entry -> entry.getType() == VanillaEntryTypes.ITEM && ((ItemStack) entry.getValue()).getItem() instanceof ColoredItem && !((ItemStack) entry.getValue()).getOrCreateNbt().contains("mt_0_material"));
    }
    
}

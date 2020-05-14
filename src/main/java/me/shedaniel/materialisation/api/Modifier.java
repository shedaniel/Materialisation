package me.shedaniel.materialisation.api;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Modifier {
    
    static Builder builder() {
        return new Builder();
    }
    
    default boolean isApplicableTo(ItemStack tool) {
        return tool.getItem() instanceof MaterialisedMiningTool && getMaximumLevel(tool) > 0;
    }
    
    default int getDurabilityCost(ItemStack tool, int level) {
        return 0;
    }
    
    default int getMaximumLevel(ItemStack tool) {
        return 0;
    }
    
    default int getExtraMiningSpeed(ItemStack tool, int level) {
        return 0;
    }
    
    default int getExtraAttackDamage(ItemStack tool, int level) {
        return 0;
    }
    
    default int getExtraMiningLevel(ItemStack tool, int level) {
        return 0;
    }
    
    default int getExtraEnchantability(ItemStack tool, int level) {
        return 0;
    }
    
    default float getDurabilityMultiplier(ItemStack tool, int level) {
        return 1f;
    }
    
    default float getMiningSpeedMultiplier(ItemStack tool, int level) {
        return 1f;
    }
    
    default float getAttackDamageMultiplier(ItemStack tool, int level) {
        return 1f;
    }
    
    default ImmutableList<ToolType> getApplicableToolTypes() {
        return ImmutableList.of();
    }
    
    default List<Text> getModifierDescription(int level) {
        return Collections.emptyList();
    }
    
    default boolean hasGraphicalDescription(int level) {
        Pair<Integer, Integer> range = getGraphicalDescriptionRange();
        if (range == null) return false;
        return level >= range.getLeft() && level <= range.getRight();
    }
    
    @Nullable
    default Pair<Integer, Integer> getGraphicalDescriptionRange() {
        return null;
    }
    
    @Nullable
    default Identifier getModelIdentifier(ToolType type) {
        return null;
    }
    
    class ModifierImpl implements Modifier {
        private final BiFunction<ItemStack, Integer, Integer> durabilityCost;
        private final Supplier<ImmutableList<ToolType>> applicableToolTypes;
        private final Function<ToolType, Integer> maximumLevel;
        private final BiFunction<ItemStack, Integer, Integer> extraMiningSpeed;
        private final BiFunction<ItemStack, Integer, Integer> extraAttackDamage;
        private final BiFunction<ItemStack, Integer, Integer> extraMiningLevel;
        private final BiFunction<ItemStack, Integer, Integer> extraEnchantability;
        private final BiFunction<ItemStack, Integer, Float> durabilityMultiplier;
        private final BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier;
        private final BiFunction<ItemStack, Integer, Float> attackDamageMultiplier;
        private final Function<ToolType, Identifier> modelIdentifier;
        @Nullable
        private final Function<Integer, List<Text>> description;
        @Nullable
        private final Pair<Integer, Integer> graphicalDescriptionRange;
        
        private ModifierImpl(
                BiFunction<ItemStack, Integer, Integer> durabilityCost,
                Supplier<ImmutableList<ToolType>> applicableToolTypes,
                Function<ToolType, Integer> maximumLevel,
                BiFunction<ItemStack, Integer, Integer> extraMiningSpeed,
                BiFunction<ItemStack, Integer, Integer> extraAttackDamage,
                BiFunction<ItemStack, Integer, Integer> extraMiningLevel,
                BiFunction<ItemStack, Integer, Integer> extraEnchantability,
                BiFunction<ItemStack, Integer, Float> durabilityMultiplier,
                BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier,
                BiFunction<ItemStack, Integer, Float> attackDamageMultiplier,
                @Nullable Function<Integer, List<Text>> description,
                @Nullable Pair<Integer, Integer> graphicalDescriptionRange,
                Function<ToolType, Identifier> modelIdentifier
        ) {
            this.durabilityCost = durabilityCost;
            this.applicableToolTypes = applicableToolTypes;
            this.maximumLevel = maximumLevel;
            this.extraMiningSpeed = extraMiningSpeed;
            this.extraAttackDamage = extraAttackDamage;
            this.extraMiningLevel = extraMiningLevel;
            this.extraEnchantability = extraEnchantability;
            this.durabilityMultiplier = durabilityMultiplier;
            this.miningSpeedMultiplier = miningSpeedMultiplier;
            this.attackDamageMultiplier = attackDamageMultiplier;
            this.description = description;
            this.graphicalDescriptionRange = graphicalDescriptionRange;
            this.modelIdentifier = modelIdentifier;
        }
        
        private static Modifier create(
                BiFunction<ItemStack, Integer, Integer> durabilityCost,
                Supplier<ImmutableList<ToolType>> applicableToolTypes,
                Function<ToolType, Integer> maximumLevel,
                BiFunction<ItemStack, Integer, Integer> extraMiningSpeed,
                BiFunction<ItemStack, Integer, Integer> extraAttackDamage,
                BiFunction<ItemStack, Integer, Integer> extraMiningLevel,
                BiFunction<ItemStack, Integer, Integer> extraEnchantability,
                BiFunction<ItemStack, Integer, Float> durabilityMultiplier,
                BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier,
                BiFunction<ItemStack, Integer, Float> attackDamageMultiplier,
                @Nullable Function<Integer, List<Text>> description,
                @Nullable Pair<Integer, Integer> graphicalDescriptionRange,
                Function<ToolType, Identifier> modelIdentifier
        ) {
            return new ModifierImpl(
                    durabilityCost,
                    applicableToolTypes,
                    maximumLevel,
                    extraMiningSpeed,
                    extraAttackDamage,
                    extraMiningLevel,
                    extraEnchantability,
                    durabilityMultiplier,
                    miningSpeedMultiplier,
                    attackDamageMultiplier,
                    description,
                    graphicalDescriptionRange,
                    modelIdentifier
            );
        }
        
        @Override
        public List<Text> getModifierDescription(int level) {
            if (description != null) {
                List<Text> apply = description.apply(level);
                if (apply != null) {
                    List<Text> desc = new ArrayList<>();
                    for (Text s : apply) desc.add(s);
                    return desc;
                }
            }
            return Collections.emptyList();
        }
        
        @Nullable
        @Override
        public Pair<Integer, Integer> getGraphicalDescriptionRange() {
            return graphicalDescriptionRange;
        }
        
        @Override
        public int getDurabilityCost(ItemStack tool, int level) {
            return durabilityCost.apply(tool, level);
        }
        
        @Override
        public int getMaximumLevel(ItemStack tool) {
            return tool.getItem() instanceof MaterialisedMiningTool && getApplicableToolTypes().contains(((MaterialisedMiningTool) tool.getItem()).getToolType()) ?
                    maximumLevel.apply(((MaterialisedMiningTool) tool.getItem()).getToolType()) : 0;
        }
        
        @Override
        public int getExtraMiningSpeed(ItemStack tool, int level) {
            return extraMiningSpeed.apply(tool, level);
        }
        
        @Override
        public int getExtraAttackDamage(ItemStack tool, int level) {
            return extraAttackDamage.apply(tool, level);
        }
        
        @Override
        public int getExtraMiningLevel(ItemStack tool, int level) {
            return extraMiningLevel.apply(tool, level);
        }
        
        @Override
        public int getExtraEnchantability(ItemStack tool, int level) {
            return extraEnchantability.apply(tool, level);
        }
        
        @Override
        public float getDurabilityMultiplier(ItemStack tool, int level) {
            return durabilityMultiplier.apply(tool, level);
        }
        
        @Override
        public float getMiningSpeedMultiplier(ItemStack tool, int level) {
            return miningSpeedMultiplier.apply(tool, level);
        }
        
        @Override
        public float getAttackDamageMultiplier(ItemStack tool, int level) {
            return attackDamageMultiplier.apply(tool, level);
        }
        
        @Override
        public ImmutableList<ToolType> getApplicableToolTypes() {
            return applicableToolTypes.get();
        }
        
        @Nullable
        @Override
        public Identifier getModelIdentifier(ToolType type) {
            return modelIdentifier.apply(type);
        }
    }
    
    class Builder {
        private BiFunction<ItemStack, Integer, Integer> durabilityCost = (tool, level) -> 0;
        private Supplier<ImmutableList<ToolType>> applicableToolTypes = ImmutableList::of;
        private Function<ToolType, Integer> maximumLevel = (type) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraMiningSpeed = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraAttackDamage = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraMiningLevel = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraEnchantability = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Float> durabilityMultiplier = (tool, level) -> 1f;
        private BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier = (tool, level) -> 1f;
        private BiFunction<ItemStack, Integer, Float> attackDamageMultiplier = (tool, level) -> 1f;
        private Map<ToolType, Identifier> modelIdentifiers = new HashMap<>();
        @Nullable
        private Function<Integer, List<Text>> description;
        @Nullable
        private Pair<Integer, Integer> graphicalDescriptionRange;
        
        private Builder() {
        }
        
        public Modifier build() {
            return ModifierImpl.create(
                    durabilityCost,
                    applicableToolTypes,
                    maximumLevel,
                    extraMiningSpeed,
                    extraAttackDamage,
                    extraMiningLevel,
                    extraEnchantability,
                    durabilityMultiplier,
                    miningSpeedMultiplier,
                    attackDamageMultiplier,
                    description,
                    graphicalDescriptionRange,
                    type -> modelIdentifiers.get(type)
            );
        }
        
        public Builder description(@Nullable Function<Integer, List<Text>> description) {
            this.description = description;
            return this;
        }
        
        public Builder graphicalDescriptionLevelRange(@Nullable Pair<Integer, Integer> range) {
            this.graphicalDescriptionRange = range;
            return this;
        }
        
        public Builder graphicalDescriptionLevelRange(int min, int max) {
            this.graphicalDescriptionRange = new Pair<>(min, max);
            return this;
        }
        
        public Builder durabilityCost(BiFunction<ItemStack, Integer, Integer> durabilityCost) {
            this.durabilityCost = durabilityCost;
            return this;
        }
        
        public Builder applicableToolTypes(Supplier<ImmutableList<ToolType>> applicableToolTypes) {
            this.applicableToolTypes = applicableToolTypes;
            return this;
        }
        
        public Builder maximumLevel(Function<ToolType, Integer> maximumLevel) {
            this.maximumLevel = maximumLevel;
            return this;
        }
        
        public Builder extraMiningSpeed(BiFunction<ItemStack, Integer, Integer> extraMiningSpeed) {
            this.extraMiningSpeed = extraMiningSpeed;
            return this;
        }
        
        public Builder extraAttackDamage(BiFunction<ItemStack, Integer, Integer> extraAttackDamage) {
            this.extraAttackDamage = extraAttackDamage;
            return this;
        }
        
        public Builder extraMiningLevel(BiFunction<ItemStack, Integer, Integer> extraMiningLevel) {
            this.extraMiningLevel = extraMiningLevel;
            return this;
        }
        
        public Builder extraEnchantability(BiFunction<ItemStack, Integer, Integer> extraEnchantability) {
            this.extraEnchantability = extraEnchantability;
            return this;
        }
        
        public Builder durabilityMultiplier(BiFunction<ItemStack, Integer, Float> durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }
        
        public Builder miningSpeedMultiplier(BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier) {
            this.miningSpeedMultiplier = miningSpeedMultiplier;
            return this;
        }
        
        public Builder attackDamageMultiplier(BiFunction<ItemStack, Integer, Float> attackDamageMultiplier) {
            this.attackDamageMultiplier = attackDamageMultiplier;
            return this;
        }
        
        public Builder durabilityCost(int durabilityCost) {
            this.durabilityCost = (tool, level) -> durabilityCost;
            return this;
        }
        
        public Builder applicableToolTypes(ImmutableList<ToolType> applicableToolTypes) {
            this.applicableToolTypes = () -> applicableToolTypes;
            return this;
        }
        
        public Builder maximumLevel(int maximumLevel) {
            this.maximumLevel = type -> maximumLevel;
            return this;
        }
        
        public Builder extraMiningSpeed(int extraMiningSpeed) {
            this.extraMiningSpeed = (tool, level) -> extraMiningSpeed;
            return this;
        }
        
        public Builder extraAttackDamage(int extraAttackDamage) {
            this.extraAttackDamage = (tool, level) -> extraAttackDamage;
            return this;
        }
        
        public Builder extraMiningLevel(int extraMiningLevel) {
            this.extraMiningLevel = (tool, level) -> extraMiningLevel;
            return this;
        }
        
        public Builder extraEnchantability(int extraEnchantability) {
            this.extraEnchantability = (tool, level) -> extraEnchantability;
            return this;
        }
        
        public Builder durabilityMultiplier(float durabilityMultiplier) {
            this.durabilityMultiplier = (tool, level) -> durabilityMultiplier;
            return this;
        }
        
        public Builder miningSpeedMultiplier(float miningSpeedMultiplier) {
            this.miningSpeedMultiplier = (tool, level) -> miningSpeedMultiplier;
            return this;
        }
        
        public Builder attackDamageMultiplier(float attackDamageMultiplier) {
            this.attackDamageMultiplier = (tool, level) -> attackDamageMultiplier;
            return this;
        }
        
        public Builder model(Identifier modelId, ToolType... types) {
            for (ToolType type : types) {
                modelIdentifiers.put(type, modelId);
            }
            return this;
        }
    }
}
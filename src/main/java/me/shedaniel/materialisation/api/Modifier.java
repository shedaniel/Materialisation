package me.shedaniel.materialisation.api;

import com.google.common.collect.ImmutableList;
import me.shedaniel.materialisation.MaterialisationUtils;
import me.shedaniel.materialisation.items.MaterialisedMiningTool;
import net.minecraft.item.ItemStack;

import java.util.function.BiFunction;

public class Modifier {
    private final BiFunction<ItemStack, Integer, Integer> durabilityCost;
    private final BiFunction<ItemStack, Integer, ImmutableList<ToolType>> applicableToolTypes;
    private final BiFunction<ItemStack, Integer, Integer> maximalLevel;
    private final BiFunction<ItemStack, Integer, Integer> extraMiningSpeed;
    private final BiFunction<ItemStack, Integer, Integer> extraAttackDamage;
    private final BiFunction<ItemStack, Integer, Integer> extraMiningLevel;
    private final BiFunction<ItemStack, Integer, Integer> extraEnchantability;
    private final BiFunction<ItemStack, Integer, Float> durabilityMultiplier;
    private final BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier;
    private final BiFunction<ItemStack, Integer, Float> attackDamageMultiplier;

    public Modifier(
            BiFunction<ItemStack, Integer, Integer> durabilityCost,
            BiFunction<ItemStack, Integer, ImmutableList<ToolType>> applicableToolTypes,
            BiFunction<ItemStack, Integer, Integer> maximalLevel,
            BiFunction<ItemStack, Integer, Integer> extraMiningSpeed,
            BiFunction<ItemStack, Integer, Integer> extraAttackDamage,
            BiFunction<ItemStack, Integer, Integer> extraMiningLevel,
            BiFunction<ItemStack, Integer, Integer> extraEnchantability,
            BiFunction<ItemStack, Integer, Float> durabilityMultiplier,
            BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier,
            BiFunction<ItemStack, Integer, Float> attackDamageMultiplier
    ) {
        this.durabilityCost = durabilityCost;
        this.applicableToolTypes = applicableToolTypes;
        this.maximalLevel = maximalLevel;
        this.extraMiningSpeed = extraMiningSpeed;
        this.extraAttackDamage = extraAttackDamage;
        this.extraMiningLevel = extraMiningLevel;
        this.extraEnchantability = extraEnchantability;
        this.durabilityMultiplier = durabilityMultiplier;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamageMultiplier = attackDamageMultiplier;
    }

    public boolean isApplicableTo(ItemStack tool, int level) {
        return getApplicableToolTypes(tool, level).contains(((MaterialisedMiningTool) tool.getItem()).getToolType())
                && MaterialisationUtils.getToolMaxDurability(tool) > getDurabilityCost(tool, level);
    }

    public int getDurabilityCost(ItemStack tool, int level) {
        return durabilityCost.apply(tool, level);
    }

    public ImmutableList<ToolType> getApplicableToolTypes(ItemStack tool, int level) {
        return applicableToolTypes.apply(tool, level);
    }

    public int getMaximalLevel(ItemStack tool, int level) {
        return maximalLevel.apply(tool, level);
    }

    public int getExtraMiningSpeed(ItemStack tool, int level) {
        return extraMiningSpeed.apply(tool, level);
    }

    public int getExtraAttackDamage(ItemStack tool, int level) {
        return extraAttackDamage.apply(tool, level);
    }

    public int getExtraMiningLevel(ItemStack tool, int level) {
        return extraMiningLevel.apply(tool, level);
    }

    public int getExtraEnchantability(ItemStack tool, int level) {
        return extraEnchantability.apply(tool, level);
    }

    public float getDurabilityMultiplier(ItemStack tool, int level) {
        return durabilityMultiplier.apply(tool, level);
    }

    public float getMiningSpeedMultiplier(ItemStack tool, int level) {
        return miningSpeedMultiplier.apply(tool, level);
    }

    public float getAttackDamageMultiplier(ItemStack tool, int level) {
        return attackDamageMultiplier.apply(tool, level);
    }

    public boolean isApplicableTo(ItemStack tool) {
        return getApplicableToolTypes(tool, 0).contains(((MaterialisedMiningTool) tool.getItem()).getToolType())
                && MaterialisationUtils.getToolMaxDurability(tool) > getDurabilityCost(tool, 0);
    }

    public int getDurabilityCost(ItemStack tool) {
        return durabilityCost.apply(tool, 0);
    }

    public ImmutableList<ToolType> getApplicableToolTypes(ItemStack tool) {
        return applicableToolTypes.apply(tool, 0);
    }

    public int getMaximalLevel(ItemStack tool) {
        return maximalLevel.apply(tool, 0);
    }

    public int getExtraMiningSpeed(ItemStack tool) {
        return extraMiningSpeed.apply(tool, 0);
    }

    public int getExtraAttackDamage(ItemStack tool) {
        return extraAttackDamage.apply(tool, 0);
    }

    public int getExtraMiningLevel(ItemStack tool) {
        return extraMiningLevel.apply(tool, 0);
    }

    public int getExtraEnchantability(ItemStack tool) {
        return extraEnchantability.apply(tool, 0);
    }

    public float getDurabilityMultiplier(ItemStack tool) {
        return durabilityMultiplier.apply(tool, 0);
    }

    public float getMiningSpeedMultiplier(ItemStack tool) {
        return miningSpeedMultiplier.apply(tool, 0);
    }

    public float getAttackDamageMultiplier(ItemStack tool) {
        return attackDamageMultiplier.apply(tool, 0);
    }

    public static class Builder {
        private BiFunction<ItemStack, Integer, Integer> durabilityCost = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, ImmutableList<ToolType>> applicableToolTypes = (tool, level) -> ImmutableList.copyOf(ToolType.values());
        private BiFunction<ItemStack, Integer, Integer> maximalLevel = (tool, level) -> 3;
        private BiFunction<ItemStack, Integer, Integer> extraMiningSpeed = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraAttackDamage = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraMiningLevel = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Integer> extraEnchantability = (tool, level) -> 0;
        private BiFunction<ItemStack, Integer, Float> durabilityMultiplier = (tool, level) -> 1f;
        private BiFunction<ItemStack, Integer, Float> miningSpeedMultiplier = (tool, level) -> 1f;
        private BiFunction<ItemStack, Integer, Float> attackDamageMultiplier = (tool, level) -> 1f;

        public Modifier build() {
            return new Modifier(
                    durabilityCost,
                    applicableToolTypes,
                    maximalLevel,
                    extraMiningSpeed,
                    extraAttackDamage,
                    extraMiningLevel,
                    extraEnchantability,
                    durabilityMultiplier,
                    miningSpeedMultiplier,
                    attackDamageMultiplier
            );
        }

        public Builder durabilityCost(BiFunction<ItemStack, Integer, Integer> durabilityCost) {
            this.durabilityCost = durabilityCost;
            return this;
        }

        public Builder applicableToolTypes(BiFunction<ItemStack, Integer, ImmutableList<ToolType>> applicableToolTypes) {
            this.applicableToolTypes = applicableToolTypes;
            return this;
        }

        public Builder maximalLevel(BiFunction<ItemStack, Integer, Integer> maximalLevel) {
            this.maximalLevel = maximalLevel;
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
            this.applicableToolTypes = (tool, level) -> applicableToolTypes;
            return this;
        }

        public Builder maximalLevel(int maximalLevel) {
            this.maximalLevel = (tool, level) -> maximalLevel;
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
    }
}
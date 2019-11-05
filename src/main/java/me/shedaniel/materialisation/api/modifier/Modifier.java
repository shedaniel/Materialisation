package me.shedaniel.materialisation.api.modifier;

public class Modifier {
    private final int extraModifierSlots;
    private final int extraDurability;
    private final int extraMiningSpeed;
    private final int extraAttackDamage;
    private final int extraMiningLevel;
    private final float durabilityMultiplier;
    private final float miningSpeedMultiplier;
    private final float attackDamageMultiplier;

    private Modifier(
            int extraModifierSlotsCount,
            int extraDurability,
            int extraMiningSpeed,
            int extraAttackDamage,
            int extraMiningLevel,
            float durabilityMultiplier,
            float miningSpeedMultiplier,
            float attackDamageMultiplier
    ) {
        this.extraModifierSlots = extraModifierSlotsCount;
        this.extraDurability = extraDurability;
        this.extraMiningSpeed = extraMiningSpeed;
        this.extraAttackDamage = extraAttackDamage;
        this.extraMiningLevel = extraMiningLevel;
        this.durabilityMultiplier = durabilityMultiplier;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamageMultiplier = attackDamageMultiplier;
    }

    public int getExtraModifierSlots() {
        return extraModifierSlots;
    }

    public int getExtraDurability() {
        return extraDurability;
    }

    public int getExtraMiningSpeed() {
        return extraMiningSpeed;
    }

    public int getExtraAttackDamage() {
        return extraAttackDamage;
    }

    public int getExtraMiningLevel() {
        return extraMiningLevel;
    }

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public float getMiningSpeedMultiplier() {
        return miningSpeedMultiplier;
    }

    public float getAttackDamageMultiplier() {
        return attackDamageMultiplier;
    }

    public static class Builder {
        private int extraModifierSlots = 0;
        private int extraDurability = 0;
        private int extraMiningSpeed = 0;
        private int extraDamage = 0;
        private int extraMiningLevel = 0;
        private float durabilityMultiplier = 1f;
        private float miningSpeedMultiplier = 1f;
        private float attackDamageMultiplier = 1f;

        public Modifier build() {
            return new Modifier(
                    extraModifierSlots,
                    extraDurability,
                    extraMiningSpeed,
                    extraDamage,
                    extraMiningLevel,
                    durabilityMultiplier,
                    miningSpeedMultiplier,
                    attackDamageMultiplier
            );
        }

        public Builder extraModifierSlots(int extraModifierSlots) {
            this.extraModifierSlots = extraModifierSlots;
            return this;
        }

        public Builder extraDurability(int extraDurability) {
            this.extraDurability = extraDurability;
            return this;
        }

        public Builder extraMiningSpeed(int extraMiningSpeed) {
            this.extraMiningSpeed = extraMiningSpeed;
            return this;
        }

        public Builder extraDamage(int extraDamage) {
            this.extraDamage = extraDamage;
            return this;
        }

        public Builder extraMiningLevel(int extraMiningLevel) {
            this.extraMiningLevel = extraMiningLevel;
            return this;
        }

        public Builder durabilityMultiplier(float durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }

        public Builder miningSpeedMultiplier(float miningSpeedMultiplier) {
            this.miningSpeedMultiplier = miningSpeedMultiplier;
            return this;
        }

        public Builder attackDamageMultiplier(float attackDamageMultiplier) {
            this.attackDamageMultiplier = attackDamageMultiplier;
            return this;
        }
    }
}

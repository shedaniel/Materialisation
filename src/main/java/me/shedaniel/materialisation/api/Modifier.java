package me.shedaniel.materialisation.api;

import net.minecraft.item.Item;

public class Modifier {
    private final Item modifyingItem;
    private final int additionalModifierSlotsCount;
    private final int additionalDurability;
    private final int additionalSpeed;
    private final int additionalAttackDamage;
    private final int additionalMiningLevel;
    private final float durabilityMultiplier;
    private final float speedMultiplier;
    private final float attackDamageMultiplier;

    private Modifier(
            Item modifyingItem,
            int additionalModifierSlotsCount,
            int additionalDurability,
            int additionalSpeed,
            int additionalAttackDamage,
            int additionalMiningLevel,
            float durabilityMultiplier,
            float speedMultiplier,
            float attackDamageMultiplier
    ) {
        this.modifyingItem = modifyingItem;
        this.additionalModifierSlotsCount = additionalModifierSlotsCount;
        this.additionalDurability = additionalDurability;
        this.additionalSpeed = additionalSpeed;
        this.additionalAttackDamage = additionalAttackDamage;
        this.additionalMiningLevel = additionalMiningLevel;
        this.durabilityMultiplier = durabilityMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.attackDamageMultiplier = attackDamageMultiplier;
    }

    public Item getModifyingItem() {
        return modifyingItem;
    }

    public int getAdditionalModifierSlotsCount() {
        return additionalModifierSlotsCount;
    }

    public int getAdditionalDurability() {
        return additionalDurability;
    }

    public int getAdditionalSpeed() {
        return additionalSpeed;
    }

    public int getAdditionalAttackDamage() {
        return additionalAttackDamage;
    }

    public int getAdditionalMiningLevel() {
        return additionalMiningLevel;
    }

    public float getDurabilityMultiplier() {
        return durabilityMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getAttackDamageMultiplier() {
        return attackDamageMultiplier;
    }

    public static class Builder {
        private final Item modifyingItem;
        private int additionalModifierSlotsCount = 0;
        private int additionalDurability = 0;
        private int additionalSpeed = 0;
        private int additionalAttackDamage = 0;
        private int additionalMiningLevel = 0;
        private float durabilityMultiplier = 1f;
        private float speedMultiplier = 1f;
        private float attackDamageMultiplier = 1f;

        public Builder(Item modifyingItem) {
            this.modifyingItem = modifyingItem;
        }

        public Modifier build() {
            return new Modifier(
                    modifyingItem,
                    additionalModifierSlotsCount,
                    additionalDurability,
                    additionalSpeed,
                    additionalAttackDamage,
                    additionalMiningLevel,
                    durabilityMultiplier,
                    speedMultiplier,
                    attackDamageMultiplier
            );
        }

        public Builder setAdditionalModifierSlotsCount(int additionalModifierSlotsCount) {
            this.additionalModifierSlotsCount = additionalModifierSlotsCount;
            return this;
        }

        public Builder setAdditionalDurability(int additionalDurability) {
            this.additionalDurability = additionalDurability;
            return this;
        }

        public Builder setAdditionalSpeed(int additionalSpeed) {
            this.additionalSpeed = additionalSpeed;
            return this;
        }

        public Builder setAdditionalAttackDamage(int additionalAttackDamage) {
            this.additionalAttackDamage = additionalAttackDamage;
            return this;
        }

        public Builder setAdditionalMiningLevel(int additionalMiningLevel) {
            this.additionalMiningLevel = additionalMiningLevel;
            return this;
        }

        public Builder setDurabilityMultiplier(float durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }

        public Builder setSpeedMultiplier(float speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
            return this;
        }

        public Builder setAttackDamageMultiplier(float attackDamageMultiplier) {
            this.attackDamageMultiplier = attackDamageMultiplier;
            return this;
        }
    }
}

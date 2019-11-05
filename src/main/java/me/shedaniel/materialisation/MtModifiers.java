package me.shedaniel.materialisation;

import me.shedaniel.materialisation.api.Modifier;
import net.minecraft.item.Items;

public class MtModifiers {
    public static final Modifier DIAMOND;

    static {
        DIAMOND = new Modifier.Builder(Items.DIAMOND)
                .setAdditionalDurability(300)
                .setAdditionalMiningLevel(1)
                .build();
    }
}

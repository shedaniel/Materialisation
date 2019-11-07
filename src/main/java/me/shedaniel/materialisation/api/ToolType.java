package me.shedaniel.materialisation.api;

public enum ToolType {
    AXE, HAMMER, MEGA_AXE, PICKAXE, SHOVEL, SWORD, UNKNOWN;

    public static final ToolType[] MINING_TOOLS = new ToolType[] { AXE, HAMMER, MEGA_AXE, PICKAXE, SHOVEL };
}

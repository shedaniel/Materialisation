package me.shedaniel.materialisation.api;

public enum ToolType {
    AXE,
    HAMMER,
    MEGA_AXE,
    PICKAXE,
    SHOVEL,
    SWORD,
    UNKNOWN;
    
    public static final ToolType[] MINING_TOOLS = new ToolType[]{AXE, HAMMER, MEGA_AXE, PICKAXE, SHOVEL};
    public static final ToolType[] WEAPON = new ToolType[]{AXE, HAMMER, MEGA_AXE, SWORD};
    public static final ToolType[] ALL = new ToolType[]{AXE, HAMMER, MEGA_AXE, PICKAXE, SHOVEL, SWORD};
}

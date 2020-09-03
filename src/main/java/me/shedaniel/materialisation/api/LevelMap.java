package me.shedaniel.materialisation.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelMap<T> extends HashMap<Integer, List<T>> {
    @SuppressWarnings("CanBeFinal")
    private List<T> base = new ArrayList<>();
    
    public List<T> getBase() {
        return base;
    }
}

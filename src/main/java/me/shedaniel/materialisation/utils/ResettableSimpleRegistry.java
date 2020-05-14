package me.shedaniel.materialisation.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.MutableRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class ResettableSimpleRegistry<T> extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap(256);
    protected BiMap<Identifier, T> entries = HashBiMap.create();
    protected List<T> randomEntries;
    private int nextId;
    
    public ResettableSimpleRegistry() {
    }
    
    public void reset() {
        indexedEntries = new Int2ObjectBiMap<>(256);
        entries = HashBiMap.create();
        randomEntries = null;
        nextId = 0;
    }
    
    public <V extends T> V set(int int_1, Identifier identifier_1, V object_1) {
        this.indexedEntries.put(object_1, int_1);
        Validate.notNull(identifier_1);
        Validate.notNull(object_1);
        this.randomEntries = null;
        if (this.entries.containsKey(identifier_1)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", identifier_1);
        }
        
        this.entries.put(identifier_1, object_1);
        if (this.nextId <= int_1) {
            this.nextId = int_1 + 1;
        }
        
        return object_1;
    }
    
    public <V extends T> V add(Identifier identifier_1, V object_1) {
        return this.set(this.nextId, identifier_1, object_1);
    }
    
    @Nullable
    public Identifier getId(T object_1) {
        return this.entries.inverse().get(object_1);
    }
    
    public int getRawId(@Nullable T object_1) {
        return this.indexedEntries.getId(object_1);
    }
    
    @Nullable
    public T get(int int_1) {
        return this.indexedEntries.get(int_1);
    }
    
    public Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }
    
    @Nullable
    public T get(@Nullable Identifier identifier_1) {
        return this.entries.get(identifier_1);
    }
    
    public Optional<T> getOrEmpty(@Nullable Identifier identifier_1) {
        return Optional.ofNullable(this.entries.get(identifier_1));
    }
    
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entries.keySet());
    }
    
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }
    
    @Nullable
    public T getRandom(Random random_1) {
        if (this.randomEntries == null) {
            Collection<T> collection_1 = this.entries.values();
            if (collection_1.isEmpty()) {
                return null;
            }
            
            this.randomEntries = ImmutableList.copyOf(collection_1);
        }
        
        return (T) this.randomEntries.get(random_1.nextInt(this.randomEntries.size()));
    }
    
    @Environment(EnvType.CLIENT)
    public boolean containsId(Identifier identifier_1) {
        return this.entries.containsKey(identifier_1);
    }
}
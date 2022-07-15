package me.shedaniel.materialisation.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import me.shedaniel.materialisation.ModReference;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class ResettableSimpleRegistry<T> extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected Int2ObjectBiMap<T> indexedEntries = new Int2ObjectBiMap<>(256);
    protected BiMap<Identifier, T> entries = HashBiMap.create();
    private BiMap<RegistryKey<T>, T> entriesByKey = HashBiMap.create();
    private Set<RegistryKey<T>> loadedKeys = Sets.newIdentityHashSet();
    protected Object[] randomEntries;
    private int nextId;
    
    public ResettableSimpleRegistry(String id) {
        super(RegistryKey.ofRegistry(new Identifier(ModReference.MOD_ID, id)), Lifecycle.stable());
    }

    @SuppressWarnings("unused")
    public ResettableSimpleRegistry(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }
    
    public void reset() {
        indexedEntries = new Int2ObjectBiMap<>(256);
        entries = HashBiMap.create();
        entriesByKey = HashBiMap.create();
        loadedKeys = Sets.newIdentityHashSet();
        randomEntries = null;
        nextId = 0;
    }

    @Override
    public <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle) {
        this.indexedEntries.put(entry, rawId);
        Validate.notNull(key);
        Validate.notNull(entry);
        this.randomEntries = null;
        if (this.entriesByKey.containsKey(key)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", key);
        }
        
        this.entries.put(key.getValue(), entry);
        this.entriesByKey.put(key, entry);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        
        return entry;
    }

    @Override
    public <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle) {
        return this.set(this.nextId, key, entry, lifecycle);
    }

    @Override
    public <V extends T> V replace(OptionalInt rawId, RegistryKey<T> key, V newEntry, Lifecycle lifecycle) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Nullable
    public Identifier getId(T entry) {
        return this.entries.inverse().get(entry);
    }
    
    public Optional<RegistryKey<T>> getKey(T value) {
        return Optional.ofNullable(this.entriesByKey.inverse().get(value));
    }
    
    public int getRawId(@Nullable T entry) {
        return this.indexedEntries.getRawId(entry);
    }
    
    @Nullable
    public T get(@Nullable RegistryKey<T> key) {
        return this.entriesByKey.get(key);
    }
    
    @Nullable
    public T get(int index) {
        return this.indexedEntries.get(index);
    }
    
    public @NotNull Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }
    
    @Nullable
    public T get(@Nullable Identifier id) {
        return this.entries.get(id);
    }

    @Override
    protected Lifecycle getEntryLifecycle(T entry) {
        return null;
    }

    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    protected Lifecycle method_31139(T object) {
        return null;
    }

    public Lifecycle method_31138() {
        return null;
    }

    public Optional<T> getOrEmpty(@Nullable Identifier id) {
        return Optional.ofNullable(this.entries.get(id));
    }
    
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entries.keySet());
    }
    
    public Set<Map.Entry<RegistryKey<T>, T>> getEntries() {
        return Collections.unmodifiableMap(this.entriesByKey).entrySet();
    }
    
    @Nullable
    @SuppressWarnings("unused")
    public T getRandom(Random random) {
        if (this.randomEntries == null) {
            Collection<T> collection = this.entries.values();
            if (collection.isEmpty()) {
                return null;
            }
            
            this.randomEntries = collection.toArray(new Object[0]);
        }

        //noinspection unchecked
        return (T) Util.getRandom(this.randomEntries, random);
    }
    
    public boolean containsId(Identifier id) {
        return this.entries.containsKey(id);
    }

    @Override
    public boolean contains(RegistryKey<T> key) {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean isLoaded(RegistryKey<T> registryKey) {
        return this.loadedKeys.contains(registryKey);
    }

    @SuppressWarnings("unused")
    public void markLoaded(RegistryKey<T> registryKey) {
        this.loadedKeys.add(registryKey);
    }

    public <V extends T> V method_31062(OptionalInt optionalInt, RegistryKey<T> registryKey, V object, Lifecycle lifecycle) {
        return null;
    }
}
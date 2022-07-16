package me.shedaniel.materialisation.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import me.shedaniel.materialisation.ModReference;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.*;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class ResettableSimpleRegistry<T> extends MutableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected Int2ObjectBiMap<T> indexedEntries = Int2ObjectBiMap.create(256);
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
        indexedEntries = Int2ObjectBiMap.create(256);
        entries = HashBiMap.create();
        entriesByKey = HashBiMap.create();
        loadedKeys = Sets.newIdentityHashSet();
        randomEntries = null;
        nextId = 0;
    }

    @Override
    public RegistryEntry<T> set(int rawId, RegistryKey<T> key, T entry, Lifecycle lifecycle) {
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

        return RegistryEntry.of(entry);
    }

    @Override
    public RegistryEntry<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle) {
        return this.set(this.nextId, key, entry, lifecycle);
    }

    @Override
    public RegistryEntry<T> replace(OptionalInt rawId, RegistryKey<T> key, T newEntry, Lifecycle lifecycle) {
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

    @Override
    public int size() {
        return 0;
    }

    public @NotNull Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }
    
    @Nullable
    public T get(@Nullable Identifier id) {
        return this.entries.get(id);
    }

    @Override
    public Lifecycle getEntryLifecycle(T entry) {
        return null;
    }

    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    public Optional<T> getOrEmpty(@Nullable Identifier id) {
        return Optional.ofNullable(this.entries.get(id));
    }
    
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.entries.keySet());
    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntrySet() {
        return Collections.unmodifiableSet(this.entriesByKey.entrySet());
    }
    
    @Nullable
    @SuppressWarnings("unused")
    public Optional<RegistryEntry<T>> getRandom(Random random) {
        if (this.randomEntries == null) {
            Collection<T> collection = this.entries.values();
            if (collection.isEmpty()) {
                return null;
            }
            
            this.randomEntries = collection.toArray(new Object[0]);
        }

        //noinspection unchecked
        return Optional.of(RegistryEntry.of((T) Util.getRandom(this.randomEntries, random)));
    }
    
    public boolean containsId(Identifier id) {
        return this.entries.containsKey(id);
    }

    @Override
    public boolean contains(RegistryKey<T> key) {
        return false;
    }

    @Override
    public Registry<T> freeze() {
        return null;
    }

    @Override
    public RegistryEntry<T> getOrCreateEntry(RegistryKey<T> key) {
        return null;
    }

    @Override
    public RegistryEntry.Reference<T> createEntry(T value) {
        return null;
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(int rawId) {
        return Optional.empty();
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key) {
        return Optional.empty();
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return null;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
        return Optional.empty();
    }

    @Override
    public RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag) {
        return null;
    }

    @Override
    public Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries() {
        return null;
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return null;
    }

    @Override
    public boolean containsTag(TagKey<T> tag) {
        return false;
    }

    @Override
    public void clearTags() {

    }

    @Override
    public void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {

    }

    @SuppressWarnings("unused")
    public boolean isLoaded(RegistryKey<T> registryKey) {
        return this.loadedKeys.contains(registryKey);
    }

    @SuppressWarnings("unused")
    public void markLoaded(RegistryKey<T> registryKey) {
        this.loadedKeys.add(registryKey);
    }

}
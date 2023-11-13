package context;

import properties.PropertyUtility;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static properties.PropertyUtility.*;

/**
 * The ContextStore class provides a thread-safe storage for key-value pairs in a ConcurrentHashMap.
 * Each thread has its own map, which avoids concurrency issues and ensures thread safety.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in an earlier version)
 */
@SuppressWarnings({"unused", "unchecked"})
public class ContextStore {

    /**
     * ThreadLocal variable to store a ConcurrentHashMap for each thread.
     */
    private static final ThreadLocal<ConcurrentHashMap<Object, Object>> map = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * Associates the specified value with the specified key in the ContextStore.
     * If the key or value is null, the operation is skipped.
     * This method is synchronized to ensure thread safety during the put operation.
     *
     * @param <K>   The type of the keys in the ContextStore.
     * @param <V>   The type of the values in the ContextStore.
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @throws IllegalArgumentException If either the provided key or value is null.
     * @see ContextStore#map
     */
    public static synchronized <K, V> void put(K key, V value) {
        if (key != null && value != null) {
            map.get().put(key, value);
        }
    }

    /**
     * Removes the entry with the specified key from the ContextStore.
     * If the key is not present in the ContextStore, null is returned.
     * This method is synchronized to ensure thread safety during the removal process.
     *
     * @param <K> The type of the keys in the ContextStore.
     * @param <V> The type of the values in the ContextStore.
     * @param key The key whose associated entry is to be removed.
     * @return The value associated with the specified key before removal, or null if the key is not present.
     * @throws IllegalArgumentException If the provided key is null.
     * @see ContextStore#map
     */
    public static synchronized <K, V> V remove(K key) {
        return key != null ? ((ConcurrentHashMap<K, V>) map.get()).remove(key) : null;
    }

    /**
     * Retrieves the value associated with the specified key from the ContextStore.
     * If the key is not present in the ContextStore, null is returned.
     * This method is synchronized to ensure thread safety during the retrieval process.
     *
     * @param <K> The type of the keys in the map.
     * @param <V> The type of the values in the map.
     * @param key The key whose associated value is to be retrieved.
     * @return The value associated with the specified key, or null if the key is not present.
     * @throws IllegalArgumentException If the provided key is null.
     * @see ContextStore#map
     */
    public static synchronized <K, V> V get(K key) {
        return key != null ? ((ConcurrentHashMap<K, V>) map.get()).get(key) : null;
    }

    /**
     * Retrieves the value associated with the specified key from the ContextStore.
     * If the key is not present in the ContextStore, the provided defaultValue is returned.
     * This method is synchronized to ensure thread safety during the retrieval process.
     *
     * @param <K>           The type of the keys in the map.
     * @param <V>           The type of the values in the map.
     * @param key           The key whose associated value is to be retrieved.
     * @param defaultValue  The default value to be returned if the key is not present in the map.
     * @return The value associated with the specified key, or the defaultValue if the key is not present.
     * @throws IllegalArgumentException If the provided key is null.
     * @see ContextStore#map
     */
    public static synchronized <K, V> V get(K key, V defaultValue) {
        return key != null ? ((ConcurrentHashMap<K, V>) map.get()).get(key) : defaultValue;
    }

    /**
     * Retrieves an unmodifiable set view of the keys contained in the ContextStore.
     * This method is synchronized to ensure thread safety during the retrieval process.
     *
     * @param <K> The type of the keys in the ContextStore.
     * @param <V> The type of the values in the ContextStore.
     * @return An unmodifiable set view of the keys in the ContextStore.
     * @see ContextStore#map
     */
    public static synchronized <K, V> Set<K> items() {
        return Collections.unmodifiableSet(((ConcurrentHashMap<K, V>) map.get()).keySet());
    }

    /**
     * Clears all key-value mappings from the ContextStore.
     * This method is synchronized to ensure thread safety during the clear operation.
     *
     * @see ContextStore#map
     */
    static synchronized void clear() {
        map.get().clear();
    }

    /**
     * Updates the value associated with the specified key in the ContextStore.
     * If the key or value is null, the update operation is skipped.
     * This method is synchronized to ensure thread safety during the update process.
     *
     * @param <K>   The type of the keys in the ContextStore.
     * @param <V>   The type of the values in the ContextStore.
     * @param key   The key whose associated value is to be updated.
     * @param value The new value to be associated with the specified key.
     * @throws IllegalArgumentException If either the provided key or value is null.
     * @see ContextStore#map
     */
    public static synchronized <K, V> void update(K key, V value) {
        if (key != null && value != null) {
            ((ConcurrentHashMap<K, V>) map.get()).computeIfPresent(key, (k, oldValue) -> value);
        }
    }

    /**
     * Merges the entries from the specified map into the ContextStore.
     * This method is synchronized to ensure thread safety during the merge operation.
     *
     * @param maps Maps containing entries to be merged into the ContextStore.
     * @see ContextStore#map
     */
    public static synchronized void merge(Map<?, ?>... maps) {
        for (Map<?, ?> map:maps) ContextStore.map.get().putAll(map);
    }

    /**
     * Loads properties from one or more property files, merging them into the ContextStore for the current thread.
     * The method is synchronized to ensure thread safety during the loading and merging process.
     *
     * @param propertyNames An array of property file names or paths to be loaded and merged.
     * @throws IllegalArgumentException If the provided array of property names is null or empty.
     * @throws RuntimeException If an error occurs during the loading or merging of properties.
     *                          This can be an IOException or any other runtime exception.
     *                          The specific exception details are logged for further investigation.
     * @see PropertyUtility#fromPropertyFile(String) fromPropertyFile(String)
     * @see ContextStore#merge(Map...) UtilityPropertiesMap.merge(Map)
     */
    public static synchronized void loadProperties(String... propertyNames) {
        for (String propertyName : propertyNames) merge(fromPropertyFile(propertyName));
    }
}

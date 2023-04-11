package context;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
     * Adds a key-value pair to the map for the current thread.
     * @param key the key to be added
     * @param value the value to be added
     */
    public static synchronized <K, V> void put(K key, V value) {
        if (key != null && value != null) {
            map.get().put(key, value);
        }
    }

    /**
     * Removes a key-value pair from the map for the current thread.
     * @param key the key to be removed
     * @return the value associated with the key, or null if the key is not found
     */
    public static synchronized <K, V> V remove(K key) {
        return key != null ? ((ConcurrentHashMap<K, V>) map.get()).remove(key) : null;
    }

    /**
     * Retrieves the value associated with a key from the map for the current thread.
     * @param key the key to be retrieved
     * @return the value associated with the key, or null if the key is not found
     */
    public static synchronized <K, V> V get(K key) {
        return key != null ? ((ConcurrentHashMap<K, V>) map.get()).get(key) : null;
    }

    /**
     * Retrieves an unmodifiable set of keys in the map for the current thread.
     * @return an unmodifiable set of keys in the map
     */
    public static synchronized <K, V> Set<K> items() {
        return Collections.unmodifiableSet(((ConcurrentHashMap<K, V>) map.get()).keySet());
    }

    /**
     * Clears the map for the current thread.
     */
    static synchronized void clear() {
        map.get().clear();
    }
}

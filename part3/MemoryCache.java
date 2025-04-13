import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {
    public Map<String, String> cache;
    private final int capacity;
    private final Deque<String> keysOrder;

    // Constructor to initialize the MemoryCache
    public MemoryCache(int capacity) {
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>(capacity);
        this.keysOrder = new LinkedList<>();
    }

    // Update key order as elements are added to the cache
    private void updateKeyOrder(String key) {
        keysOrder.addLast(key);
        if (keysOrder.size() > capacity) {
            String oldestKey = keysOrder.removeFirst();
            cache.remove(oldestKey);
        }
    }

    // Return list of keys
    public String retrieveKeysOrder() {
        return String.join(",", keysOrder);
    }

    // Add/update an element in the MemoryCache
    public void insertValue(String key, String value) {
        updateKeyOrder(key);
        cache.put(key, value); // Updates value if key already exists
    }

    // Remove an element from the MemoryCache
    public void removeValue(String key) {
        cache.remove(key);
        keysOrder.remove(key);
    }

    // Get the value associated with a key in MemoryCache
    public String fetchValue(String key) {
        return cache.get(key);
    }
}

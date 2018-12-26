import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

class BoundedLinkedhashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxElements;

    BoundedLinkedhashMap(int maxElements) {
        this.maxElements = maxElements;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxElements;
    }
}

public class LruCache<K, V> {

    private final BoundedLinkedhashMap<K, V> map;

    public LruCache(int maxElements) {
        this.map = new BoundedLinkedhashMap<>(maxElements);
    }

    public Optional<V> get(K key) {
        V value;

        synchronized (map) {
            value = map.remove(key);
            if (value != null) {
                put(key, value);
            }
        }

        return Optional.ofNullable(value);
    }

    public V put(K key, V value) {
        synchronized (map) {
            map.put(key, value);
        }
        return value;
    }
}

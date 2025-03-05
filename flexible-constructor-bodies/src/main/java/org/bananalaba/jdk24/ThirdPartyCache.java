package org.bananalaba.jdk24;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThirdPartyCache {

    private final Map<String, Object> backend = new ConcurrentHashMap<>();

    public ThirdPartyCache(final String encryptionAlgorithm) {
        // some expensive initialisation logic
    }

    public void put(final String key, final Object value) {
        backend.put(key, value);
    }

    public Object get(final String key) {
        return backend.get(key);
    }

    public int size() {
        return backend.size();
    }

    public void clear() {
        backend.clear();
    }

}

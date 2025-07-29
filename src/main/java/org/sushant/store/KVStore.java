package org.sushant.store;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class KVStore {
    private ConcurrentHashMap<String, String> store;

    public KVStore() { this.store = new ConcurrentHashMap<>(); }

    public void set(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        String value = store.get(key);
        return Objects.requireNonNullElse(value, null);
    }

    public void delete(String key) {
        store.remove(key);
    }
}

package org.sushant.store;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public class KVStore implements Serializable {
    private ConcurrentHashMap<String, String> store;

    public KVStore() { this.store = new ConcurrentHashMap<>(); }

    private static KVStore instance;

    public static KVStore getInstance() {
        if(instance == null) {
            instance = new KVStore();
        }
        return instance;
    }

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

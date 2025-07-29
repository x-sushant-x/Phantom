package org.sushant.store;

import lombok.Data;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class KVStore {
    private ConcurrentHashMap<String, Object> store;

    public void set(String key, String value) {
        store.put(key, value);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = store.get(key);

        if(clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            throw new ClassCastException("Cannot cast " + value + " to " + clazz.getName());
        }
    }
}

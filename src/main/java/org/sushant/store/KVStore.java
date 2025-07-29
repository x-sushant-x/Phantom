package org.sushant.store;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class KVStore {
    private ConcurrentHashMap<String, Object> store;

    KVStore() {
        this.store = new ConcurrentHashMap<>();
    }

    public void set(String key, String value) {
        store.put(key, value);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = store.get(key);

        if(value == null) {
            return null;
        }

        if(clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            String msg = String.format("Cannot cast %s of type %s to %s", value, value.getClass(), clazz);
            throw new ClassCastException(msg);
        }
    }
}

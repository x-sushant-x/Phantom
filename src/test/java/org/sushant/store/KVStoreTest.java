package org.sushant.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KVStoreTest {

    @Test
    void testSetAndGetStringValue() {
        KVStore kvStore = new KVStore();
        kvStore.set("key1", "value1");

        String result = kvStore.get("key1", String.class);
        assertEquals("value1", result);
    }

    @Test
    void testGetWrongType() {
        KVStore kvStore = new KVStore();
        kvStore.set("key2", "123");

        assertThrows(ClassCastException.class, () -> {
            Integer num = kvStore.get("key2", Integer.class);
        });
    }

    @Test
    void testNonExistentKey() {
        KVStore kvStore = new KVStore();

        assertNull(kvStore.get("missingKey", String.class));
    }
}
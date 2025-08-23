package org.sushant.consistent_hashing;

import lombok.RequiredArgsConstructor;

import java.util.SortedMap;
import java.util.TreeMap;

@RequiredArgsConstructor
public class ConsistentHashing <T> {
    private final int vNodes;

    private final SortedMap<Integer, T> ring = new TreeMap<>();

    public void add(T node) {
        for(int i = 0; i < vNodes; i++) {
            int hash = MurmurHasher.hash(node.toString() + i);
            ring.put(hash, node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < vNodes; i++) {
            int hash = MurmurHasher.hash(node.toString() + i);
            ring.remove(hash);
        }
    }

    public T get(Object key) {
        if(ring.isEmpty()) {
            return null;
        }

        int hash = MurmurHasher.hash(key.toString());

        if(!ring.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = ring.tailMap(hash);

            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }

        return ring.get(hash);
    }
}

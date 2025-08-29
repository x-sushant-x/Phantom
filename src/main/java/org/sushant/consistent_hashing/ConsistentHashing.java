package org.sushant.consistent_hashing;

import lombok.RequiredArgsConstructor;

import java.util.*;

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

    public List<T> getNextN(Object key, int replicationFactor) {
        List<T> nodes = new ArrayList<>();

        if(ring.isEmpty() || replicationFactor == 0) return nodes;

        int hash = MurmurHasher.hash(key.toString());

        SortedMap<Integer, T> tailMap = ring.tailMap(hash);

        Iterator<T> it = tailMap.values().iterator();

        while (nodes.size() < replicationFactor && it.hasNext()) {
            T node = it.next();

            if(!nodes.contains(node)) {
                nodes.add(node);
            }
        }

        if(nodes.size() < replicationFactor) {
            for (T node : ring.values()) {
                if (nodes.size() >= replicationFactor) break;

                if (!nodes.contains(node)) {
                    nodes.add(node);
                }
            }
        }

        return nodes;
    }
}

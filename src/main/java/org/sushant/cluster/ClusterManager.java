package org.sushant.cluster;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.sushant.consistent_hashing.HashRing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor
public class ClusterManager {
    private final String selfID;
    private final Map<String, ClusterNode> nodes = new ConcurrentHashMap<>();
    private final HashRing<ClusterNode> hashRing;

    public void addNode(ClusterNode newNode) {
        if(!selfID.equals(newNode.getId())) {
            hashRing.add(newNode);
            nodes.put(newNode.getId(), newNode);
        }
    }

    public void removeNode(String nodeId) {
        ClusterNode nodeToRemove = nodes.get(nodeId);
        hashRing.remove(nodeToRemove);
        nodes.remove(nodeId);
    }

    public List<ClusterNode> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }
}
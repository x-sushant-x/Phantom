package org.sushant.cluster;

import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class ClusterManager {
    private final String selfID;
    private final Map<String, ClusterNode> nodes = new ConcurrentHashMap<>();

    public void addNode(ClusterNode newNode) {
        if(!selfID.equals(newNode.getId())) {
            nodes.put(newNode.getId(), newNode);
        }
    }

    public void removeNode(String nodeId) {
        nodes.remove(nodeId);
    }

    public List<ClusterNode> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }
}
package org.sushant.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sushant.cluster.ClusterManager;
import org.sushant.cluster.ClusterNode;
import org.sushant.consistent_hashing.HashRing;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class KVService {
    private final ClusterManager clusterManager;
    private final HashRing<ClusterNode> hashRing;
    private final int replicationFactor = 2;
    private final KVStore localStore;


    public void set(String key, String value) {
        List<ClusterNode> nodes = hashRing.getNextN(key, replicationFactor);

        for (ClusterNode node : nodes) {
            try {
                if (node.getId().equals(clusterManager.getSelfID())) {
                    localStore.set(key, value);
                } else {
                    log.info("SET command forwarded to: {} on port: {}", node.getHost(), node.getPort());
                    node.set(key, value);
                }
            } catch (Exception e) {
                node.setStatus(ClusterNode.Status.DOWN);
                e.printStackTrace();
            }
        }
    }

    public String get(String key) {
        List<ClusterNode> nodes = hashRing.getNextN(key, replicationFactor);

        for (ClusterNode node : nodes) {
            try {
                if (node.getId().equals(clusterManager.getSelfID())) {
                    return localStore.get(key);
                } else {
                    String value = node.get(key);
                    log.info("Retrieved ");
                    if (value != null) return value;
                }
            } catch (IOException e) {
                node.setStatus(ClusterNode.Status.DOWN);
            }
        }
        return null;
    }
}

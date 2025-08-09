package org.sushant;

import lombok.extern.slf4j.Slf4j;
import org.sushant.cluster.ClusterJoiner;
import org.sushant.cluster.ClusterManager;
import org.sushant.server.ClusterServer;
import org.sushant.server.TCPServer;
import org.sushant.store.KVStore;
import org.sushant.store.SnapshotManager;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ClusterManager clusterManager = new ClusterManager("abc");
        ClusterServer clusterServer = new ClusterServer(3000, clusterManager);
        clusterServer.start();

        ClusterJoiner clusterJoiner = new ClusterJoiner();

        SnapshotManager snapshotManager = new SnapshotManager("store.snapshot", null);
        snapshotManager.startSnapshotScheduler();

        KVStore store = snapshotManager.loadSnapshot();

        TCPServer server = new TCPServer(6349, store);

        try {
            clusterJoiner.joinCluster();
            server.startServer();
        } catch (IOException e) {
            log.error("Unable to start server: {}", e.getMessage());
        }
    }
}
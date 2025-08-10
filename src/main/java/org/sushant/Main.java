package org.sushant;

import lombok.extern.slf4j.Slf4j;
import org.sushant.cluster.ClusterJoiner;
import org.sushant.cluster.ClusterManager;
import org.sushant.monitoring.HeartBeatTask;
import org.sushant.server.ClusterServer;
import org.sushant.server.TCPServer;
import org.sushant.store.KVStore;
import org.sushant.store.SnapshotManager;
import org.sushant.utils.ConfigLoader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        String config = args.length > 0 ? args[0] : "application.properties";

        log.info("ConfigLoader: Using config: {}", config);
        ConfigLoader.load(config);

        // Cluster manager holds cluster state and node list
        ClusterManager clusterManager = new ClusterManager("abc");

        // Start cluster communication server (handles inter-node messages)
        ClusterServer clusterServer = new ClusterServer(3000, clusterManager);
        clusterServer.start();

        // Initialize cluster joiner and heartbeat monitor
        ClusterJoiner clusterJoiner = new ClusterJoiner();
        HeartBeatTask heartBeatTask = new HeartBeatTask(clusterManager);

        // Snapshot manager for persisting KV store state
        SnapshotManager snapshotManager = new SnapshotManager("store.snapshot", null);
        snapshotManager.startSnapshotScheduler(); // Schedule periodic snapshots

        // Load persisted store or start fresh
        KVStore store = snapshotManager.loadSnapshot();

        // TCP server to handle client requests
        TCPServer server = new TCPServer(6349, store);

        try {
            joinCluster(clusterJoiner);

            // Starts TCP server in separate thread so that we don't block application.
            startTCPServerThread(server);

            // Start periodic heartbeat monitoring
            scheduleHeartBeatMonitoring(heartBeatTask);

        } catch (Exception e) {
            log.error("Error starting services: {}", e.getMessage(), e);
        }
    }

    private static void startTCPServerThread(TCPServer server) {
        new Thread(() -> {
            try {
                server.startServer();
            } catch (IOException e) {
                log.error("Unable to start server: {}", e.getMessage());
            }
        }, "TCP-Server-Thread").start();
    }

    private static void scheduleHeartBeatMonitoring(HeartBeatTask heartBeatTask) {
        int rate = Integer.parseInt(ConfigLoader.get("monitoring.heartbeat.rate"));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(heartBeatTask, rate, rate, TimeUnit.SECONDS);
    }

    private static void joinCluster(ClusterJoiner clusterJoiner) {
        int rate = Integer.parseInt(ConfigLoader.get("cluster.join.interval"));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(clusterJoiner::joinCluster, rate, rate, TimeUnit.SECONDS);

    }
}
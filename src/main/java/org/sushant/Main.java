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
    public static void main(String[] args) {
        ClusterManager clusterManager = new ClusterManager("abc");
        ClusterServer clusterServer = new ClusterServer(3000, clusterManager);
        clusterServer.start();

        ClusterJoiner clusterJoiner = new ClusterJoiner();
        HeartBeatTask heartBeatTask = new HeartBeatTask(clusterManager);

        SnapshotManager snapshotManager = new SnapshotManager("store.snapshot", null);
        snapshotManager.startSnapshotScheduler();

        KVStore store = snapshotManager.loadSnapshot();

        TCPServer server = new TCPServer(6349, store);

        try {
            clusterJoiner.joinCluster();

            new Thread(() -> {
                try {
                    server.startServer();
                } catch (IOException e) {
                    log.error("Unable to start server: {}", e.getMessage());
                }
            }, "TCP-Server-Thread").start();

            scheduleHeartBeatMonitoring(heartBeatTask);

        } catch (Exception e) {
            log.error("Error starting services: {}", e.getMessage(), e);
        }
    }

    private static void scheduleHeartBeatMonitoring(HeartBeatTask heartBeatTask) {
        int rate = Integer.parseInt(ConfigLoader.get("monitoring.heartbeat.rate"));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(heartBeatTask, rate, rate, TimeUnit.SECONDS);
    }

}
package org.sushant.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.sushant.cluster.ClusterManager;
import org.sushant.cluster.ClusterNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Slf4j
public class HeartBeatTask implements Runnable {
    private final ClusterManager clusterManager;

    public HeartBeatTask(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public void run() {
        log.info("Starting heartbeat monitoring at: {}", LocalDateTime.now());

        for (ClusterNode node : clusterManager.getAllNodes()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://" + node.getHost() + ":" + node.getPort() + "/ping"))
                        .GET()
                        .build();

                HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                node.setStatus(ClusterNode.Status.UP);
            } catch (Exception e) {
                node.setStatus(ClusterNode.Status.DOWN);
                System.err.println("Node down: " + node.getId());
            }
        }
    }
}

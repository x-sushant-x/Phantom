package org.sushant.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Header;
import lombok.extern.slf4j.Slf4j;
import org.sushant.utils.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
public class ClusterJoiner {

    public void joinCluster() {
        String nodesStr = ConfigLoader.get("cluster.nodes");

        String[] nodesIPs = nodesStr.split(",");

        for (String endpoint : nodesIPs) {
            try {
                String id = ConfigLoader.get("self.id");
                String host = ConfigLoader.get("self.host");
                int port = Integer.parseInt(ConfigLoader.get("self.port"));

                ClusterNode node = new ClusterNode(id, host, port);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint + "/join"))
                        .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(node)))
                        .header(Header.CONTENT_TYPE, "application/json")
                        .build();

                HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                log.info("Cluster join response: {} from node: {}", resp.body(), endpoint);
            } catch (Exception e) {
                log.error("Failed to join cluster via: {}", endpoint);
            }
        }
    }
}

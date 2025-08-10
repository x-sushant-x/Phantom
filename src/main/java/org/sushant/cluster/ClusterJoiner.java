package org.sushant.cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Header;
import lombok.extern.slf4j.Slf4j;
import org.sushant.utils.ConfigLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ClusterJoiner {

    private final Map<String, Boolean> joinStatus = new HashMap<>();

    public void joinCluster() {
        String nodesStr = ConfigLoader.get("cluster.nodes");

        String[] nodesHosts = nodesStr.split(",");

        for (String endpoint : nodesHosts) {
            try {
                if (Boolean.TRUE.equals(joinStatus.get(endpoint))) {
                    continue; // already joined successfully before
                }

                ClusterNode node = buildClusterNode();

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = buildHTTPRequest(endpoint, node);

                HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    log.info("Cluster join response: {} from node: {}", resp.body(), endpoint);

                    markJoin(endpoint, true);
                } else {
                    log.warn("Cluster join failed with status {} from node: {}", resp.statusCode(), endpoint);
                    markJoin(endpoint, false);
                }

            } catch (java.net.ConnectException e) {
                log.error("Join Cluster Error. Connection refused to node: {}", endpoint);
                markJoin(endpoint, false);


            } catch (java.net.SocketTimeoutException e) {
                log.error("Join Cluster Error. Connection timed out to node: {}", endpoint);
                markJoin(endpoint, false);


            } catch (java.net.UnknownHostException e) {
                log.error("Join Cluster Error. Unknown host: {}", endpoint);
                markJoin(endpoint, false);

            } catch (IOException e) {
                log.error("Join Cluster Error. I/O error while connecting to node: {}", endpoint);
                markJoin(endpoint, false);


            } catch (Exception e) {
                log.error("Join Cluster Error. Unexpected error while joining cluster via: {}", endpoint);
                markJoin(endpoint, false);
            }
        }
    }

    private HttpRequest buildHTTPRequest(String endpoint, ClusterNode node) throws Exception {

        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/join"))
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(node)))
                .header(Header.CONTENT_TYPE, "application/json")
                .build();
    }

    private ClusterNode buildClusterNode() {
        String id = ConfigLoader.get("self.id");
        String host = ConfigLoader.get("self.host");
        int port = Integer.parseInt(ConfigLoader.get("self.port"));

        return new ClusterNode(id, host, port);
    }

    private void markJoin(String endpoint, boolean success) {
        joinStatus.put(endpoint, success);
    }
}

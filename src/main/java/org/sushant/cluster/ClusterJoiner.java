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


@Slf4j
public class ClusterJoiner {

    public void joinCluster() {
        String nodesStr = ConfigLoader.get("cluster.nodes");

        String[] nodesIPs = nodesStr.split(",");

        for (String endpoint : nodesIPs) {
            try {
                ClusterNode node = buildClusterNode();

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = buildHTTPRequest(endpoint, node);

                HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                    log.info("Cluster join response: {} from node: {}", resp.body(), endpoint);
                } else {
                    log.warn("Cluster join failed with status {} from node: {}", resp.statusCode(), endpoint);
                }

            } catch (java.net.ConnectException e) {
                log.error("Join Cluster Error. Connection refused to node: {}", endpoint);
            } catch (java.net.SocketTimeoutException e) {
                log.error("Join Cluster Error. Connection timed out to node: {}", endpoint);
            } catch (java.net.UnknownHostException e) {
                log.error("Join Cluster Error. Unknown host: {}", endpoint);
            } catch (IOException e) {
                log.error("Join Cluster Error. I/O error while connecting to node: {}", endpoint);
            } catch (Exception e) {
                log.error("Join Cluster Error. Unexpected error while joining cluster via: {}", endpoint);
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
}

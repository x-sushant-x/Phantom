package org.sushant.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.sushant.cluster.ClusterManager;
import org.sushant.cluster.ClusterNode;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ClusterServer {
    private final Integer PORT;
    private final ClusterManager clusterManager;

    public void start() {
        new Thread(() -> {
            log.info("Starting cluster server on port: {}", PORT);

            Javalin.create()
                    .post("/join", new HandleJoinCluster())
                    .start(PORT);
        }).start();
    }

    private class HandleJoinCluster implements Handler {

        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            String req = ctx.body();

            Map<String, String> response = new HashMap<>();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                var newNode = objectMapper.readValue(req, ClusterNode.class);

                clusterManager.addNode(newNode);

                buildSuccessResponse(response);

                ctx.json(response).status(HttpStatus.OK);
            } catch (Exception e) {
                buildErrorResponse(response);
                ctx.json(response).status(HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void buildSuccessResponse(Map<String, String> response) {
        response.put("status", "success");
        response.put("message", "Node joined successfully");
    }

    private void buildErrorResponse(Map<String, String> response) {
        response.put("status", "fail");
        response.put("message", "Invalid request body.");
    }

}

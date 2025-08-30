package org.sushant.cluster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.sushant.utils.PersistentTCPClient;

import java.io.IOException;

@Slf4j
@Data
public class ClusterNode implements TCPWorker {
    @JsonProperty("id")
    private final String id;

    @JsonProperty("host")
    private final String host;

    @JsonProperty("port")
    private final int port;

    @JsonIgnore
    private Status status;

    @JsonIgnore
    private PersistentTCPClient tcpClient;

    @JsonCreator
    public ClusterNode(
            @JsonProperty("id") String id,
            @JsonProperty("host") String host,
            @JsonProperty("port") int port
    ) {
        this.id = id;
        this.host = host;
        this.port = port;

        try {
            this.tcpClient = new PersistentTCPClient(host, port);
            log.info("PersistentTCPClient created for node: {} on port: {}", host, port);
        } catch (Exception e) {
            log.error("unable to created Persistent TCP Connection to node: {} on port: {}", host, port);
            this.status = Status.DOWN;
        }
    }

    public enum Status {
        UP, DOWN
    }

    @Override
    public void set(String key, String value) throws IOException {
        tcpClient.set(key, value);
    }

    @Override
    public String get(String key) throws IOException {
        return tcpClient.get(key);
    }
}

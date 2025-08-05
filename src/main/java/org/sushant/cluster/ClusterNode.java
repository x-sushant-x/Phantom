package org.sushant.cluster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class ClusterNode {
    private final String id;
    private final String host;
    private final int port;

    @JsonCreator
    public ClusterNode(
            @JsonProperty("id") String id,
            @JsonProperty("host") String host,
            @JsonProperty("port") int port
    ) {
        this.id = id;
        this.host = host;
        this.port = port;
    }
}

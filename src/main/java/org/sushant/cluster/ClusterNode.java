package org.sushant.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ClusterNode {
    private final String id;
    private final String host;
    private final int port;
    private NodeStatus status;

    public enum NodeStatus { UP, DOWN }
}

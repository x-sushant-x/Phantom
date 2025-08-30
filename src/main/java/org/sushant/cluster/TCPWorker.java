package org.sushant.cluster;

import java.io.IOException;

public interface TCPWorker {
    void set(String key, String value) throws IOException;

    String get(String key) throws IOException;
}

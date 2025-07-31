package org.sushant;

import lombok.extern.slf4j.Slf4j;
import org.sushant.server.TCPServer;
import org.sushant.store.KVStore;
import org.sushant.store.SnapshotManager;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) {
        SnapshotManager snapshotManager = new SnapshotManager("store.snapshot", null);
        snapshotManager.startSnapshotScheduler();

        KVStore store = snapshotManager.loadSnapshot();

        TCPServer server = new TCPServer(6349, store);

        try {
            server.startServer();
        } catch (IOException e) {
            log.error("Unable to start server: {}", e.getMessage());
        }
    }
}
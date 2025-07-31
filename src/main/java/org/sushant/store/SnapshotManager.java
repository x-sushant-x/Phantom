package org.sushant.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
@AllArgsConstructor
public class SnapshotManager {
    private String SNAPSHOT_FILE;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private KVStore store;

    private synchronized void saveSnapshot() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SNAPSHOT_FILE))) {
            out.writeObject(store);
            log.info("Snapshot saved.");
        } catch (Exception e) {
            log.error("Failed to save snapshot: ", e);
        }
    }

    public KVStore loadSnapshot() {
        File file = new File(SNAPSHOT_FILE);

        if(!file.exists()) {
            log.info("No snapshots found. Initializing new store");
            return new KVStore();
        }

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            store = (KVStore) in.readObject();
            log.info("Snapshot loaded from disk.");
        } catch (Exception e) {
            log.error("Failed to load snapshot. Starting fresh. Error: {}", e.getMessage());
        }

        return store;
    }

    public void startSnapshotScheduler() {
        executor.scheduleAtFixedRate(this::saveSnapshot, 1, 1, TimeUnit.MINUTES);
        log.info("Snapshot scheduler started. Interval: 1 minute.");
    }
}

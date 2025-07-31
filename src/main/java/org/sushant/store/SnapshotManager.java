package org.sushant.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
@AllArgsConstructor
public class SnapshotManager {
    private String SNAPSHOT_FILE;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private KVStore kvStore;

    private synchronized void saveSnapshot() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SNAPSHOT_FILE))) {
            out.writeObject(kvStore);
            log.info("Snapshot saved.");
        } catch (Exception e) {
            log.error("Failed to save snapshot: ", e);
        }
    }

    public void startSnapshotScheduler() {
        executor.scheduleAtFixedRate(this::saveSnapshot, 0, 1, TimeUnit.MINUTES);
        log.info("Snapshot scheduler started. Interval: 1 minute.");
    }
}

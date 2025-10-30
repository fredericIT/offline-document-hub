import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeSyncManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static WatchService watchService;
    private static boolean isMonitoring = false;
    private static final Map<WatchKey, Path> keys = new HashMap<>();
    private static final List<FileChangeListener> listeners = new ArrayList<>();

    public interface FileChangeListener {
        void onFileCreated(Path file);
        void onFileModified(Path file);
        void onFileDeleted(Path file);
    }

    public static void startFileMonitoring(String directoryPath) {
        if (isMonitoring) return;

        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(directoryPath);

            WatchKey key = path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            keys.put(key, path);
            isMonitoring = true;

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    WatchKey watchKey = watchService.take();
                    Path dir = keys.get(watchKey);

                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path name = (Path) event.context();
                        Path child = dir.resolve(name);

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            notifyFileCreated(child);
                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            notifyFileModified(child);
                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            notifyFileDeleted(child);
                        }
                    }
                    watchKey.reset();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, 0, 2, TimeUnit.SECONDS);

            System.out.println("File monitoring started for: " + directoryPath);
        } catch (IOException e) {
            System.err.println("Failed to start file monitoring: " + e.getMessage());
        }
    }

    public static void addFileChangeListener(FileChangeListener listener) {
        listeners.add(listener);
    }

    private static void notifyFileCreated(Path file) {
        for (FileChangeListener listener : listeners) {
            listener.onFileCreated(file);
        }
    }

    private static void notifyFileModified(Path file) {
        for (FileChangeListener listener : listeners) {
            listener.onFileModified(file);
        }
    }

    private static void notifyFileDeleted(Path file) {
        for (FileChangeListener listener : listeners) {
            listener.onFileDeleted(file);
        }
    }

    public static void startAutoSync() {
        scheduler.scheduleAtFixedRate(() -> {
            if (CloudSyncManager.isInternetAvailable() && UserStore.isAdmin(UserStore.getCurrentUser())) {
                // Auto-sync modified files
                syncModifiedFiles();
            }
        }, 5, 300, TimeUnit.SECONDS); // Check every 5 minutes
    }

    private static void syncModifiedFiles() {
        // Implementation to sync recently modified files to cloud
        System.out.println("Auto-sync: Checking for modified files...");
    }

    public static void stopMonitoring() {
        isMonitoring = false;
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping file monitoring: " + e.getMessage());
        }
        scheduler.shutdown();
    }
}
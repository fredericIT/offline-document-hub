package com.example.offlinedocumenthubserver;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;

import static com.example.offlinedocumenthubserver.RESTServer.googleDriveService;
import static com.example.offlinedocumenthubserver.RESTServer.sendAdminNotification;
import static com.example.offlinedocumenthubserver.RESTServer.storeBackupHistory;

public class ServerAutoBackupService {
    // Reduced from 4 hours to 1 minute for testing
    private static final long BACKUP_INTERVAL_MINUTES = 500;
    private static final long BACKUP_INTERVAL_MS = BACKUP_INTERVAL_MINUTES * 60 * 1000;
    private static final long GOOGLE_DRIVE_INIT_WAIT_MS = 30000; // Wait up to 30 seconds for init
    private static LocalDateTime lastBackupTime = null;
    private static Timer autoBackupTimer;
    private static boolean isRunning = false;
    private static boolean isGoogleDriveInitialized = false;

    public static void startAutoBackupService() {
        if (isRunning) {
            return;
        }

        System.out.println("🔄 Starting server-side auto backup service...");
        System.out.println("⏰ Backup interval: " + BACKUP_INTERVAL_MINUTES + " minute(s)");
        isRunning = true;

        // Wait for Google Drive initialization before starting the timer
        waitForGoogleDriveInitialization();
    }

    private static void waitForGoogleDriveInitialization() {
        System.out.println("⏳ Waiting for Google Drive service to initialize...");

        Timer initTimer = new Timer(true);
        initTimer.schedule(new TimerTask() {
            private int attempts = 0;
            private final int maxAttempts = 30; // 30 attempts * 1 second = 30 seconds max

            @Override
            public void run() {
                attempts++;

                // Check if Google Drive is ready via RESTServer
                if (RESTServer.googleDriveService != null && RESTServer.googleDriveService.isInitialized()) {
                    isGoogleDriveInitialized = true;
                    initTimer.cancel();
                    System.out.println("✅ Google Drive service is now ready!");
                    startBackupTimer();
                    return;
                }

                System.out.println("⏳ Waiting for Google Drive... Attempt " + attempts + "/" + maxAttempts);

                if (attempts >= maxAttempts) {
                    initTimer.cancel();
                    System.err.println("❌ Google Drive initialization timeout after " + maxAttempts + " seconds");
                    System.err.println("❌ Auto-backup service cannot start without Google Drive");
                    isRunning = false;
                }
            }
        }, 0, 1000); // Check every second
    }

    private static void startBackupTimer() {
        autoBackupTimer = new Timer(true);
        // Check every 30 seconds for backup conditions
        autoBackupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndTriggerAutoBackup();
            }
        }, 0, 30 * 1000); // Check every 30 seconds

        System.out.println("✅ Auto-backup timer started successfully!");
    }

    private static void checkAndTriggerAutoBackup() {
        try {
            System.out.println("🔍 [AUTO-BACKUP] Checking conditions at " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            // Check 1: Google Drive service ready
            if (googleDriveService == null || !googleDriveService.isInitialized()) {
                System.out.println("❌ [AUTO-BACKUP] Google Drive not ready - skipping");
                return;
            }

            // Check 2: Internet connection available
            if (!isInternetAvailable()) {
                System.out.println("🌐 [AUTO-BACKUP] No internet - skipping");
                return;
            }

            // Check 3: Backup not already running
            if (googleDriveService.isBackupRunning()) {
                System.out.println("⏳ [AUTO-BACKUP] Backup already in progress - skipping");
                return;
            }

            // Check 4: Enough time passed since last backup
            if (lastBackupTime != null) {
                long timeSinceLastBackup = Duration.between(lastBackupTime, LocalDateTime.now()).toMillis();
                if (timeSinceLastBackup < BACKUP_INTERVAL_MS) {
                    long secondsLeft = (BACKUP_INTERVAL_MS - timeSinceLastBackup) / 1000;
                    System.out.println("⏰ [AUTO-BACKUP] Too soon - " + secondsLeft + " seconds remaining");
                    return;
                }
            }

            // All conditions met - trigger auto-backup
            System.out.println("✅ [AUTO-BACKUP] All conditions met - triggering backup");
            triggerServerAutoBackup();

        } catch (Exception e) {
            System.err.println("❌ [AUTO-BACKUP] Check error: " + e.getMessage());
        }
    }

    private static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode == 200);
        } catch (Exception e) {
            return false;
        }
    }

    private static void triggerServerAutoBackup() {
        try {
            System.out.println("🚀 [AUTO-BACKUP] Starting automatic backup...");

            // Generate backup folder name
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String backupFolderName = "DocumentHub_Backup_" + timestamp;

            // Store initial backup history
            storeBackupHistory("auto", "System", backupFolderName, "in_progress", null, 0, 0);

            // Send start notification
            sendAdminNotification("Auto-backup Started",
                    "Automatic backup process started at " +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                            "\nBackup folder: " + backupFolderName);

            // Use the existing GoogleDriveService instance directly
            Map<String, Object> backupResult = googleDriveService.performBackup("auto", "System");

            if (Boolean.TRUE.equals(backupResult.get("success"))) {
                lastBackupTime = LocalDateTime.now();

                // Update backup history with success
                storeBackupHistory("auto", "System", backupFolderName, "success", null, 0, 0);

                System.out.println("✅ [AUTO-BACKUP] Completed successfully at " + lastBackupTime);

                // Send success notification
                sendAdminNotification("Auto-backup Completed",
                        "Automatic backup completed successfully!\n" +
                                "Completed at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                                "Backup folder: " + backupResult.get("folderName") + "\n" +
                                "All data has been securely backed up to Google Drive.");

            } else {
                String errorMessage = (String) backupResult.get("message");
                System.err.println("❌ [AUTO-BACKUP] Failed: " + errorMessage);

                // Update backup history with failure
                storeBackupHistory("auto", "System", backupFolderName, "failed", errorMessage, 0, 0);

                // Send failure notification
                sendAdminNotification("Auto-backup Failed",
                        "Automatic backup failed!\n" +
                                "Attempted at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                                "Error: " + errorMessage + "\n" +
                                "Please check the server logs.");
            }

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            System.err.println("❌ [AUTO-BACKUP] Error: " + errorMessage);

            // Store error in backup history
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String backupFolderName = "DocumentHub_Backup_" + timestamp;
            storeBackupHistory("auto", "System", backupFolderName, "failed", errorMessage, 0, 0);

            // Send error notification
            sendAdminNotification("Auto-backup Error",
                    "Automatic backup encountered an error!\n" +
                            "Attempted at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                            "Error: " + errorMessage + "\n" +
                            "Please check the server connectivity.");
        }
    }

    public static void stopAutoBackupService() {
        if (autoBackupTimer != null) {
            autoBackupTimer.cancel();
            autoBackupTimer = null;
        }
        isRunning = false;
        System.out.println("🛑 Server auto backup service stopped");
    }

    public static boolean isRunning() {
        return isRunning;
    }

    // Method to get last backup time for debugging
    public static LocalDateTime getLastBackupTime() {
        return lastBackupTime;
    }
}
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Enable hardware acceleration
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");

        // Initialize database and services
        initializeSystem();

        SwingUtilities.invokeLater(() -> {
            new LandingPage().show();
        });
    }

    private static void initializeSystem() {
        try {
            // Initialize database connection
            DatabaseManager.initialize();
            System.out.println("Database initialized successfully");

            // Initialize cloud sync manager
            CloudSyncManager.initialize();
            System.out.println("Cloud sync manager initialized");

            // Start LAN communication service
            LANCommunicationManager.startLANService();
            System.out.println("LAN communication service started");

            // Add shutdown hook for cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LANCommunicationManager.stopLANService();
                System.out.println("System shutdown complete");
            }));

        } catch (Exception e) {
            System.err.println("System initialization failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "System initialization failed. Please check database connection.",
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Enable hardware acceleration
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");

        SwingUtilities.invokeLater(() -> {
            new LandingPage().show();
        });
    }
}
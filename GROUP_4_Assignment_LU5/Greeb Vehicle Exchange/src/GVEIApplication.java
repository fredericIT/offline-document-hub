// GVEIApplication.java
public class GVEIApplication {
    public static void main(String[] args) {
        // Set system properties for better font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Show main menu instead of login form directly
        MainMenu mainMenu = new MainMenu();
        mainMenu.setVisible(true);
    }
}
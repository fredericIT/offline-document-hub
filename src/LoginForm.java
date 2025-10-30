import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public void show() {
        frame = new JFrame("Offline Document Hub - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIComponents.DARK_GREY);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(UIComponents.CARD_BG);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.setPreferredSize(new Dimension(300, 200));

        JLabel titleLabel = new JLabel("🔑 Login to Document Hub");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIComponents.LIGHT_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderTextField userField = UIComponents.createTextField("Username", 15);
        userField.setMaximumSize(new Dimension(250, 35));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderPasswordField passField = UIComponents.createPasswordField("Password", 15);
        passField.setMaximumSize(new Dimension(250, 35));
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIComponents.CARD_BG);
        JButton loginButton = UIComponents.styledButton("Login");
        JButton backButton = UIComponents.styledButton("Back");

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(userField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(buttonPanel);

        mainPanel.add(loginPanel);
        frame.setContentPane(mainPanel);

        // Event handlers
        backButton.addActionListener(e -> {
            frame.dispose();
            new LandingPage().show();
        });

        loginButton.addActionListener(e -> {
            String username = userField.getRealText();
            String password = passField.getRealPassword();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both username and password",
                        "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (UserStore.validateUser(username, password)) {
                // Set the current user
                UserStore.setCurrentUser(username);

                JOptionPane.showMessageDialog(frame, "Login successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new Dashboard(username).show();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}
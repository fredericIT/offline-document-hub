import javax.swing.*;
import java.awt.*;

public class LoginForm {

    public void show() {
        final JFrame frame = new JFrame("Login - Offline Document Hub");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(460, 380);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UIComponents.DARK_GREY);

        JPanel card = new JPanel();
        card.setBackground(UIComponents.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 280));

        JLabel heading = new JLabel("🔑 Login");
        heading.setForeground(UIComponents.LIGHT_TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Enter your username and password", SwingConstants.CENTER);
        sub.setForeground(Color.LIGHT_GRAY);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderTextField userField = UIComponents.createTextField("Username", 20);
        userField.setMaximumSize(new Dimension(320, 36));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderPasswordField passField = UIComponents.createPasswordField("Password", 20);
        passField.setMaximumSize(new Dimension(320, 36));
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel();
        btnRow.setBackground(UIComponents.CARD_BG);
        JButton back = UIComponents.styledButton("Back");
        JButton login = UIComponents.styledButton("Login");
        btnRow.add(back);
        btnRow.add(login);

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(userField);
        card.add(Box.createVerticalStrut(12));
        card.add(passField);
        card.add(Box.createVerticalStrut(18));
        card.add(btnRow);

        root.add(card);
        frame.setContentPane(root);

        // Actions
        back.addActionListener(e -> {
            frame.dispose();
            new LandingPage().show();
        });

        login.addActionListener(e -> {
            String username = userField.getRealText();
            String password = passField.getRealPassword();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter username and password", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (UserStore.validateUser(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login successful — welcome " + username + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new Dashboard(username).show();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}


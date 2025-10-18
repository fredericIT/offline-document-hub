import javax.swing.*;
import java.awt.*;

public class SignupForm {

    public void show() {
        final JFrame frame = new JFrame("Create Account - Offline Document Hub");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 420);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UIComponents.DARK_GREY);

        JPanel card = new JPanel();
        card.setBackground(UIComponents.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 320));

        JLabel heading = new JLabel("📝 Create Account");
        heading.setForeground(UIComponents.LIGHT_TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Pick a username and password", SwingConstants.CENTER);
        sub.setForeground(Color.LIGHT_GRAY);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderTextField userField = UIComponents.createTextField("Username", 20);
        userField.setMaximumSize(new Dimension(320, 36));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderPasswordField passField = UIComponents.createPasswordField("Password", 20);
        passField.setMaximumSize(new Dimension(320, 36));
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        UIComponents.PlaceholderPasswordField confirmField = UIComponents.createPasswordField("Confirm Password", 20);
        confirmField.setMaximumSize(new Dimension(320, 36));
        confirmField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel();
        btnRow.setBackground(UIComponents.CARD_BG);
        JButton back = UIComponents.styledButton("Back");
        JButton signup = UIComponents.styledButton("Sign Up");
        btnRow.add(back);
        btnRow.add(signup);

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(userField);
        card.add(Box.createVerticalStrut(12));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(18));
        card.add(btnRow);

        root.add(card);
        frame.setContentPane(root);

        back.addActionListener(e -> {
            frame.dispose();
            new LandingPage().show();
        });

        signup.addActionListener(e -> {
            String username = userField.getRealText();
            String password = passField.getRealPassword();
            String confirm = confirmField.getRealPassword();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please complete all fields", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match", "Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean added = UserStore.addUser(username, password);
            if (!added) {
                JOptionPane.showMessageDialog(frame, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(frame, "Account created — you may now log in", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new LoginForm().show();
        });

        frame.setVisible(true);
    }
}

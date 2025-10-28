import javax.swing.*;
import java.awt.*;

public class SignupForm {

    public void show() {
        final JFrame frame = new JFrame("Create account - DriveClone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 550);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JButton backBtn = new JButton("←");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        backBtn.setFocusPainted(false);

        JLabel title = new JLabel("DriveClone");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UIComponents.PRIMARY_BLUE);

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        // Signup Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel formTitle = new JLabel("Create your account");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        formTitle.setForeground(UIComponents.DARK_TEXT);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formSubtitle = new JLabel("Continue to DriveClone");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formSubtitle.setForeground(UIComponents.LIGHT_TEXT);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = UIComponents.createDriveTextField("Choose a username", 20);
        usernameField.setMaximumSize(new Dimension(400, 45));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = UIComponents.createDrivePasswordField("Create a password", 20);
        passwordField.setMaximumSize(new Dimension(400, 45));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField confirmField = UIComponents.createDrivePasswordField("Confirm your password", 20);
        confirmField.setMaximumSize(new Dimension(400, 45));
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton signupBtn = UIComponents.createPrimaryButton("Create account");
        signupBtn.setMaximumSize(new Dimension(400, 45));
        signupBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel altAuth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        altAuth.setBackground(Color.WHITE);
        JLabel loginLink = new JLabel("<html><u>Sign in</u></html>");
        loginLink.setForeground(UIComponents.PRIMARY_BLUE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        altAuth.add(new JLabel("Already have an account?"));
        altAuth.add(loginLink);

        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(formSubtitle);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(new JLabel("Username"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(new JLabel("Password"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(new JLabel("Confirm Password"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(confirmField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(signupBtn);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(altAuth);

        root.add(header, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);

        frame.setContentPane(root);

        // Action listeners
        backBtn.addActionListener(e -> {
            frame.dispose();
            new LandingPage().show();
        });

        signupBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || username.equals("Choose a username") ||
                    password.isEmpty() || password.equals("Create a password") ||
                    confirm.isEmpty() || confirm.equals("Confirm your password")) {
                JOptionPane.showMessageDialog(frame, "Please complete all fields", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean added = UserStore.addUser(username, password);
            if (!added) {
                JOptionPane.showMessageDialog(frame, "Username already taken. Please choose another.", "Username Unavailable", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(frame, "Account created successfully! You can now sign in.", "Account Created", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new LoginForm().show();
        });

        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new LoginForm().show();
            }
        });

        frame.setVisible(true);
    }
}
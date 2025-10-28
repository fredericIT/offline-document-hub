import javax.swing.*;
import java.awt.*;

public class LandingPage {

    public void show() {
        final JFrame frame = new JFrame("Google Drive Clone - Offline Document Hub");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel logo = new JLabel("🚀 DriveClone");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(new Color(66, 133, 244));

        JPanel authButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        authButtons.setBackground(Color.WHITE);
        JButton loginBtn = createFlatButton("Sign in");
        JButton signupBtn = createPrimaryButton("Create account");

        authButtons.add(loginBtn);
        authButtons.add(Box.createHorizontalStrut(10));
        authButtons.add(signupBtn);

        header.add(logo, BorderLayout.WEST);
        header.add(authButtons, BorderLayout.EAST);

        // Hero Section
        JPanel hero = new JPanel(new GridBagLayout());
        hero.setBackground(Color.WHITE);
        hero.setBorder(BorderFactory.createEmptyBorder(60, 30, 60, 30));

        JLabel heroIcon = new JLabel("📁");
        heroIcon.setFont(new Font("Segoe UI", Font.PLAIN, 80));

        JLabel heroTitle = new JLabel("Secure. Private. Offline-First.");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        heroTitle.setForeground(new Color(32, 33, 36));

        JLabel heroSubtitle = new JLabel("<html><div style='text-align:center;color:#5f6368;font-size:18px;max-width:600px;'>" +
                "Store, share, and collaborate on files and folders from your mobile device, tablet, or computer</div></html>");
        heroSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JButton getStarted = createPrimaryButton("Get started");
        getStarted.setFont(new Font("Segoe UI", Font.BOLD, 16));
        getStarted.setPreferredSize(new Dimension(180, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        hero.add(heroIcon, gbc);
        hero.add(Box.createVerticalStrut(20), gbc);
        hero.add(heroTitle, gbc);
        hero.add(Box.createVerticalStrut(15), gbc);
        hero.add(heroSubtitle, gbc);
        hero.add(Box.createVerticalStrut(30), gbc);
        hero.add(getStarted, gbc);

        // Features
        JPanel features = new JPanel(new GridLayout(1, 3, 20, 0));
        features.setBackground(Color.WHITE);
        features.setBorder(BorderFactory.createEmptyBorder(40, 60, 60, 60));

        features.add(createFeaturePanel("🔒", "Secure Storage", "Your files are protected with enterprise-grade security"));
        features.add(createFeaturePanel("⚡", "Fast Access", "Access your files quickly, even without internet"));
        features.add(createFeaturePanel("👥", "Easy Sharing", "Share files securely with your team"));

        root.add(header, BorderLayout.NORTH);
        root.add(hero, BorderLayout.CENTER);
        root.add(features, BorderLayout.SOUTH);

        frame.setContentPane(root);

        // Action listeners
        loginBtn.addActionListener(e -> {
            frame.dispose();
            new LoginForm().show();
        });

        signupBtn.addActionListener(e -> {
            frame.dispose();
            new SignupForm().show();
        });

        getStarted.addActionListener(e -> {
            frame.dispose();
            new SignupForm().show();
        });

        frame.setVisible(true);
    }

    private JButton createFlatButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(new Color(66, 133, 244));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(66, 133, 244));
        button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createFeaturePanel(String icon, String title, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(32, 33, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align:center;color:#5f6368;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descLabel);

        return panel;
    }
}
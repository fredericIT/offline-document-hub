import javax.swing.*;
import java.awt.*;

// --- Helper UI class (added to remove errors) ---
class UIComponents {
    public static final Color DARK_GREEN = new Color(0, 100, 0);
    public static final Color DARK_GREY = new Color(40, 40, 40);
    public static final Color LIGHT_TEXT = Color.WHITE;
    public static final Color CARD_BG = new Color(50, 50, 50);

    public static JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(DARK_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }
}

// --- Dummy LoginForm class (added to remove errors) ---
class LoginForm {
    public void show() {
        JFrame frame = new JFrame("Login");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel("Login Form Placeholder", SwingConstants.CENTER));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// --- Dummy SignupForm class (added to remove errors) ---
class SignupForm {
    public void show() {
        JFrame frame = new JFrame("Signup");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel("Signup Form Placeholder", SwingConstants.CENTER));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// --- Main Landing Page ---
public class LandingPage {

    public void show() {
        final JFrame frame = new JFrame("Offline Document Hub");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 560);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));

        // Left (brand panel)
        JPanel left = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, UIComponents.DARK_GREEN, 0, h, UIComponents.DARK_GREY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        left.setLayout(new BorderLayout());
        left.setOpaque(false);

        JLabel logo = new JLabel("📂", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 120));
        logo.setForeground(UIComponents.LIGHT_TEXT);

        JLabel brand = new JLabel("Offline Document Hub", SwingConstants.CENTER);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brand.setForeground(UIComponents.LIGHT_TEXT);
        brand.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));

        left.add(logo, BorderLayout.CENTER);
        left.add(brand, BorderLayout.SOUTH);

        // Right (card panel)
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIComponents.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(380, 320));

        JLabel title = new JLabel("Welcome to Document Hub", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UIComponents.LIGHT_TEXT);

        JLabel subtitle = new JLabel(
                "<html><div style='text-align:center;color:#D3D3D3'>Work offline, sync when available.<br>Secure local storage on your LAN.</div></html>",
                SwingConstants.CENTER);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 18, 0));

        JButton loginBtn = UIComponents.styledButton("🔑 Login");
        JButton signupBtn = UIComponents.styledButton("📝 Create account");

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            frame.dispose();
            new LoginForm().show();
        });

        signupBtn.addActionListener(e -> {
            frame.dispose();
            new SignupForm().show();
        });

        card.add(title);
        card.add(subtitle);
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(signupBtn);

        right.add(card);

        root.add(left);
        root.add(right);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // Run directly for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LandingPage().show());
    }
}

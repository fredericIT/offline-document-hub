import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class PremiumLoginSystem extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn, createAccountBtn, forgotPasswordBtn;
    private JCheckBox rememberMe;
    private Timer fadeTimer;
    private float alpha = 0.0f;

    public PremiumLoginSystem() {
        initializeUI();
        startAnimations();
    }

    private void initializeUI() {
        setTitle("Premium Document Hub - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1400, 900, 40, 40));

        // Main panel with elegant dark background
        JPanel mainPanel = new ElegantBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        // Close button
        JButton closeBtn = new CloseButton();
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(closeBtn);

        // Center content
        JPanel centerPanel = createLoginPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        // Logo/Title
        JLabel titleLabel = new JLabel("DOCUMENT HUB PRO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enterprise Document Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login container
        JPanel loginContainer = createLoginForm();

        panel.add(Box.createVerticalStrut(40));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(50));
        panel.add(loginContainer);

        return panel;
    }

    private JPanel createLoginForm() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(400, 500));

        // Glass effect panel
        JPanel glassPanel = new GlassPanel();
        glassPanel.setLayout(new BoxLayout(glassPanel, BoxLayout.Y_AXIS));
        glassPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel signInLabel = new JLabel("Sign in to your account");
        signInLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        signInLabel.setForeground(new Color(180, 180, 180));
        signInLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Username field
        JLabel userLabel = new JLabel("EMAIL OR USERNAME");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(new Color(200, 200, 200));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new ModernTextField();
        usernameField.setMaximumSize(new Dimension(320, 45));

        // Password field
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(200, 200, 200));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new ModernPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 45));

        // Remember me and forgot password
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setMaximumSize(new Dimension(320, 30));

        rememberMe = new ModernCheckBox("Remember me");
        forgotPasswordBtn = new ModernLinkButton("Forgot password?");

        optionsPanel.add(rememberMe, BorderLayout.WEST);
        optionsPanel.add(forgotPasswordBtn, BorderLayout.EAST);

        // Login button
        loginBtn = new GradientButton("SIGN IN TO YOUR ACCOUNT");
        loginBtn.setMaximumSize(new Dimension(320, 50));

        // Create account
        JPanel createPanel = new JPanel();
        createPanel.setOpaque(false);
        JLabel createLabel = new JLabel("Don't have an account?");
        createLabel.setForeground(new Color(180, 180, 180));
        createAccountBtn = new ModernLinkButton("Create one now");

        createPanel.add(createLabel);
        createPanel.add(createAccountBtn);

        // Add components to form
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(optionsPanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createPanel);

        // Add to glass panel
        glassPanel.add(welcomeLabel);
        glassPanel.add(Box.createVerticalStrut(5));
        glassPanel.add(signInLabel);
        glassPanel.add(formPanel);

        container.add(glassPanel);
        return container;
    }

    private void startAnimations() {
        // Fade in animation
        fadeTimer = new Timer(30, e -> {
            alpha += 0.05f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop();
            }
            setOpacity(alpha);
        });
        fadeTimer.start();
    }

    // Custom Components

    class ElegantBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Elegant dark blue gradient
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 32, 65),
                    getWidth(), getHeight(), new Color(30, 58, 110)
            );

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Subtle geometric pattern
            g2d.setColor(new Color(255, 255, 255, 8));
            int size = 80;
            for (int i = 0; i < getWidth(); i += size) {
                for (int j = 0; j < getHeight(); j += size) {
                    if ((i + j) % (size * 2) == 0) {
                        g2d.fillRect(i, j, size / 4, size / 4);
                    }
                }
            }
        }
    }

    class GlassPanel extends JPanel {
        public GlassPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Create glass effect
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Semi-transparent background
            g2d.setColor(new Color(255, 255, 255, 25));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            // Subtle border
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
        }
    }

    class ModernTextField extends JTextField {
        public ModernTextField() {
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            setFont(new Font("Segoe UI", Font.PLAIN, 16));

            // Add placeholder text
            setText("Enter your username");
            setForeground(new Color(180, 180, 180));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals("Enter your username")) {
                        setText("");
                        setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText("Enter your username");
                        setForeground(new Color(180, 180, 180));
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // Border
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);

            super.paintComponent(g);
        }
    }

    class ModernPasswordField extends JPasswordField {
        public ModernPasswordField() {
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            setFont(new Font("Segoe UI", Font.PLAIN, 16));

            // Add placeholder text
            setEchoChar((char)0);
            setText("Enter your password");
            setForeground(new Color(180, 180, 180));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (String.valueOf(getPassword()).equals("Enter your password")) {
                        setText("");
                        setEchoChar('•');
                        setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        setEchoChar((char)0);
                        setText("Enter your password");
                        setForeground(new Color(180, 180, 180));
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // Border
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);

            super.paintComponent(g);
        }
    }

    class GradientButton extends JButton {
        private boolean hover = false;

        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            setFocusPainted(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });

            addActionListener(e -> performLogin());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Elegant blue gradient
            GradientPaint gradient;
            if (hover) {
                gradient = new GradientPaint(0, 0, new Color(41, 128, 185), getWidth(), getHeight(), new Color(52, 152, 219));
            } else {
                gradient = new GradientPaint(0, 0, new Color(30, 58, 110), getWidth(), getHeight(), new Color(41, 128, 185));
            }

            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            super.paintComponent(g);
        }
    }

    class ModernCheckBox extends JCheckBox {
        public ModernCheckBox(String text) {
            super(text);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setFocusPainted(false);
            setIcon(new ModernCheckBoxIcon());
        }
    }

    class ModernCheckBoxIcon implements javax.swing.Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JCheckBox cb = (JCheckBox) c;

            // Box
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillRoundRect(x, y, 16, 16, 4, 4);

            // Border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, 16, 16, 4, 4);

            // Check mark
            if (cb.isSelected()) {
                g2d.setColor(new Color(52, 152, 219));
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x+3, y+8, x+6, y+11);
                g2d.drawLine(x+6, y+11, x+13, y+4);
            }
        }

        @Override
        public int getIconWidth() { return 20; }
        @Override
        public int getIconHeight() { return 20; }
    }

    class ModernLinkButton extends JButton {
        public ModernLinkButton(String text) {
            super(text);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(new Color(173, 216, 230));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(new Color(135, 206, 250));
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(new Color(173, 216, 230));
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
            });

            addActionListener(e -> {
                JOptionPane.showMessageDialog(PremiumLoginSystem.this,
                        "Password reset instructions have been sent to your email.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    class CloseButton extends JButton {
        public CloseButton() {
            setText("✕");
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(40, 40));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(Color.RED);
                    setBackground(new Color(255, 0, 0, 50));
                    setOpaque(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(Color.WHITE);
                    setOpaque(false);
                }
            });

            addActionListener(e -> System.exit(0));
        }
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Handle placeholder text
        if (username.equals("Enter your username") || password.equals("Enter your password")) {
            showError("Please enter both username and password");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Simulate login process
        loginBtn.setText("SIGNING IN...");
        loginBtn.setEnabled(false);

        Timer timer = new Timer(2000, e -> {
            if (username.equals("admin") && password.equals("admin123")) {
                showSuccess("Login successful! Welcome to Document Hub Pro");
                // Here you would open the main application
            } else {
                showError("Invalid credentials. Try: admin / admin123");
                loginBtn.setText("SIGN IN TO YOUR ACCOUNT");
                loginBtn.setEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new PremiumLoginSystem().setVisible(true);
        });
    }
}
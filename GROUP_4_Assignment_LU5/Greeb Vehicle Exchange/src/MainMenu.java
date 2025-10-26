// MainMenu.java
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends Frame {
    private Font titleFont, buttonFont;

    public MainMenu() {
        initializeUI();
        loadResources();
    }

    private void initializeUI() {
        setTitle("Green Vehicle Exchange Initiative - Rwanda");
        setSize(1000, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        // Set background color
        setBackground(new Color(34, 139, 34)); // Forest green

        // Create main panel with custom painting
        Panel mainPanel = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                // Draw gradient background
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(34, 139, 34),
                        getWidth(), getHeight(), new Color(152, 251, 152)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw title
                g.setColor(Color.WHITE);
                g.setFont(titleFont);
                String title = "Green Vehicle Exchange Initiative";
                FontMetrics fm = g.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g.drawString(title, (getWidth() - titleWidth) / 2, 150);

                // Draw subtitle
                g.setFont(new Font("Arial", Font.ITALIC, 18));
                String subtitle = "Government of Rwanda - Promoting Sustainable Transportation";
                int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
                g.drawString(subtitle, (getWidth() - subtitleWidth) / 2, 190);

                // Draw version info
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString("Version 1.0", getWidth() - 100, getHeight() - 20);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Create buttons with better styling
        Button citizenBtn = createStyledButton("Citizen Login", new Color(70, 130, 180));
        Button adminBtn = createStyledButton("Admin Login", new Color(46, 139, 87));
        Button exitBtn = createStyledButton("Exit", new Color(178, 34, 34));

        // Button panel - REMOVED setOpaque() as it's not needed in AWT
        Panel buttonPanel = new Panel(new GridLayout(3, 1, 20, 20));
        // AWT Panels don't have setOpaque() - they're always opaque
        buttonPanel.add(citizenBtn);
        buttonPanel.add(adminBtn);
        buttonPanel.add(exitBtn);

        // Add constraints for centering
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Footer with additional info
        Panel footerPanel = new Panel(new FlowLayout());
        footerPanel.setBackground(new Color(34, 139, 34));
        Label footerLabel = new Label("© 2024 Government of Rwanda - Green Vehicle Exchange Initiative");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);

        // Event handlers
        citizenBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginForm("citizen");
            }
        });

        adminBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginForm("admin");
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    private Button createStyledButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(buttonFont);
        button.setPreferredSize(new Dimension(200, 50));

        // REMOVED: setFocusPainted() - AWT buttons don't have this method
        // In AWT, focus painting is automatic and cannot be disabled

        // Add hover effect using MouseAdapter
        button.addMouseListener(new MouseAdapter() {
            private Color originalColor = bgColor;

            public void mouseEntered(MouseEvent e) {
                originalColor = button.getBackground();
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
        });

        return button;
    }

    private void loadResources() {
        try {
            // Create fonts
            titleFont = new Font("Arial", Font.BOLD, 36);
            buttonFont = new Font("Arial", Font.BOLD, 16);
        } catch (Exception e) {
            // Fallback to basic fonts
            titleFont = new Font("Dialog", Font.BOLD, 36);
            buttonFont = new Font("Dialog", Font.BOLD, 16);
        }
    }

    private void showLoginForm(String userType) {
        this.setVisible(false);
        LoginForm loginForm = new LoginForm();

        // Pre-fill admin email if admin button was clicked
        if ("admin".equals(userType)) {
            // You can set a hint or pre-fill the email field if needed
        }
        loginForm.setVisible(true);
    }

    // Main method for direct testing
    public static void main(String[] args) {
        MainMenu menu = new MainMenu();
        menu.setVisible(true);
    }
}
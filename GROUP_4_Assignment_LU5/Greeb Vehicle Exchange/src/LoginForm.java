// Enhanced LoginForm.java
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends Frame {
    private TextField emailField;
    private TextField passwordField;
    private Button loginButton, registerButton, backButton;
    private Label messageLabel, titleLabel;
    private UserDAO userDAO;

    public LoginForm() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GVEI - Login");
        setSize(500, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(new Color(240, 245, 240));

        // Title Panel
        Panel titlePanel = new Panel();
        titlePanel.setBackground(new Color(34, 139, 34));
        titlePanel.setLayout(new FlowLayout());
        titleLabel = new Label("GVEI Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 245, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createStyledLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        emailField = createStyledTextField();
        mainPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createStyledLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = createStyledTextField();
        passwordField.setEchoChar('*');
        mainPanel.add(passwordField, gbc);

        // Message label
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        messageLabel = new Label("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignment(Label.CENTER);
        mainPanel.add(messageLabel, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 245, 240));

        loginButton = createStyledButton("Login", new Color(34, 139, 34));
        registerButton = createStyledButton("Register", new Color(70, 130, 180));
        backButton = createStyledButton("Back", new Color(178, 34, 34));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        Panel footerPanel = new Panel();
        footerPanel.setBackground(new Color(34, 139, 34));
        footerPanel.add(new Label("Government of Rwanda - Green Vehicle Initiative"));
        add(footerPanel, BorderLayout.SOUTH);

        // Event handlers
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegistrationForm());
        backButton.addActionListener(e -> showMainMenu());

        // Enter key support
        emailField.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                showMainMenu();
            }
        });
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(34, 139, 34));
        return label;
    }

    private TextField createStyledTextField() {
        TextField field = new TextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        return field;
    }

    private Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 35));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            return;
        }

        User user = userDAO.login(email, password);
        if (user != null) {
            messageLabel.setText("Login successful!");
            this.setVisible(false);

            if ("admin".equals(user.getRole())) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new CitizenDashboard(user).setVisible(true);
            }
        } else {
            messageLabel.setText("Invalid email or password");
        }
    }

    private void showRegistrationForm() {
        new RegistrationForm().setVisible(true);
    }

    private void showMainMenu() {
        this.setVisible(false);
        new MainMenu().setVisible(true);
    }
}
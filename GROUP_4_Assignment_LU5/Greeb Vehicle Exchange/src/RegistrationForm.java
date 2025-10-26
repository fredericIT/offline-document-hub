// Enhanced RegistrationForm.java
import java.awt.*;
import java.awt.event.*;

public class RegistrationForm extends Frame {
    private TextField nameField, emailField, passwordField, confirmPasswordField;
    private Button registerButton, cancelButton;
    private Label messageLabel, titleLabel;
    private UserDAO userDAO;

    public RegistrationForm() {
        userDAO = new UserDAO();
        initializeUI();
        applyRegistrationFormStyling();
    }

    private void initializeUI() {
        setTitle("GVEI - User Registration");
        setSize(500, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(StyleUtils.BACKGROUND_COLOR);

        // Title Panel
        Panel titlePanel = StyleUtils.createHeaderPanel("Create New Account");
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(StyleUtils.BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createFormLabel("Full Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        nameField = StyleUtils.createTextField(20);
        mainPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createFormLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        emailField = StyleUtils.createTextField(20);
        mainPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(createFormLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        passwordField = StyleUtils.createTextField(20);
        passwordField.setEchoChar('*');
        mainPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(createFormLabel("Confirm Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        confirmPasswordField = StyleUtils.createTextField(20);
        confirmPasswordField.setEchoChar('*');
        mainPanel.add(confirmPasswordField, gbc);

        // Message label
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        messageLabel = new Label("");
        messageLabel.setForeground(StyleUtils.DANGER_COLOR);
        messageLabel.setAlignment(Label.CENTER);
        messageLabel.setFont(StyleUtils.BODY_FONT);
        mainPanel.add(messageLabel, gbc);

        // Buttons
        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(StyleUtils.BACKGROUND_COLOR);

        registerButton = StyleUtils.createButton("Register", StyleUtils.ACCENT_COLOR);
        cancelButton = StyleUtils.createButton("Cancel", StyleUtils.SECONDARY_COLOR);

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        add(StyleUtils.createFooterPanel(), BorderLayout.SOUTH);

        // Event handlers - FIXED: Using anonymous classes instead of lambda
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });

        // Enter key support for form submission
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        emailField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        confirmPasswordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                goBack();
            }
        });
    }

    private void applyRegistrationFormStyling() {
        setBackground(StyleUtils.BACKGROUND_COLOR);

        // Style all text fields
        if (nameField != null) {
            nameField.setFont(StyleUtils.BODY_FONT);
            nameField.setBackground(StyleUtils.CARD_COLOR);
        }
        if (emailField != null) {
            emailField.setFont(StyleUtils.BODY_FONT);
            emailField.setBackground(StyleUtils.CARD_COLOR);
        }
        if (passwordField != null) {
            passwordField.setFont(StyleUtils.BODY_FONT);
            passwordField.setBackground(StyleUtils.CARD_COLOR);
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.setFont(StyleUtils.BODY_FONT);
            confirmPasswordField.setBackground(StyleUtils.CARD_COLOR);
        }

        // Style message label
        if (messageLabel != null) {
            messageLabel.setFont(StyleUtils.BODY_FONT);
        }
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(StyleUtils.SUBHEADER_FONT);
        label.setForeground(StyleUtils.PRIMARY_COLOR);
        label.setAlignment(Label.RIGHT);
        return label;
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            messageLabel.setForeground(StyleUtils.DANGER_COLOR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            messageLabel.setForeground(StyleUtils.DANGER_COLOR);
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters");
            messageLabel.setForeground(StyleUtils.DANGER_COLOR);
            return;
        }

        // Basic email validation
        if (!isValidEmail(email)) {
            messageLabel.setText("Please enter a valid email address");
            messageLabel.setForeground(StyleUtils.DANGER_COLOR);
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("citizen");

        if (userDAO.registerUser(user)) {
            messageLabel.setText("Registration successful! Please login.");
            messageLabel.setForeground(StyleUtils.ACCENT_COLOR);

            // Auto-clear after successful registration
            clearFormFields();
        } else {
            messageLabel.setText("Registration failed. Email may already exist.");
            messageLabel.setForeground(StyleUtils.DANGER_COLOR);
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    private void clearFormFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void goBack() {
        this.setVisible(false);
        new LoginForm().setVisible(true);
    }
}
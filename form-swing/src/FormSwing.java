import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormSwing extends JFrame {

    // Declare fields
    private JTextField nameField, emailField;
    private JComboBox<String> roleBox;
    private JTextArea commentsArea;

    public FormSwing() {
        // Frame setup
        setTitle("Sample Swing Form");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Components
        JLabel nameLabel = new JLabel("Full Name:");
        nameField = new JTextField();

        JLabel emailLabel = new JLabel("Email Address:");
        emailField = new JTextField();

        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"Student", "Staff", "Admin"};
        roleBox = new JComboBox<>(roles);

        JLabel commentsLabel = new JLabel("Comments:");
        commentsArea = new JTextArea(4, 20);
        JScrollPane scrollPane = new JScrollPane(commentsArea);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitListener());

        // Layout setup
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to layout
        gbc.gridx = 0; gbc.gridy = 0; add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(roleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(roleBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(commentsLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(scrollPane, gbc);

        gbc.gridx = 1; gbc.gridy = 4; add(submitButton, gbc);

        setVisible(true);
    }

    // Button click listener
    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleBox.getSelectedItem();
            String comments = commentsArea.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(FormSwing.this,
                        "Please fill in all required fields!",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(FormSwing.this,
                        "Form Submitted Successfully!\n\n" +
                                "Name: " + name + "\n" +
                                "Email: " + email + "\n" +
                                "Role: " + role + "\n" +
                                "Comments: " + comments,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormSwing::new);
    }
}

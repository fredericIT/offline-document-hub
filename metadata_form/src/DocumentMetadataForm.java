import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class DocumentMetadataForm extends JFrame implements ActionListener {

    // Declare form components
    private JTextField titleField, authorField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryBox;
    private JLabel fileLabel, messageLabel;
    private JButton uploadButton, saveButton;
    private File selectedFile;

    public DocumentMetadataForm() {
        // Frame setup
        setTitle("📄 Document Metadata Form");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title label
        JLabel header = new JLabel("Document Metadata Form", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(30, 60, 120));
        add(header, BorderLayout.NORTH);

        // Center form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField(20);
        formPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(4, 20);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryBox = new JComboBox<>(new String[]{"--Select--", "Research", "Report", "Manual", "Others"});
        formPanel.add(categoryBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("File Upload:"), gbc);
        gbc.gridx = 1;
        uploadButton = new JButton("Choose File");
        uploadButton.addActionListener(this);
        formPanel.add(uploadButton, gbc);

        gbc.gridy = 5;
        fileLabel = new JLabel("No file selected");
        fileLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(fileLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Bottom panel with Save button and message
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        saveButton = new JButton("Save Document");
        saveButton.addActionListener(this);
        bottomPanel.add(saveButton);

        messageLabel = new JLabel("", JLabel.CENTER);
        bottomPanel.add(messageLabel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileLabel.setText(selectedFile.getName());
            }
        } else if (e.getSource() == saveButton) {
            saveDocument();
        }
    }

    private void saveDocument() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = (String) categoryBox.getSelectedItem();

        if (title.isEmpty() || author.isEmpty() || description.isEmpty() ||
                category.equals("--Select--") || selectedFile == null) {
            messageLabel.setText("⚠️ Please fill all fields and select a file!");
            messageLabel.setForeground(Color.RED);
        } else {
            messageLabel.setText("✅ Document metadata saved successfully!");
            messageLabel.setForeground(new Color(0, 128, 0));

            // Reset form after success
            titleField.setText("");
            authorField.setText("");
            descriptionArea.setText("");
            categoryBox.setSelectedIndex(0);
            selectedFile = null;
            fileLabel.setText("No file selected");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DocumentMetadataForm().setVisible(true));
    }
}


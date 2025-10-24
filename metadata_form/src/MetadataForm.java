import javax.swing.*;
import java.awt.*;

public class MetadataForm extends JFrame {
    public MetadataForm() {
        setTitle("Document Metadata Form");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();
        titleField.setToolTipText("Enter the document's title");

        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();
        authorField.setToolTipText("Enter the name of the author");

        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setToolTipText("Write a short summary of the document");

        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Click to save document metadata");

        add(titleLabel);
        add(titleField);
        add(authorLabel);
        add(authorField);
        add(descriptionLabel);
        add(descriptionArea);
        add(new JLabel()); // empty space
        add(saveButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MetadataForm();
    }
}


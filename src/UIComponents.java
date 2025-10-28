import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class UIComponents {
    public static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    public static final Color DARK_TEXT = new Color(32, 33, 36);
    public static final Color LIGHT_TEXT = new Color(95, 99, 104);
    public static final Color BORDER_COLOR = new Color(218, 220, 224);
    public static final Color HOVER_BG = new Color(248, 250, 255);

    // Google Drive styled button
    public static JButton createDriveButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(DARK_TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_BG);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_BLUE, 1),
                        new EmptyBorder(8, 16, 8, 16)
                ));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1),
                        new EmptyBorder(8, 16, 8, 16)
                ));
            }
        });

        return button;
    }

    // Primary action button
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(53, 122, 232));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_BLUE);
            }
        });

        return button;
    }

    // Google Drive styled text field
    public static JTextField createDriveTextField(String placeholder, int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);

        if (placeholder != null) {
            field.setText(placeholder);
            field.setForeground(LIGHT_TEXT);

            field.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(DARK_TEXT);
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(LIGHT_TEXT);
                    }
                }
            });
        }

        return field;
    }

    // Google Drive styled password field
    public static JPasswordField createDrivePasswordField(String placeholder, int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setEchoChar((char) 0); // Show placeholder text

        if (placeholder != null) {
            field.setText(placeholder);
            field.setForeground(LIGHT_TEXT);

            field.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (String.valueOf(field.getPassword()).equals(placeholder)) {
                        field.setText("");
                        field.setForeground(DARK_TEXT);
                        field.setEchoChar('•');
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (field.getPassword().length == 0) {
                        field.setText(placeholder);
                        field.setForeground(LIGHT_TEXT);
                        field.setEchoChar((char) 0);
                    }
                }
            });
        }

        return field;
    }

    // Card panel for content
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }
}
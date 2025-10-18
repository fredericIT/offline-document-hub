import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class UIComponents {
    public static final Color DARK_GREEN = new Color(34, 139, 34);
    public static final Color DARK_GREY = new Color(45, 45, 45);
    public static final Color CARD_BG = new Color(60, 60, 60);
    public static final Color LIGHT_TEXT = Color.WHITE;
    public static final Color PLACEHOLDER = Color.GRAY;

    // Styled button used across the app
    public static JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(DARK_GREEN);
        button.setForeground(LIGHT_TEXT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 128, 0));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(DARK_GREEN);
            }
        });

        return button;
    }

    // Create a placeholder text field
    public static PlaceholderTextField createTextField(String placeholder, int columns) {
        return new PlaceholderTextField(placeholder, columns);
    }

    // Create a placeholder password field
    public static PlaceholderPasswordField createPasswordField(String placeholder, int columns) {
        return new PlaceholderPasswordField(placeholder, columns);
    }

    // ========== PlaceholderTextField ==========
    public static class PlaceholderTextField extends JTextField {
        private final String placeholder;
        private boolean showingPlaceholder = true;

        public PlaceholderTextField(String placeholder, int columns) {
            super(placeholder, columns);
            this.placeholder = placeholder;
            init();
        }

        private void init() {
            setForeground(PLACEHOLDER);
            setBackground(CARD_BG);
            setCaretColor(LIGHT_TEXT);
            setBorder(new CompoundBorder(new LineBorder(DARK_GREEN, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(LIGHT_TEXT);
                        showingPlaceholder = false;
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(PLACEHOLDER);
                        showingPlaceholder = true;
                    }
                }
            });
        }

        // returns actual user input ("" if placeholder shown)
        public String getRealText() {
            return showingPlaceholder ? "" : getText();
        }
    }

    // ========== PlaceholderPasswordField ==========
    public static class PlaceholderPasswordField extends JPasswordField {
        private final String placeholder;
        private boolean showingPlaceholder = true;
        private char defaultEcho;

        public PlaceholderPasswordField(String placeholder, int columns) {
            super(placeholder, columns);
            this.placeholder = placeholder;
            init();
        }

        private void init() {
            // store default echo; if it's 0, fallback to '*'
            defaultEcho = getEchoChar();
            if (defaultEcho == 0) defaultEcho = '*';

            setForeground(PLACEHOLDER);
            setBackground(CARD_BG);
            setCaretColor(LIGHT_TEXT);
            setEchoChar((char) 0); // show placeholder text
            setBorder(new CompoundBorder(new LineBorder(DARK_GREEN, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(LIGHT_TEXT);
                        setEchoChar(defaultEcho);
                        showingPlaceholder = false;
                    }
                }

                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        setText(placeholder);
                        setForeground(PLACEHOLDER);
                        setEchoChar((char) 0);
                        showingPlaceholder = true;
                    }
                }
            });
        }

        // returns actual password as String ("" if placeholder shown)
        public String getRealPassword() {
            return showingPlaceholder ? "" : new String(getPassword());
        }
    }
}

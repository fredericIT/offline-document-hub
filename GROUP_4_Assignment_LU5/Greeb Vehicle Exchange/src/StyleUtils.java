// StyleUtils.java
import java.awt.*;

public class StyleUtils {

    // Color Scheme
    public static final Color PRIMARY_COLOR = new Color(34, 139, 34);      // Forest Green
    public static final Color SECONDARY_COLOR = new Color(70, 130, 180);   // Steel Blue
    public static final Color ACCENT_COLOR = new Color(46, 139, 87);       // Sea Green
    public static final Color DANGER_COLOR = new Color(178, 34, 34);       // Fire Brick
    public static final Color WARNING_COLOR = new Color(255, 165, 0);      // Orange
    public static final Color BACKGROUND_COLOR = new Color(240, 255, 240); // Honeydew
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(51, 51, 51);          // Dark Gray
    public static final Color TEXT_LIGHT = Color.WHITE;

    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    // Create styled button
    public static Button createButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setBackground(bgColor);
        button.setForeground(TEXT_LIGHT);
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(new Dimension(120, 35));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
        });

        return button;
    }

    // Create styled label
    public static Label createLabel(String text, Font font, Color color) {
        Label label = new Label(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // Create styled text field
    public static TextField createTextField(int columns) {
        TextField field = new TextField(columns);
        field.setFont(BODY_FONT);
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_COLOR);
        return field;
    }

    // Create styled text area
    public static TextArea createTextArea(int rows, int columns) {
        TextArea area = new TextArea(rows, columns);
        area.setFont(BODY_FONT);
        area.setBackground(CARD_COLOR);
        area.setForeground(TEXT_COLOR);
        area.setEditable(false);
        return area;
    }

    // Create header panel
    public static Panel createHeaderPanel(String title) {
        Panel header = new Panel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(PRIMARY_COLOR);
        Label titleLabel = createLabel(title, TITLE_FONT, TEXT_LIGHT);
        header.add(titleLabel);
        header.setPreferredSize(new Dimension(800, 60));
        return header;
    }

    // Create footer panel
    public static Panel createFooterPanel() {
        Panel footer = new Panel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(PRIMARY_COLOR);
        Label footerLabel = createLabel("Government of Rwanda - Green Vehicle Exchange Initiative",
                BODY_FONT, TEXT_LIGHT);
        footer.add(footerLabel);
        footer.setPreferredSize(new Dimension(800, 40));
        return footer;
    }

    // Create card panel (for table-like layouts)
    public static Panel createCardPanel() {
        Panel card = new Panel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setForeground(TEXT_COLOR);
        return card;
    }

    // Create table header
    public static Panel createTableHeader(String[] headers) {
        Panel headerPanel = new Panel(new GridLayout(1, headers.length));
        headerPanel.setBackground(SECONDARY_COLOR);

        for (String header : headers) {
            Label headerLabel = createLabel(header, SUBHEADER_FONT, TEXT_LIGHT);
            headerLabel.setAlignment(Label.CENTER);
            headerPanel.add(headerLabel);
        }

        return headerPanel;
    }
}
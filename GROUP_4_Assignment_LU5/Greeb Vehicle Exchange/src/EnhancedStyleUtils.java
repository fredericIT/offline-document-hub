// EnhancedStyleUtils.java
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class EnhancedStyleUtils {

    // Enhanced Color Scheme
    public static final Color PRIMARY_COLOR = new Color(34, 139, 34);      // Forest Green
    public static final Color SECONDARY_COLOR = new Color(70, 130, 180);   // Steel Blue
    public static final Color ACCENT_COLOR = new Color(46, 139, 87);       // Sea Green
    public static final Color DANGER_COLOR = new Color(178, 34, 34);       // Fire Brick
    public static final Color WARNING_COLOR = new Color(255, 165, 0);      // Orange
    public static final Color SUCCESS_COLOR = new Color(60, 179, 113);     // Medium Sea Green
    public static final Color BACKGROUND_COLOR = new Color(240, 255, 240); // Honeydew
    public static final Color TABLE_HEADER_COLOR = new Color(34, 139, 34);
    public static final Color TABLE_ROW_EVEN = new Color(240, 255, 240);
    public static final Color TABLE_ROW_ODD = new Color(255, 255, 255);
    public static final Color TEXT_COLOR = new Color(51, 51, 51);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color CARD_COLOR = Color.WHITE;

    // Enhanced Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font TABLE_HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    // Create styled table
    public static Panel createTablePanel(String[] headers, String[][] data) {
        Panel tablePanel = new Panel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);

        // Create table header
        Panel headerPanel = createTableHeader(headers);
        tablePanel.add(headerPanel, BorderLayout.NORTH);

        // Create table body with ScrollPane
        Panel bodyPanel = createTableBody(headers.length, data);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(bodyPanel);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    public static Panel createTableHeader(String[] headers) {
        Panel headerPanel = new Panel(new GridLayout(1, headers.length));
        headerPanel.setBackground(TABLE_HEADER_COLOR);

        for (String header : headers) {
            Label headerLabel = createLabel(header, TABLE_HEADER_FONT, TEXT_LIGHT);
            headerLabel.setAlignment(Label.CENTER);
            headerPanel.add(headerLabel);
        }

        return headerPanel;
    }

    public static Panel createTableBody(int columns, String[][] data) {
        Panel bodyPanel = new Panel(new GridLayout(0, columns));
        bodyPanel.setBackground(BACKGROUND_COLOR);

        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                Color rowColor = (i % 2 == 0) ? TABLE_ROW_EVEN : TABLE_ROW_ODD;
                for (int j = 0; j < columns; j++) {
                    String cellValue = (j < data[i].length) ? data[i][j] : "";
                    Label cellLabel = createLabel(cellValue, BODY_FONT, TEXT_COLOR);
                    cellLabel.setBackground(rowColor);
                    cellLabel.setAlignment(Label.CENTER);
                    bodyPanel.add(cellLabel);
                }
            }
        }

        return bodyPanel;
    }

    // Create search panel
    public static Panel createSearchPanel(String placeholder) {
        Panel searchPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);

        TextField searchField = createTextField(20);
        searchField.setText(placeholder);

        Button searchButton = createButton("Search", SECONDARY_COLOR);
        Button clearButton = createButton("Clear", DANGER_COLOR);

        searchPanel.add(createLabel("Search:", SUBHEADER_FONT, PRIMARY_COLOR));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        return searchPanel;
    }

    // Create filter panel
    public static Panel createFilterPanel(String[] filterOptions) {
        Panel filterPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BACKGROUND_COLOR);

        filterPanel.add(createLabel("Filter by:", SUBHEADER_FONT, PRIMARY_COLOR));

        Choice filterChoice = new Choice();
        filterChoice.add("All");
        for (String option : filterOptions) {
            filterChoice.add(option);
        }

        filterPanel.add(filterChoice);

        return filterPanel;
    }

    // Create search panel with components for external access
    public static SearchComponents createSearchComponents(String placeholder) {
        Panel searchPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);

        TextField searchField = createTextField(20);
        searchField.setText(placeholder);

        Button searchButton = createButton("Search", SECONDARY_COLOR);
        Button clearButton = createButton("Clear", DANGER_COLOR);

        searchPanel.add(createLabel("Search:", SUBHEADER_FONT, PRIMARY_COLOR));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        return new SearchComponents(searchPanel, searchField, searchButton, clearButton);
    }

    // Create filter panel with components for external access
    public static FilterComponents createFilterComponents(String[] filterOptions) {
        Panel filterPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BACKGROUND_COLOR);

        filterPanel.add(createLabel("Filter by:", SUBHEADER_FONT, PRIMARY_COLOR));

        Choice filterChoice = new Choice();
        filterChoice.add("All");
        for (String option : filterOptions) {
            filterChoice.add(option);
        }

        filterPanel.add(filterChoice);

        return new FilterComponents(filterPanel, filterChoice);
    }

    // Create statistics chart panel
    public static Panel createChartPanel(String title, Map<String, Integer> data) {
        Panel chartPanel = new Panel(new BorderLayout());
        chartPanel.setBackground(CARD_COLOR);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        // Title
        Label titleLabel = createLabel(title, HEADER_FONT, PRIMARY_COLOR);
        titleLabel.setAlignment(Label.CENTER);
        chartPanel.add(titleLabel, BorderLayout.NORTH);

        // Chart area
        Panel chartArea = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                drawBarChart(g, data, getWidth(), getHeight());
            }
        };
        chartArea.setBackground(CARD_COLOR);
        chartPanel.add(chartArea, BorderLayout.CENTER);

        return chartPanel;
    }

    private static void drawBarChart(Graphics g, Map<String, Integer> data, int width, int height) {
        if (data == null || data.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int barWidth = Math.max(30, (width - 100) / Math.max(1, data.size()));
        int maxValue = data.values().stream().max(Integer::compare).orElse(1);
        int chartHeight = height - 80;

        // Draw bars
        int x = 50;
        Color[] colors = {PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, WARNING_COLOR, SUCCESS_COLOR};
        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int barHeight = (int) ((double) entry.getValue() / maxValue * chartHeight);
            int y = height - 60 - barHeight;

            g2d.setColor(colors[colorIndex % colors.length]);
            g2d.fillRect(x, y, barWidth - 10, barHeight);

            g2d.setColor(TEXT_COLOR);
            // Draw label (truncate if too long)
            String label = entry.getKey();
            if (label.length() > 8) {
                label = label.substring(0, 8) + "..";
            }
            g2d.drawString(label, x, height - 40);
            g2d.drawString(String.valueOf(entry.getValue()), x, y - 5);

            x += barWidth;
            colorIndex++;
        }

        // Draw axes
        g2d.setColor(TEXT_COLOR);
        g2d.drawLine(40, height - 60, width - 20, height - 60); // X-axis
        g2d.drawLine(40, 20, 40, height - 60); // Y-axis
    }

    // Utility methods
    public static Button createButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setBackground(bgColor);
        button.setForeground(TEXT_LIGHT);
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(new Dimension(120, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public static Label createLabel(String text, Font font, Color color) {
        Label label = new Label(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static TextField createTextField(int columns) {
        TextField field = new TextField(columns);
        field.setFont(BODY_FONT);
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_COLOR);
        return field;
    }

    public static Panel createHeaderPanel(String title) {
        Panel header = new Panel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(PRIMARY_COLOR);
        Label titleLabel = createLabel(title, TITLE_FONT, TEXT_LIGHT);
        header.add(titleLabel);
        header.setPreferredSize(new Dimension(800, 60));
        return header;
    }

    // Container class for search components
    public static class SearchComponents {
        public Panel panel;
        public TextField searchField;
        public Button searchButton;
        public Button clearButton;

        public SearchComponents(Panel panel, TextField searchField, Button searchButton, Button clearButton) {
            this.panel = panel;
            this.searchField = searchField;
            this.searchButton = searchButton;
            this.clearButton = clearButton;
        }
    }

    // Container class for filter components
    public static class FilterComponents {
        public Panel panel;
        public Choice filterChoice;

        public FilterComponents(Panel panel, Choice filterChoice) {
            this.panel = panel;
            this.filterChoice = filterChoice;
        }
    }
}
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.File;

public class DriveDashboard {
    private final String user;
    private JFrame frame;
    private JPanel mainContent;
    private CardLayout cardLayout;

    public DriveDashboard(String username) {
        this.user = username;
    }

    public void show() {
        frame = new JFrame("DriveClone - " + user);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        // Sidebar and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createSidebar(), createMainContent());
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(1);
        splitPane.setBackground(Color.WHITE);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
        header.setPreferredSize(new Dimension(100, 60));

        // Left side - Logo and search
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftHeader.setBackground(Color.WHITE);

        JLabel logo = new JLabel("🚀 DriveClone");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(new Color(66, 133, 244));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(241, 243, 244));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchPanel.setPreferredSize(new Dimension(400, 40));

        JTextField searchField = new JTextField("Search in DriveClone");
        searchField.setBorder(null);
        searchField.setBackground(new Color(241, 243, 244));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchPanel.add(searchField, BorderLayout.CENTER);

        leftHeader.add(logo);
        leftHeader.add(searchPanel);

        // Right side - User info
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setBackground(Color.WHITE);

        JButton notifications = createIconButton("🔔", 35);
        JButton apps = createIconButton("⬛", 35);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        userPanel.setBackground(Color.WHITE);
        JLabel userInitial = new JLabel(String.valueOf(user.charAt(0)).toUpperCase());
        userInitial.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userInitial.setForeground(Color.WHITE);
        userInitial.setOpaque(true);
        userInitial.setBackground(new Color(66, 133, 244));
        userInitial.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        userInitial.setPreferredSize(new Dimension(35, 35));

        JLabel userName = new JLabel(user);
        userName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        userPanel.add(userInitial);
        userPanel.add(userName);

        rightHeader.add(notifications);
        rightHeader.add(apps);
        rightHeader.add(userPanel);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(250, 0));

        // New button
        JButton newBtn = UIComponents.createPrimaryButton("+ New");
        newBtn.setMaximumSize(new Dimension(220, 45));
        newBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Navigation items
        String[] navItems = {
                "📊 My Drive", "🔄 Recent", "⭐ Starred", "⏰ Recent", "🗑️ Trash", "💾 Storage"
        };

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        for (String item : navItems) {
            JButton navButton = createNavButton(item);
            navButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            navButton.setMaximumSize(new Dimension(220, 40));
            navPanel.add(navButton);
            navPanel.add(Box.createVerticalStrut(5));
        }

        // Storage info
        JPanel storagePanel = new JPanel();
        storagePanel.setLayout(new BoxLayout(storagePanel, BoxLayout.Y_AXIS));
        storagePanel.setBackground(new Color(248, 250, 255));
        storagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        storagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel storageTitle = new JLabel("Storage");
        storageTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        storageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar storageBar = new JProgressBar(0, 100);
        storageBar.setValue(65);
        storageBar.setForeground(new Color(66, 133, 244));
        storageBar.setBackground(new Color(232, 234, 237));
        storageBar.setPreferredSize(new Dimension(180, 4));
        storageBar.setMaximumSize(new Dimension(180, 4));

        JLabel storageText = new JLabel("6.5 GB of 10 GB used");
        storageText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        storageText.setForeground(UIComponents.LIGHT_TEXT);
        storageText.setAlignmentX(Component.LEFT_ALIGNMENT);

        storagePanel.add(storageTitle);
        storagePanel.add(Box.createVerticalStrut(10));
        storagePanel.add(storageBar);
        storagePanel.add(Box.createVerticalStrut(5));
        storagePanel.add(storageText);

        sidebar.add(newBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(navPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(storagePanel);

        return sidebar;
    }

    private JPanel createMainContent() {
        mainContent = new JPanel(new CardLayout());
        cardLayout = (CardLayout) mainContent.getLayout();
        mainContent.setBackground(Color.WHITE);

        // Add different views
        mainContent.add(createMyDriveView(), "myDrive");
        mainContent.add(createRecentView(), "recent");
        mainContent.add(createStarredView(), "starred");
        mainContent.add(createStorageView(), "storage");

        return mainContent;
    }

    private JPanel createMyDriveView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(Color.WHITE);

        String[] toolbarItems = {"My Drive", "Computers", "Shared with me", "Recent"};
        for (String item : toolbarItems) {
            JButton btn = new JButton(item);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            if (item.equals("My Drive")) {
                btn.setForeground(UIComponents.PRIMARY_BLUE);
                btn.setBackground(new Color(232, 240, 254));
            }
            toolbar.add(btn);
        }

        // Files grid view (Google Drive style)
        JPanel filesPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        filesPanel.setBackground(Color.WHITE);
        filesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Sample files
        String[][] files = {
                {"📁", "Projects", "12 items", "Today"},
                {"📁", "Personal", "8 items", "Yesterday"},
                {"📄", "Project Plan.docx", "2.4 MB", "Today"},
                {"📊", "Budget.xlsx", "1.8 MB", "2 days ago"},
                {"📑", "Meeting Notes.pdf", "3.1 MB", "3 days ago"},
                {"🖼️", "Screenshot.png", "4.2 MB", "Week ago"},
                {"🎬", "Presentation.mp4", "15.7 MB", "Week ago"},
                {"📝", "Readme.txt", "0.1 MB", "2 weeks ago"}
        };

        for (String[] file : files) {
            filesPanel.add(createFileCard(file[0], file[1], file[2], file[3]));
        }

        JScrollPane scrollPane = new JScrollPane(filesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFileCard(String icon, String name, String size, String date) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIComponents.BORDER_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("<html><div style='text-align:center;'>" + name + "</div></html>");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;color:#5f6368;font-size:11px;'>" + size + " • " + date + "</div></html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(infoLabel);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 250, 255));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JPanel createRecentView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Recent Files");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createStarredView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Starred Files");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createStorageView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Storage Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        return panel;
    }

    private JButton createIconButton(String icon, int size) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        button.setPreferredSize(new Dimension(size, size));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(248, 250, 255));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }
}
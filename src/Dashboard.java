import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class Dashboard {
    private final String user;
    private JPanel main;
    private JFrame frame;

    public Dashboard(String username) {
        this.user = username;
    }

    public void show() {
        frame = new JFrame("Document Hub Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIComponents.DARK_GREEN);
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = new JLabel("📂 Document Hub - Welcome " + user);
        title.setForeground(UIComponents.LIGHT_TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);
        frame.add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));
        sidebar.setBackground(UIComponents.DARK_GREY);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        JButton btnHome = new JButton("🏠 Home");
        JButton btnUpload = new JButton("📤 Upload");
        JButton btnView = new JButton("📑 View Documents");
        JButton btnSettings = new JButton("⚙ Settings");
        JButton btnAbout = new JButton("ℹ About");
        JButton btnLogout = new JButton("🚪 Logout");

        JButton[] sideButtons = {btnHome, btnUpload, btnView, btnSettings, btnAbout, btnLogout};
        for (JButton b : sideButtons) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            b.setForeground(UIComponents.LIGHT_TEXT);
            b.setBackground(new Color(60, 60, 60));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(UIComponents.DARK_GREEN); }
                public void mouseExited(MouseEvent e) { b.setBackground(new Color(60, 60, 60)); }
            });
            sidebar.add(b);
        }

        frame.add(sidebar, BorderLayout.WEST);

        // Main content with CardLayout
        main = new JPanel(new CardLayout());
        main.setBackground(Color.WHITE);

        // Home panel
        JPanel homePanel = createHomePanel();

        // Upload panel
        JPanel uploadPanel = createUploadPanel();

        // View panel (table mock)
        JPanel viewPanel = createViewPanel();

        // Settings panel
        JPanel settingsPanel = createSettingsPanel();

        // About panel
        JPanel aboutPanel = createAboutPanel();

        // Add to main
        main.add(homePanel, "home");
        main.add(uploadPanel, "upload");
        main.add(viewPanel, "view");
        main.add(settingsPanel, "settings");
        main.add(aboutPanel, "about");

        frame.add(main, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(30, 30, 30));
        JLabel foot = new JLabel("© 2025 Offline Document Hub | Five Gang Developers");
        foot.setForeground(Color.LIGHT_GRAY);
        footer.add(foot);
        frame.add(footer, BorderLayout.SOUTH);

        // Button actions to switch cards
        CardLayout cl = (CardLayout) (main.getLayout());
        btnHome.addActionListener(e -> cl.show(main, "home"));
        btnUpload.addActionListener(e -> cl.show(main, "upload"));
        btnView.addActionListener(e -> cl.show(main, "view"));
        btnSettings.addActionListener(e -> cl.show(main, "settings"));
        btnAbout.addActionListener(e -> cl.show(main, "about"));
        btnLogout.addActionListener(e -> {
            frame.dispose();
            new LandingPage().show();
        });

        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with welcome message
        JLabel welcome = new JLabel("<html><h1 style='color:#228B22;'>Welcome, " + user + "! 👋</h1>"
                + "<p style='font-size:14px; color:#666;'>Manage your documents efficiently with our offline document hub</p></html>");
        homePanel.add(welcome, BorderLayout.NORTH);

        // Center content with attractive image and stats
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // Create an attractive document-related image (using emoji and text as placeholder)
        JLabel imageLabel = new JLabel("<html><div style='text-align:center;'>"
                + "<div style='font-size:80px;'>📂 🗂️ 📄</div>"
                + "<div style='font-size:24px; color:#228B22; margin:20px;'>Your Secure Document Hub</div>"
                + "<div style='color:#666; font-size:14px;'>Store, organize, and access your files offline</div>"
                + "</div></html>", SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Stat 1
        JPanel stat1 = createStatPanel("12", "Total Documents", "📄");
        // Stat 2
        JPanel stat2 = createStatPanel("5", "Recent Uploads", "🆕");
        // Stat 3
        JPanel stat3 = createStatPanel("2.1", "Storage Used (GB)", "💾");

        statsPanel.add(stat1);
        statsPanel.add(stat2);
        statsPanel.add(stat3);

        centerPanel.add(imageLabel, BorderLayout.CENTER);
        centerPanel.add(statsPanel, BorderLayout.SOUTH);

        homePanel.add(centerPanel, BorderLayout.CENTER);

        // Quick actions panel
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickActions.setBackground(Color.WHITE);
        quickActions.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton quickUpload = UIComponents.styledButton("📤 Quick Upload");
        JButton quickView = UIComponents.styledButton("👁️ View All Files");
        JButton quickSync = UIComponents.styledButton("🔄 Sync Now");

        quickUpload.addActionListener(e -> {
            CardLayout cl = (CardLayout) main.getLayout();
            cl.show(main, "upload");
        });
        quickView.addActionListener(e -> {
            CardLayout cl = (CardLayout) main.getLayout();
            cl.show(main, "view");
        });
        quickSync.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Sync functionality coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE));

        quickActions.add(quickUpload);
        quickActions.add(quickView);
        quickActions.add(quickSync);

        homePanel.add(quickActions, BorderLayout.SOUTH);

        return homePanel;
    }

    private JPanel createUploadPanel() {
        JPanel uploadPanel = new JPanel(new BorderLayout());
        uploadPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        uploadPanel.setBackground(Color.WHITE);

        // Header
        JPanel uploadHeader = new JPanel(new BorderLayout());
        uploadHeader.setBackground(Color.WHITE);
        JLabel upLabel = new JLabel("📤 Upload Documents");
        upLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        upLabel.setForeground(UIComponents.DARK_GREEN);

        JLabel subLabel = new JLabel("Select files to upload or manage existing documents");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(Color.GRAY);

        uploadHeader.add(upLabel, BorderLayout.NORTH);
        uploadHeader.add(subLabel, BorderLayout.SOUTH);
        uploadHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Main content with file browser and upload section
        JPanel uploadContent = new JPanel(new GridLayout(1, 2, 20, 0));
        uploadContent.setBackground(Color.WHITE);

        // Left: File browser for existing files
        JPanel fileBrowserPanel = new JPanel(new BorderLayout());
        fileBrowserPanel.setBackground(Color.WHITE);
        fileBrowserPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "📁 Existing Files & Folders",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                UIComponents.DARK_GREEN
        ));

        // Sample files and folders data
        Object[][] fileData = {
                {"📁", "Projects", "Folder", "2024-01-15", "12 items"},
                {"📁", "Reports", "Folder", "2024-01-10", "8 items"},
                {"📄", "ProjectPlan.docx", "Word Document", "2024-01-12", "2.4 MB"},
                {"📊", "Budget.xlsx", "Excel Spreadsheet", "2024-01-08", "1.8 MB"},
                {"🖼️", "Diagram.png", "Image File", "2024-01-05", "4.2 MB"},
                {"📄", "MeetingNotes.pdf", "PDF Document", "2024-01-03", "3.1 MB"},
                {"🎵", "Presentation.mp4", "Video File", "2023-12-28", "15.7 MB"},
                {"📄", "Requirements.txt", "Text File", "2023-12-20", "0.2 MB"}
        };

        String[] columns = {"Type", "Name", "File Type", "Modified", "Size"};
        DefaultTableModel fileModel = new DefaultTableModel(fileData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable fileTable = new JTable(fileModel);
        fileTable.setRowHeight(40);
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        fileTable.setShowGrid(false);
        fileTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom renderer for better appearance
        fileTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0) {
                    setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    setHorizontalAlignment(CENTER);
                } else {
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    setHorizontalAlignment(LEFT);
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(fileTable);
        tableScroll.setPreferredSize(new Dimension(400, 300));
        fileBrowserPanel.add(tableScroll, BorderLayout.CENTER);

        // Right: Upload section
        JPanel uploadSection = new JPanel();
        uploadSection.setLayout(new BoxLayout(uploadSection, BoxLayout.Y_AXIS));
        uploadSection.setBackground(Color.WHITE);
        uploadSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "⬆️ Upload New Files",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                UIComponents.DARK_GREEN
        ));

        JLabel selected = new JLabel("No file selected");
        selected.setAlignmentX(Component.LEFT_ALIGNMENT);
        selected.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton choose = UIComponents.styledButton("📁 Choose Files...");
        JButton doUpload = UIComponents.styledButton("🚀 Upload Files");
        choose.setAlignmentX(Component.LEFT_ALIGNMENT);
        doUpload.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Upload info panel
        JPanel uploadInfo = new JPanel();
        uploadInfo.setLayout(new BoxLayout(uploadInfo, BoxLayout.Y_AXIS));
        uploadInfo.setBackground(new Color(240, 248, 255));
        uploadInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        uploadInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoTitle = new JLabel("💡 Upload Tips:");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tip1 = new JLabel("• Max file size: 100MB");
        JLabel tip2 = new JLabel("• Supported: PDF, DOC, XLS, Images, Videos");
        JLabel tip3 = new JLabel("• Files are stored locally on your network");

        for (JLabel tip : new JLabel[]{tip1, tip2, tip3}) {
            tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tip.setAlignmentX(Component.LEFT_ALIGNMENT);
            tip.setForeground(Color.DARK_GRAY);
        }

        uploadInfo.add(infoTitle);
        uploadInfo.add(Box.createVerticalStrut(5));
        uploadInfo.add(tip1);
        uploadInfo.add(tip2);
        uploadInfo.add(tip3);

        choose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFiles().length > 1) {
                    selected.setText("Selected: " + chooser.getSelectedFiles().length + " files");
                } else {
                    selected.setText("Selected: " + chooser.getSelectedFile().getName());
                }
            }
        });

        doUpload.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Files uploaded to local server successfully!", "Upload Complete", JOptionPane.INFORMATION_MESSAGE);
            selected.setText("No file selected");
        });

        uploadSection.add(Box.createVerticalStrut(10));
        uploadSection.add(choose);
        uploadSection.add(Box.createVerticalStrut(10));
        uploadSection.add(selected);
        uploadSection.add(Box.createVerticalStrut(20));
        uploadSection.add(doUpload);
        uploadSection.add(Box.createVerticalStrut(20));
        uploadSection.add(uploadInfo);

        uploadContent.add(fileBrowserPanel);
        uploadContent.add(uploadSection);

        uploadPanel.add(uploadHeader, BorderLayout.NORTH);
        uploadPanel.add(uploadContent, BorderLayout.CENTER);

        return uploadPanel;
    }

    private JPanel createViewPanel() {
        String[] cols = {"ID", "Document Name", "Owner", "Date", "Size", "Type"};
        Object[][] data = {
                {"1", "ProjectPlan.docx", user, "2025-09-01", "2.4 MB", "Word Document"},
                {"2", "Budget.xlsx", "finance", "2025-09-12", "1.8 MB", "Excel Spreadsheet"},
                {"3", "MeetingNotes.pdf", user, "2025-09-10", "3.1 MB", "PDF Document"},
                {"4", "Diagram.png", "design", "2025-09-08", "4.2 MB", "Image File"}
        };
        DefaultTableModel model = new DefaultTableModel(data, cols);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane tableScroll = new JScrollPane(table);
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        viewPanel.add(tableScroll, BorderLayout.CENTER);

        return viewPanel;
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        settingsPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("⚙ Application Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIComponents.DARK_GREEN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox autosync = new JCheckBox("Enable auto-sync when internet available");
        autosync.setSelected(true);
        autosync.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        autosync.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox notifications = new JCheckBox("Show desktop notifications");
        notifications.setSelected(true);
        notifications.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notifications.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox startup = new JCheckBox("Launch on system startup");
        startup.setSelected(false);
        startup.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startup.setAlignmentX(Component.LEFT_ALIGNMENT);

        settingsPanel.add(title);
        settingsPanel.add(Box.createVerticalStrut(20));
        settingsPanel.add(autosync);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(notifications);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(startup);

        return settingsPanel;
    }

    private JPanel createAboutPanel() {
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        aboutPanel.setBackground(Color.WHITE);

        JLabel aboutText = new JLabel("<html><div style='text-align:center;'>"
                + "<h1 style='color:#228B22;'>📂 Offline Document Hub</h1>"
                + "<p style='font-size:14px; color:#666; margin:20px;'>"
                + "A secure, local-first document management solution for your LAN environment.<br><br>"
                + "• Store and organize documents offline<br>"
                + "• Sync when network available<br>"
                + "• Secure local storage<br>"
                + "• Multi-user support<br><br>"
                + "<b>Version:</b> 2.1.0<br>"
                + "<b>Developed by:</b> Five Gang Developers"
                + "</p></div></html>", SwingConstants.CENTER);

        aboutPanel.add(aboutText, BorderLayout.CENTER);
        return aboutPanel;
    }

    private JPanel createStatPanel(String value, String label, String icon) {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
        statPanel.setBackground(new Color(240, 248, 255));
        statPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        statPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(icon + " " + value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UIComponents.DARK_GREEN);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statPanel.add(valueLabel);
        statPanel.add(Box.createVerticalStrut(5));
        statPanel.add(descLabel);

        return statPanel;
    }
}
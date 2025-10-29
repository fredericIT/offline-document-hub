import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

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

        // Add notification panel to header
        addNotificationPanel(header);

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

    private void addNotificationPanel(JPanel header) {
        JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notificationPanel.setBackground(UIComponents.DARK_GREEN);
        notificationPanel.setOpaque(false);

        JButton notificationBtn = new JButton("🔔");
        notificationBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        notificationBtn.setBackground(new Color(0, 0, 0, 0));
        notificationBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationBtn.setForeground(UIComponents.LIGHT_TEXT);
        notificationBtn.setFocusPainted(false);
        notificationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add notification count badge
        int unreadCount = NotificationManager.getUnreadCount(user);
        if (unreadCount > 0) {
            notificationBtn.setText("🔔 " + unreadCount);
        }

        notificationBtn.addActionListener(e -> showNotifications());

        notificationPanel.add(notificationBtn);
        header.add(notificationPanel, BorderLayout.EAST);
    }

    private void showNotifications() {
        java.util.List<NotificationManager.Notification> notifications =
                NotificationManager.getUserNotifications(user);

        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No notifications", "Notifications", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create notification dialog
        JDialog notificationDialog = new JDialog(frame, "Notifications", true);
        notificationDialog.setSize(400, 300);
        notificationDialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (NotificationManager.Notification notification : notifications) {
            String readStatus = notification.read ? "✓ " : "● ";
            listModel.addElement(readStatus + notification.title + " - " +
                    new java.text.SimpleDateFormat("MMM dd, HH:mm").format(notification.date));
        }

        JList<String> notificationList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(notificationList);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton markReadBtn = new JButton("Mark as Read");
        JButton markAllReadBtn = new JButton("Mark All as Read");
        JButton closeBtn = new JButton("Close");

        markReadBtn.addActionListener(ev -> {
            int index = notificationList.getSelectedIndex();
            if (index >= 0) {
                NotificationManager.Notification notification = notifications.get(index);
                NotificationManager.markAsRead(user, notification.id);
                notificationDialog.dispose();
                showNotifications(); // Refresh
            }
        });

        markAllReadBtn.addActionListener(ev -> {
            NotificationManager.markAllAsRead(user);
            notificationDialog.dispose();
            JOptionPane.showMessageDialog(frame, "All notifications marked as read");
        });

        closeBtn.addActionListener(ev -> notificationDialog.dispose());

        buttonPanel.add(markReadBtn);
        buttonPanel.add(markAllReadBtn);
        buttonPanel.add(closeBtn);

        panel.add(new JLabel("Your Notifications:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        notificationDialog.add(panel);
        notificationDialog.setVisible(true);
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
        JLabel tip4 = new JLabel("• Storage limit: 16GB per user");

        for (JLabel tip : new JLabel[]{tip1, tip2, tip3, tip4}) {
            tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tip.setAlignmentX(Component.LEFT_ALIGNMENT);
            tip.setForeground(Color.DARK_GRAY);
        }

        uploadInfo.add(infoTitle);
        uploadInfo.add(Box.createVerticalStrut(5));
        uploadInfo.add(tip1);
        uploadInfo.add(tip2);
        uploadInfo.add(tip3);
        uploadInfo.add(tip4);

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
            if (selected.getText().equals("No file selected")) {
                JOptionPane.showMessageDialog(frame, "Please select files first!", "No Files", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check storage limit
            long availableStorage = UserStore.getAvailableStorage(user);
            if (availableStorage <= 0) {
                JOptionPane.showMessageDialog(frame,
                        "Storage limit exceeded! You have used all your 16GB storage.",
                        "Storage Full",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to upload these files to the server?\n\n" +
                            "Files will be synchronized across the network and available to other users.",
                    "Confirm Upload",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Simulate file upload
                // In real implementation, you would get actual file sizes
                long fileSize = 1024 * 1024; // Mock 1MB file

                if (fileSize > availableStorage) {
                    JOptionPane.showMessageDialog(frame,
                            "File size exceeds available storage!",
                            "Storage Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Add document to manager (mock implementation)
                DocumentManager.addDocument("UploadedFile.pdf", user, fileSize, "PDF");

                // If admin uploaded, notify all users
                if (UserStore.isAdmin(user)) {
                    NotificationManager.notifyAllUsers(
                            "New Document Available",
                            "Admin has uploaded a new document: UploadedFile.pdf",
                            "upload"
                    );
                }

                JOptionPane.showMessageDialog(frame,
                        "Files uploaded to local server successfully!\n" +
                                "Storage used: " + formatFileSize(fileSize) + "\n" +
                                "Remaining: " + formatFileSize(availableStorage - fileSize),
                        "Upload Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                selected.setText("No file selected");

                // Refresh the view panel
                refreshViewPanel();
            }
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
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header with storage info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        long usedStorage = UserStore.getUsedStorage(user);
        long totalStorage = UserStore.getUserStorageLimit(user);
        double percentUsed = (usedStorage * 100.0) / totalStorage;

        JLabel storageLabel = new JLabel(String.format(
                "Storage: %.1f GB / %.1f GB used (%.1f%%)",
                usedStorage / (1024.0 * 1024 * 1024),
                totalStorage / (1024.0 * 1024 * 1024),
                percentUsed
        ));
        storageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        storageLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(new JLabel("Your Documents"), BorderLayout.WEST);
        headerPanel.add(storageLabel, BorderLayout.EAST);
        viewPanel.add(headerPanel, BorderLayout.NORTH);

        // Table with documents
        String[] cols = {"ID", "Document Name", "Owner", "Date", "Size", "Type", "Actions"};
        Object[][] data = {
                {"1", "ProjectPlan.docx", user, "2025-09-01", "2.4 MB", "Word Document", "Share"},
                {"2", "Budget.xlsx", "finance", "2025-09-12", "1.8 MB", "Excel Spreadsheet", "Copy Link"},
                {"3", "MeetingNotes.pdf", user, "2025-09-10", "3.1 MB", "PDF Document", "Share"},
                {"4", "Diagram.png", "design", "2025-09-08", "4.2 MB", "Image File", "Copy Link"}
        };

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Add button renderer and editor for actions column
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScroll = new JScrollPane(table);
        viewPanel.add(tableScroll, BorderLayout.CENTER);

        return viewPanel;
    }

    // Button renderer for table actions
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(UIComponents.DARK_GREEN);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            return this;
        }
    }

    // Button editor for table actions
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setBackground(UIComponents.DARK_GREEN);
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click based on action type
                if ("Share".equals(label)) {
                    // Show share dialog
                    String[] users = UserStore.getAllUsernames().toArray(new String[0]);
                    String selectedUser = (String) JOptionPane.showInputDialog(
                            frame,
                            "Select user to share with:",
                            "Share Document",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            users,
                            users[0]
                    );

                    if (selectedUser != null && !selectedUser.equals(user)) {
                        DocumentManager.shareDocumentWithUser(1, selectedUser); // Mock document ID
                        JOptionPane.showMessageDialog(frame,
                                "Document shared with " + selectedUser,
                                "Share Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } else if ("Copy Link".equals(label)) {
                    // Generate and copy shareable link
                    String link = DocumentManager.generateShareableLink(2); // Mock document ID
                    if (link != null) {
                        // Copy to clipboard
                        StringSelection stringSelection = new StringSelection(link);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);

                        JOptionPane.showMessageDialog(frame,
                                "Link copied to clipboard:\n" + link,
                                "Shareable Link",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Cannot generate link for this document",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            isPushed = false;
            return label;
        }
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

        // Storage settings
        JPanel storagePanel = new JPanel(new BorderLayout());
        storagePanel.setBackground(Color.WHITE);
        storagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        storagePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel storageTitle = new JLabel("Storage Information:");
        storageTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        storageTitle.setForeground(UIComponents.DARK_GREEN);

        long usedStorage = UserStore.getUsedStorage(user);
        long totalStorage = UserStore.getUserStorageLimit(user);
        double percentUsed = (usedStorage * 100.0) / totalStorage;

        JLabel storageInfo = new JLabel(String.format(
                "Used: %.1f GB / %.1f GB (%.1f%%)",
                usedStorage / (1024.0 * 1024 * 1024),
                totalStorage / (1024.0 * 1024 * 1024),
                percentUsed
        ));
        storageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        storagePanel.add(storageTitle, BorderLayout.NORTH);
        storagePanel.add(storageInfo, BorderLayout.CENTER);

        settingsPanel.add(title);
        settingsPanel.add(Box.createVerticalStrut(20));
        settingsPanel.add(autosync);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(notifications);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(startup);
        settingsPanel.add(Box.createVerticalStrut(20));
        settingsPanel.add(storagePanel);

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
                + "• Multi-user support<br>"
                + "• 16GB storage per user<br>"
                + "• Document sharing and link generation<br><br>"
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

    // Helper method for file size formatting
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    // Refresh view panel (placeholder for future implementation)
    private void refreshViewPanel() {
        // This would refresh the document view when new files are uploaded
        System.out.println("Refreshing view panel...");
    }
}
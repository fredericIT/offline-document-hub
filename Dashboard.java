import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class Dashboard {
    private final String user;

    public Dashboard(String username) {
        this.user = username;
    }

    public void show() {
        final JFrame frame = new JFrame("Document Hub Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 650);
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
        JPanel main = new JPanel(new CardLayout());
        main.setBackground(Color.WHITE);

        // Home panel
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel welcome = new JLabel("<html><h2>Welcome, " + user + "</h2><p>Use the left menu to navigate.</p></html>");
        homePanel.add(welcome, BorderLayout.NORTH);

        // Upload panel (simple mock)
        JPanel uploadPanel = new JPanel();
        uploadPanel.setLayout(new BoxLayout(uploadPanel, BoxLayout.Y_AXIS));
        uploadPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel upLabel = new JLabel("Upload a document (mock)");
        upLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        upLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel selected = new JLabel("No file selected");
        selected.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton choose = UIComponents.styledButton("Choose file...");
        JButton doUpload = UIComponents.styledButton("Upload (mock)");
        choose.setAlignmentX(Component.LEFT_ALIGNMENT);
        doUpload.setAlignmentX(Component.LEFT_ALIGNMENT);

        choose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                selected.setText("Selected: " + chooser.getSelectedFile().getName());
            }
        });
        doUpload.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "File uploaded to local server (mock).", "Uploaded", JOptionPane.INFORMATION_MESSAGE);
        });

        uploadPanel.add(upLabel);
        uploadPanel.add(Box.createVerticalStrut(12));
        uploadPanel.add(choose);
        uploadPanel.add(Box.createVerticalStrut(8));
        uploadPanel.add(selected);
        uploadPanel.add(Box.createVerticalStrut(12));
        uploadPanel.add(doUpload);

        // View panel (table mock)
        String[] cols = {"ID", "Document Name", "Owner", "Date"};
        Object[][] data = {
                {"1", "ProjectPlan.docx", user, "2025-09-01"},
                {"2", "Budget.xlsx", "finance", "2025-09-12"}
        };
        DefaultTableModel model = new DefaultTableModel(data, cols);
        JTable table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        viewPanel.add(tableScroll, BorderLayout.CENTER);

        // Settings panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JCheckBox autosync = new JCheckBox("Enable auto-sync when internet available");
        autosync.setSelected(true);
        settingsPanel.add(autosync);

        // About panel
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        aboutPanel.add(new JLabel("<html><h3>Offline Document Hub</h3><p>Local-first document management for your LAN.</p></html>"), BorderLayout.NORTH);

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
}

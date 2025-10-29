import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class DocumentHubDemo extends JFrame {
    private String currentUser;
    private DocumentManager dm;
    private DefaultListModel<String> fileListModel;
    private JList<String> fileList;
    private List<Path> currentDisplayPaths;

    public DocumentHubDemo() {
        super("Offline Document Hub - Demo");
        dm = new DocumentManager();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        showLogin();
    }

    private void showLogin() {
        String user = JOptionPane.showInputDialog(this, "Enter your username to login:");
        if (user == null || user.trim().isEmpty()) {
            // exit app if user cancels or empty
            System.exit(0);
        }
        currentUser = user.trim();
        dm.ensureUser(currentUser);
        // After login, show pending requests if any
        SwingUtilities.invokeLater(() -> {
            buildMainUI();
            checkAndShowPendingRequestsForCurrentUser();
        });
    }

    private void buildMainUI() {
        getContentPane().removeAll();
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Logged in as: " + currentUser));
        JButton uploadBtn = new JButton("Upload Document");
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");
        top.add(uploadBtn);
        top.add(refreshBtn);
        top.add(logoutBtn);
        add(top, BorderLayout.NORTH);

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(fileList);
        add(sp, BorderLayout.CENTER);

        // bottom status
        JLabel help = new JLabel("Tip: Double-click a file to open. If file belongs to others, request will be sent.");
        add(help, BorderLayout.SOUTH);

        // listeners
        uploadBtn.addActionListener(e -> onUpload());
        refreshBtn.addActionListener(e -> refreshFiles());
        logoutBtn.addActionListener(e -> {
            dispose();
            new DocumentHubDemo().setVisible(true);
        });

        fileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int idx = fileList.locationToIndex(evt.getPoint());
                    onOpenFile(idx);
                }
            }
        });

        refreshFiles();
        revalidate();
        repaint();
        setVisible(true);
    }

    private void onUpload() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            // confirmation dialog
            int conf = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to upload the document:\n" + f.getName() + "?",
                    "Confirm Upload",
                    JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    Path saved = dm.uploadFile(f, currentUser);
                    JOptionPane.showMessageDialog(this, "Uploaded: " + saved.getFileName().toString());
                    refreshFiles();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Upload cancelled.");
            }
        }
    }

    private void refreshFiles() {
        fileListModel.clear();
        currentDisplayPaths = dm.listFiles(currentUser);
        // show current user's files first
        if (currentDisplayPaths != null) {
            for (Path p : currentDisplayPaths) {
                fileListModel.addElement(currentUser + " / " + p.getFileName().toString());
            }
        }

        // Also show other users' files (optionally) - let's scan everyone else's files in data directory
        File dataRoot = new File("./data");
        File[] users = dataRoot.listFiles(File::isDirectory);
        if (users != null) {
            for (File u : users) {
                String owner = u.getName();
                if (owner.equals(currentUser)) continue;
                List<Path> ownerFiles = dm.listFiles(owner);
                if (ownerFiles != null) {
                    for (Path p : ownerFiles) {
                        fileListModel.addElement(owner + " / " + p.getFileName().toString());
                    }
                }
            }
        }
    }

    // idx in the displayed list
    private void onOpenFile(int idx) {
        if (idx < 0) return;
        String entry = fileListModel.getElementAt(idx);
        // format "owner / filename"
        String[] parts = entry.split("\\s*/\\s*", 2);
        if (parts.length < 2) return;
        String owner = parts[0].trim();
        String filename = parts[1].trim();

        if (owner.equals(currentUser)) {
            // open local file
            Path p = Paths.get("./data", owner, "files", filename);
            dm.openFileInDesktop(p);
        } else {
            // request access
            try {
                dm.createAccessRequest(owner, filename, currentUser);
                JOptionPane.showMessageDialog(this, "Access request sent to " + owner + " for file: " + filename);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to send request: " + ex.getMessage());
            }
        }
    }

    // Owner sees pending requests dialog where they can approve/deny
    private void checkAndShowPendingRequestsForCurrentUser() {
        List<String> reqs = dm.readRequests(currentUser);
        if (reqs == null) return;

        // collect pending
        List<Integer> pendingIdx = new ArrayList<>();
        for (int i = 0; i < reqs.size(); i++) {
            Map<String, String> m = DocumentManager.parseRequestLine(reqs.get(i));
            if (m != null && "PENDING".equalsIgnoreCase(m.get("status"))) {
                pendingIdx.add(i);
            }
        }
        if (pendingIdx.isEmpty()) return;

        // Show dialog listing pending requests
        JDialog d = new JDialog(this, "Pending Access Requests", true);
        d.setSize(600, 350);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        DefaultListModel<String> mdl = new DefaultListModel<>();
        for (int idx : pendingIdx) {
            Map<String, String> m = DocumentManager.parseRequestLine(reqs.get(idx));
            if (m != null) {
                String line = idx + ": " + m.get("requester") + " requests '" + m.get("filename") + "' at " + m.get("timestamp");
                mdl.addElement(line);
            }
        }

        JList<String> lst = new JList<>(mdl);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(lst);
        d.add(sp, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton approve = new JButton("Approve");
        JButton deny = new JButton("Deny");
        JButton close = new JButton("Close");
        btns.add(approve);
        btns.add(deny);
        btns.add(close);
        d.add(btns, BorderLayout.SOUTH);

        approve.addActionListener(e -> {
            int sel = lst.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(d, "Select a request first.");
                return;
            }
            int modelIdx = pendingIdx.get(sel);
            try {
                dm.processRequest(currentUser, modelIdx, true);
                JOptionPane.showMessageDialog(d, "Approved and granted access (file copied).");
                d.dispose();
                refreshFiles();
                // reopen to reflect remaining pending
                checkAndShowPendingRequestsForCurrentUser();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Failed to approve: " + ex.getMessage());
            }
        });

        deny.addActionListener(e -> {
            int sel = lst.getSelectedIndex();
            if (sel == -1) {
                JOptionPane.showMessageDialog(d, "Select a request first.");
                return;
            }
            int modelIdx = pendingIdx.get(sel);
            try {
                dm.processRequest(currentUser, modelIdx, false);
                JOptionPane.showMessageDialog(d, "Denied request.");
                d.dispose();
                // refresh to show remaining pending
                checkAndShowPendingRequestsForCurrentUser();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Failed to deny: " + ex.getMessage());
            }
        });

        close.addActionListener(e -> d.dispose());
        d.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DocumentHubDemo().setVisible(true));
    }
}
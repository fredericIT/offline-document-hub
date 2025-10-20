/*
 AdminDashboard.java
 A Swing-based admin panel for the Offline Document Hub.

 Features:
 - Login placeholder (role-check)
 - Tabs: Users, Roles & Permissions, Access Requests, Sync & Backups, Audit & Monitoring, Deployment
 - JTable-based lists with add/edit/delete actions
 - Background tasks for long-running operations (SwingWorker)
 - Clear extension points for Oracle JDBC & Cloud backup (Google Drive / S3) integration

 Note:
  - Implement real authentication and secure password handling (bcrypt / Argon2) on the server/DB side.
  - Replace DB placeholders in AdminService with real JDBC / prepared statements.
  - Cloud backup methods should call secure APIs with stored admin credentials.
*/

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AdminDashboard {

    private JFrame frame;
    private JTabbedPane tabs;
    private AdminService adminService;

    // Logged-in admin username (for audit)
    private String adminUsername = "admin";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminService service = new AdminService(); // replace with real service wired to Oracle
            new AdminDashboard(service).show();
        });
    }

    public AdminDashboard(AdminService service) {
        this.adminService = service;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Offline Document Hub — Admin Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("Admin Control Panel — Offline Document Hub");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        topBar.add(title, BorderLayout.WEST);

        JLabel adminLabel = new JLabel("Signed in as: " + adminUsername);
        topBar.add(adminLabel, BorderLayout.EAST);

        frame.getContentPane().add(topBar, BorderLayout.NORTH);

        tabs = new JTabbedPane();

        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Roles & Permissions", createRolesPermissionsPanel());
        tabs.addTab("Access Requests", createRequestsPanel());
        tabs.addTab("Sync & Backups", createSyncBackupPanel());
        tabs.addTab("Audit & Monitoring", createAuditPanel());
        tabs.addTab("Deployment & Settings", createDeploymentPanel());

        frame.getContentPane().add(tabs, BorderLayout.CENTER);
    }

    public void show() {
        frame.setVisible(true);
    }

    // ---------------------- Users Panel ----------------------
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"User ID", "Username", "Full Name", "Email", "Role", "Active"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            // disallow editing directly in table
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add User");
        JButton btnEdit = new JButton("Edit User");
        JButton btnDelete = new JButton("Delete User");
        JButton btnResetPassword = new JButton("Reset Password");

        actions.add(btnRefresh);
        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnResetPassword);
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.NORTH);

        // Load users initially
        loadUsersIntoModel(model);

        // Button actions
        btnRefresh.addActionListener(e -> loadUsersIntoModel(model));

        btnAdd.addActionListener(e -> {
            UserFormDialog dialog = new UserFormDialog(frame, "Add User", null, adminService);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                adminService.audit(adminUsername, "CREATE_USER", "Created user: " + dialog.getUser().username);
                loadUsersIntoModel(model);
            }
        });

        btnEdit.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(frame, "Select a user to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object idVal = model.getValueAt(sel, 0);
            int userId = (Integer) idVal;
            User user = adminService.getUserById(userId);
            UserFormDialog dialog = new UserFormDialog(frame, "Edit User", user, adminService);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                adminService.audit(adminUsername, "UPDATE_USER", "Updated user: " + user.username);
                loadUsersIntoModel(model);
            }
        });

        btnResetPassword.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(frame, "Select a user to reset password.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int userId = (Integer) model.getValueAt(sel, 0);
            String newPass = JOptionPane.showInputDialog(frame, "Enter new temporary password:");
            if (newPass != null && !newPass.trim().isEmpty()) {
                boolean ok = adminService.resetUserPassword(userId, newPass);
                if (ok) {
                    adminService.audit(adminUsername, "RESET_PASSWORD", "Reset password for user id: " + userId);
                    JOptionPane.showMessageDialog(frame, "Password reset successfully.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to reset password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(frame, "Select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int userId = (Integer) model.getValueAt(sel, 0);
            int confirm = JOptionPane.showConfirmDialog(frame, "Delete user id " + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = adminService.deleteUser(userId);
                if (ok) {
                    adminService.audit(adminUsername, "DELETE_USER", "Deleted user id: " + userId);
                    loadUsersIntoModel(model);
                } else {
                    JOptionPane.showMessageDialog(frame, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void loadUsersIntoModel(DefaultTableModel model) {
        // Clear
        model.setRowCount(0);

        // fetch in background
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return adminService.listUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    for (User u : users) {
                        model.addRow(new Object[]{
                                u.id, u.username, u.fullName, u.email, u.roleName, u.isActive
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Failed to load users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ---------------------- Roles & Permissions Panel ----------------------
    private JPanel createRolesPermissionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.35);

        // Left: Roles list
        DefaultListModel<Role> roleModel = new DefaultListModel<>();
        JList<Role> roleList = new JList<>(roleModel);
        roleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane roleScroll = new JScrollPane(roleList);
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Roles"), BorderLayout.NORTH);
        left.add(roleScroll, BorderLayout.CENTER);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddRole = new JButton("Add Role");
        JButton btnRemoveRole = new JButton("Remove Role");
        leftActions.add(btnAddRole);
        leftActions.add(btnRemoveRole);
        left.add(leftActions, BorderLayout.SOUTH);

        // Right: Permissions editor
        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Permissions (for selected role)"), BorderLayout.NORTH);

        DefaultTableModel permModel = new DefaultTableModel(new String[]{"Permission", "Allowed"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 1;
            }
        };
        JTable permTable = new JTable(permModel);
        permTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        right.add(new JScrollPane(permTable), BorderLayout.CENTER);

        JPanel permActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSavePerms = new JButton("Save Permissions");
        permActions.add(btnSavePerms);
        right.add(permActions, BorderLayout.SOUTH);

        split.setLeftComponent(left);
        split.setRightComponent(right);

        panel.add(split, BorderLayout.CENTER);

        // Load roles
        loadRoles(roleModel);

        btnAddRole.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Role name:");
            if (name != null && !name.trim().isEmpty()) {
                adminService.createRole(name.trim(), ""); // description optional
                adminService.audit(adminUsername, "CREATE_ROLE", "Created role: " + name);
                loadRoles(roleModel);
            }
        });

        btnRemoveRole.addActionListener(e -> {
            Role sel = roleList.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select a role to remove.");
                return;
            }
            int c = JOptionPane.showConfirmDialog(frame, "Delete role " + sel.name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                adminService.deleteRole(sel.id);
                adminService.audit(adminUsername, "DELETE_ROLE", "Deleted role: " + sel.name);
                loadRoles(roleModel);
            }
        });

        roleList.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                Role sel = roleList.getSelectedValue();
                permModel.setRowCount(0);
                if (sel != null) {
                    Map<String, Boolean> perms = adminService.getPermissionsForRole(sel.id);
                    // A fixed permission set for display — expand as needed
                    List<String> permissionNames = Arrays.asList("document.read", "document.write", "document.delete", "document.share", "backup.trigger", "user.manage");
                    for (String p : permissionNames) {
                        permModel.addRow(new Object[]{p, perms.getOrDefault(p, false)});
                    }
                }
            }
        });

        btnSavePerms.addActionListener(e -> {
            Role sel = roleList.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select a role first.");
                return;
            }
            Map<String, Boolean> newPerms = new HashMap<>();
            for (int r = 0; r < permModel.getRowCount(); r++) {
                String perm = (String) permModel.getValueAt(r, 0);
                Boolean allowed = (Boolean) permModel.getValueAt(r, 1);
                newPerms.put(perm, allowed != null && allowed);
            }
            adminService.setPermissionsForRole(sel.id, newPerms);
            adminService.audit(adminUsername, "UPDATE_ROLE_PERMS", "Updated permissions for role: " + sel.name);
            JOptionPane.showMessageDialog(frame, "Permissions saved.");
        });

        return panel;
    }

    private void loadRoles(DefaultListModel<Role> model) {
        model.clear();
        for (Role r : adminService.listRoles()) {
            model.addElement(r);
        }
    }

    // ---------------------- Access Requests ----------------------
    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Request ID", "User", "Document ID", "Document Title", "Requested At", "Status"};
        DefaultTableModel tblModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tblModel);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        actions.add(btnRefresh);
        actions.add(btnApprove);
        actions.add(btnReject);
        panel.add(actions, BorderLayout.NORTH);

        loadRequestsIntoModel(tblModel);

        btnRefresh.addActionListener(e -> loadRequestsIntoModel(tblModel));

        btnApprove.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(frame, "Select a request."); return; }
            int reqId = (Integer) tblModel.getValueAt(sel, 0);
            adminService.approveRequest(reqId, adminUsername);
            adminService.audit(adminUsername, "APPROVE_REQUEST", "Approved request id: " + reqId);
            loadRequestsIntoModel(tblModel);
        });

        btnReject.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(frame, "Select a request."); return; }
            int reqId = (Integer) tblModel.getValueAt(sel, 0);
            adminService.rejectRequest(reqId, adminUsername);
            adminService.audit(adminUsername, "REJECT_REQUEST", "Rejected request id: " + reqId);
            loadRequestsIntoModel(tblModel);
        });

        return panel;
    }

    private void loadRequestsIntoModel(DefaultTableModel model) {
        model.setRowCount(0);
        new SwingWorker<List<AccessRequest>, Void>() {
            @Override
            protected List<AccessRequest> doInBackground() throws Exception {
                return adminService.listAccessRequests();
            }

            @Override
            protected void done() {
                try {
                    for (AccessRequest r : get()) {
                        model.addRow(new Object[]{r.id, r.username, r.documentId, r.documentTitle, r.requestedAt, r.status});
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Failed to load requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ---------------------- Sync & Backups ----------------------
    private JPanel createSyncBackupPanel() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel top = new JPanel(new GridLayout(2, 1, 6, 6));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnTriggerBackup = new JButton("Trigger Cloud Backup (Admin Only)");
        JButton btnViewBackups = new JButton("View Backup History");
        JButton btnSimulateNetwork = new JButton("Simulate Network Available");
        controls.add(btnTriggerBackup);
        controls.add(btnViewBackups);
        controls.add(btnSimulateNetwork);
        top.add(controls);

        JTextArea logArea = new JTextArea(6, 80);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        top.add(logScroll);

        panel.add(top, BorderLayout.NORTH);

        // Bottom: automation controls & status
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox chkAutoBackup = new JCheckBox("Enable Automatic Backup when Internet Available");
        JButton btnConfigureCloud = new JButton("Cloud Settings");
        bottom.add(chkAutoBackup);
        bottom.add(btnConfigureCloud);
        panel.add(bottom, BorderLayout.SOUTH);

        // Actions
        btnTriggerBackup.addActionListener(e -> {
            // confirm admin-only
            int c = JOptionPane.showConfirmDialog(frame, "Trigger backup to cloud now? Admin-only action.", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                logArea.append(now() + " - Starting cloud backup...\n");
                btnTriggerBackup.setEnabled(false);
                new SwingWorker<Boolean, String>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        publish("Preparing dump...");
                        // TODO: create DB dump & encrypt
                        Thread.sleep(1000); // simulate
                        publish("Uploading to cloud...");
                        boolean ok = adminService.performCloudBackup(adminUsername);
                        return ok;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        for (String s : chunks) logArea.append(now() + " - " + s + "\n");
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean ok = get();
                            logArea.append(now() + " - Backup " + (ok ? "succeeded." : "failed.") + "\n");
                            adminService.audit(adminUsername, "CLOUD_BACKUP", "Manual backup triggered. Success=" + ok);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logArea.append(now() + " - Exception: " + ex.getMessage() + "\n");
                        } finally {
                            btnTriggerBackup.setEnabled(true);
                        }
                    }
                }.execute();
            }
        });

        btnSimulateNetwork.addActionListener(e -> {
            // simulate a network event that triggers automatic backup if enabled
            if (chkAutoBackup.isSelected()) {
                logArea.append(now() + " - Network detected. Starting automatic backup...\n");
                new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return adminService.performCloudBackup(adminUsername);
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean ok = get();
                            logArea.append(now() + " - Auto-backup " + (ok ? "success." : "fail.") + "\n");
                            adminService.audit(adminUsername, "AUTO_BACKUP", "Auto backup on network detect. Success=" + ok);
                        } catch (Exception ex) {
                            logArea.append(now() + " - Auto-backup error: " + ex.getMessage() + "\n");
                        }
                    }
                }.execute();
            } else {
                logArea.append(now() + " - Network detected, but automatic backup is disabled.\n");
            }
        });

        btnViewBackups.addActionListener(e -> {
            List<BackupRecord> recs = adminService.listBackups();
            StringBuilder sb = new StringBuilder("Backup history:\n");
            for (BackupRecord r : recs) {
                sb.append(String.format("%s | %s | %s\n", r.createdAt, r.cloudProvider, r.remotePath));
            }
            JOptionPane.showMessageDialog(frame, new JScrollPane(new JTextArea(sb.toString())), "Backups", JOptionPane.INFORMATION_MESSAGE);
        });

        btnConfigureCloud.addActionListener(e -> {
            CloudSettingsDialog dlg = new CloudSettingsDialog(frame, adminService.getCloudSettings(), adminService);
            dlg.setVisible(true);
        });

        return panel;
    }

    // ---------------------- Audit & Monitoring Panel ----------------------
    private JPanel createAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout(6,6));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        String[] cols = {"Time", "User", "Action", "Object", "Details"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnFilter = new JButton("Filter...");
        top.add(btnRefresh);
        top.add(btnFilter);
        panel.add(top, BorderLayout.NORTH);

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            for (AuditLog a : adminService.listAuditLogs(200)) {
                model.addRow(new Object[]{a.actionTime, a.user, a.action, a.objectType, a.details});
            }
        });

        btnRefresh.doClick(); // load once

        btnFilter.addActionListener(e -> {
            String filter = JOptionPane.showInputDialog(frame, "Filter by user or action:");
            if (filter != null) {
                model.setRowCount(0);
                for (AuditLog a : adminService.searchAudit(filter)) {
                    model.addRow(new Object[]{a.actionTime, a.user, a.action, a.objectType, a.details});
                }
            }
        });

        return panel;
    }

    // ---------------------- Deployment Settings ----------------------
    private JPanel createDeploymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(6,6));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel info = new JPanel(new GridLayout(0,1));
        info.add(new JLabel("Scalable Deployment:"));
        info.add(new JLabel("- Install client on multiple PCs; set server IP in settings."));
        info.add(new JLabel("- Use provided installer (not included here)."));
        info.add(new JLabel("- Central Admin PC has is_server=true in CLIENT_MACHINES table."));

        panel.add(info, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnExportConfig = new JButton("Export Client Config");
        JButton btnImportConfig = new JButton("Import Config to PC");
        actions.add(btnExportConfig);
        actions.add(btnImportConfig);
        panel.add(actions, BorderLayout.CENTER);

        btnExportConfig.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int rc = chooser.showSaveDialog(frame);
            if (rc == JFileChooser.APPROVE_OPTION) {
                boolean ok = adminService.exportClientConfig(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(frame, ok ? "Exported." : "Failed to export.");
            }
        });

        btnImportConfig.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int rc = chooser.showOpenDialog(frame);
            if (rc == JFileChooser.APPROVE_OPTION) {
                boolean ok = adminService.importClientConfig(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(frame, ok ? "Imported." : "Failed to import.");
            }
        });

        return panel;
    }

    // ---------------------- Small Helpers & Dialogs ----------------------
    private static String now() {
        return LocalDateTime.now().toString();
    }

    // User form (add / edit)
    static class UserFormDialog extends JDialog {
        private boolean saved = false;
        private User user;
        private JTextField tfUsername = new JTextField(20);
        private JTextField tfFullName = new JTextField(30);
        private JTextField tfEmail = new JTextField(30);
        private JComboBox<Role> cbRoles = new JComboBox<>();
        private JCheckBox chkActive = new JCheckBox("Active", true);
        private AdminService adminService;

        UserFormDialog(Frame owner, String title, User user, AdminService service) {
            super(owner, title, true);
            this.user = user;
            this.adminService = service;
            setup();
            pack();
            setLocationRelativeTo(owner);
        }

        private void setup() {
            setLayout(new BorderLayout(6,6));
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4,4,4,4);
            c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.EAST;
            form.add(new JLabel("Username:"), c);
            c.gridx = 1; c.anchor = GridBagConstraints.WEST;
            form.add(tfUsername, c);

            c.gridx = 0; c.gridy++;
            c.anchor = GridBagConstraints.EAST; form.add(new JLabel("Full name:"), c);
            c.gridx = 1; c.anchor = GridBagConstraints.WEST; form.add(tfFullName, c);

            c.gridx = 0; c.gridy++;
            c.anchor = GridBagConstraints.EAST; form.add(new JLabel("Email:"), c);
            c.gridx = 1; c.anchor = GridBagConstraints.WEST; form.add(tfEmail, c);

            c.gridx = 0; c.gridy++;
            c.anchor = GridBagConstraints.EAST; form.add(new JLabel("Role:"), c);
            c.gridx = 1; c.anchor = GridBagConstraints.WEST;
            for (Role r : adminService.listRoles()) cbRoles.addItem(r);
            form.add(cbRoles, c);

            c.gridx = 1; c.gridy++;
            form.add(chkActive, c);

            add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSave = new JButton("Save");
            JButton btnCancel = new JButton("Cancel");
            actions.add(btnSave);
            actions.add(btnCancel);
            add(actions, BorderLayout.SOUTH);

            if (user != null) {
                tfUsername.setText(user.username);
                tfFullName.setText(user.fullName);
                tfEmail.setText(user.email);
                chkActive.setSelected(user.isActive);
                // select role
                for (int i = 0; i < cbRoles.getItemCount(); i++) {
                    if (cbRoles.getItemAt(i).id == user.roleId) cbRoles.setSelectedIndex(i);
                }
            }

            btnSave.addActionListener(e -> {
                String uname = tfUsername.getText().trim();
                if (uname.isEmpty()) { JOptionPane.showMessageDialog(this, "Username required"); return; }
                Role sel = (Role) cbRoles.getSelectedItem();
                if (user == null) {
                    // create
                    User u = new User();
                    u.username = uname;
                    u.fullName = tfFullName.getText().trim();
                    u.email = tfEmail.getText().trim();
                    u.roleId = sel != null ? sel.id : 1;
                    u.isActive = chkActive.isSelected();
                    boolean ok = adminService.createUser(u);
                    if (ok) {
                        saved = true; user = u;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Create failed.");
                    }
                } else {
                    // update
                    user.username = uname;
                    user.fullName = tfFullName.getText().trim();
                    user.email = tfEmail.getText().trim();
                    user.roleId = sel != null ? sel.id : user.roleId;
                    user.isActive = chkActive.isSelected();
                    boolean ok = adminService.updateUser(user);
                    if (ok) {
                        saved = true; dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Update failed.");
                    }
                }
            });

            btnCancel.addActionListener(e -> dispose());
        }

        public boolean isSaved() { return saved; }
        public User getUser() { return user; }
    }

    // Cloud settings dialog
    static class CloudSettingsDialog extends JDialog {
        CloudSettingsDialog(Frame owner, CloudSettings s, AdminService service) {
            super(owner, "Cloud Settings", true);
            setLayout(new BorderLayout(6,6));
            JPanel p = new JPanel(new GridLayout(0,2,4,4));
            JTextField tfProvider = new JTextField(s.cloudProvider);
            JTextField tfRemotePath = new JTextField(s.remotePath);
            p.add(new JLabel("Provider:")); p.add(tfProvider);
            p.add(new JLabel("Remote Path:")); p.add(tfRemotePath);
            add(p, BorderLayout.CENTER);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSave = new JButton("Save");
            JButton btnCancel = new JButton("Cancel");
            actions.add(btnSave); actions.add(btnCancel);
            add(actions, BorderLayout.SOUTH);

            btnSave.addActionListener(e -> {
                s.cloudProvider = tfProvider.getText().trim();
                s.remotePath = tfRemotePath.getText().trim();
                service.saveCloudSettings(s);
                dispose();
            });
            btnCancel.addActionListener(e -> dispose());
            pack(); setLocationRelativeTo(owner);
        }
    }

    // ---------------------- Domain / DTO Classes ----------------------
    static class User {
        int id;
        String username;
        String fullName;
        String email;
        int roleId;
        String roleName;
        boolean isActive;
    }

    static class Role {
        int id;
        String name;
        String description;

        @Override
        public String toString() {
            return name != null ? name : "Role " + id;
        }
    }

    static class AccessRequest {
        int id;
        String username;
        int documentId;
        String documentTitle;
        String requestedAt;
        String status;
    }

    static class BackupRecord {
        int id;
        String cloudProvider;
        String remotePath;
        String createdAt;
        long sizeBytes;
    }

    static class AuditLog {
        String actionTime;
        String user;
        String action;
        String objectType;
        String details;
    }

    static class CloudSettings {
        String cloudProvider = "google-drive";
        String remotePath = "backups/offline-docs/";
        // store keys/token securely — DO NOT hardcode in production
    }

    // ---------------------- AdminService (stubbed) ----------------------
    /**
     * AdminService is a single point to implement DB & Cloud interactions.
     * Replace stub methods with real Oracle JDBC / cloud API calls.
     */
    static class AdminService {
        private final String ORACLE_URL = "jdbc:oracle:thin:@//HOST:1521/SERVICE";
        private final String ORACLE_USER = "dbuser";
        private final String ORACLE_PASS = "dbpass";

        // NOTE: in production, never store credentials in code. Use a secure vault.
        private CloudSettings cloudSettings = new CloudSettings();

        // Example: get DB connection (caller should close)
        private Connection getConnection() throws SQLException {
            // TODO: Add Oracle JDBC driver to classpath and uncomment
            // return DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASS);
            // For now we throw to remind implementer
            throw new SQLException("Oracle connection not implemented. Please wire AdminService.getConnection()");
        }

        // Users
        List<User> listUsers() {
            // TODO: query USERS + ROLES to return list
            // stubbed sample
            List<User> out = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                User u = new User();
                u.id = i;
                u.username = "user" + i;
                u.fullName = "User " + i;
                u.email = "user" + i + "@example.com";
                u.roleId = (i % 3) + 1;
                u.roleName = (u.roleId == 1 ? "Admin" : (u.roleId == 2 ? "Editor" : "Viewer"));
                u.isActive = true;
                out.add(u);
            }
            return out;
        }

        User getUserById(int id) {
            // TODO: fetch from DB
            User u = new User();
            u.id = id;
            u.username = "user" + id;
            u.fullName = "User " + id;
            u.email = "user" + id + "@example.com";
            u.roleId = 2;
            u.roleName = "Editor";
            u.isActive = true;
            return u;
        }

        boolean createUser(User u) {
            // TODO: use INSERT with hashed password (generate temp password), send email if needed
            System.out.println("createUser: " + u.username);
            return true;
        }

        boolean updateUser(User u) {
            // TODO: UPDATE users set full_name=?, email=?, role_id=?, is_active=? WHERE user_id=?
            System.out.println("updateUser: " + u.username);
            return true;
        }

        boolean deleteUser(int userId) {
            // TODO: set user as inactive or delete row after checks
            System.out.println("deleteUser id:" + userId);
            return true;
        }

        boolean resetUserPassword(int userId, String newPassword) {
            // TODO: hash & store password hash in DB, send temp password to user over secure channel
            System.out.println("reset password for " + userId);
            return true;
        }

        // Roles & permissions
        List<Role> listRoles() {
            // TODO: select * from ROLES
            List<Role> r = new ArrayList<>();
            Role a = new Role(); a.id = 1; a.name = "Admin"; r.add(a);
            Role e = new Role(); e.id = 2; e.name = "Editor"; r.add(e);
            Role v = new Role(); v.id = 3; v.name = "Viewer"; r.add(v);
            return r;
        }

        void createRole(String name, String description) {
            System.out.println("createRole: " + name);
        }

        void deleteRole(int roleId) {
            System.out.println("deleteRole: " + roleId);
        }

        Map<String, Boolean> getPermissionsForRole(int roleId) {
            // TODO: fetch from PERMISSIONS table or ROLE_PERMS
            Map<String, Boolean> perms = new HashMap<>();
            perms.put("document.read", true);
            perms.put("document.write", roleId == 1 || roleId == 2);
            perms.put("backup.trigger", roleId == 1);
            perms.put("user.manage", roleId == 1);
            return perms;
        }

        void setPermissionsForRole(int roleId, Map<String, Boolean> perms) {
            // TODO: store mapping in DB
            System.out.println("set perms for role " + roleId + ": " + perms);
        }

        // Access requests
        List<AccessRequest> listAccessRequests() {
            List<AccessRequest> out = new ArrayList<>();
            AccessRequest r = new AccessRequest();
            r.id = 1; r.username = "user3"; r.documentId = 42; r.documentTitle = "Report.docx";
            r.requestedAt = LocalDateTime.now().minusHours(2).toString();
            r.status = "PENDING";
            out.add(r);
            return out;
        }

        void approveRequest(int requestId, String by) {
            // TODO: update request row, send notification to user, add permission row
            System.out.println("approved request " + requestId + " by " + by);
        }

        void rejectRequest(int requestId, String by) {
            // TODO: update request row with REJECTED, send notification
            System.out.println("rejected request " + requestId + " by " + by);
        }

        // Backups & Cloud
        boolean performCloudBackup(String triggeredBy) {
            // TODO:
            // 1) create Oracle DB dump (expdp or export logic)
            // 2) encrypt backup
            // 3) upload to cloud (Google Drive, AWS S3, etc) using secure API and service account
            // 4) insert BACKUPS row
            System.out.println("performCloudBackup triggeredBy=" + triggeredBy);
            try {
                // simulate time
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
            return true;
        }

        List<BackupRecord> listBackups() {
            // TODO: SELECT * FROM BACKUPS ORDER BY created_at DESC
            BackupRecord b = new BackupRecord();
            b.id = 1;
            b.cloudProvider = cloudSettings.cloudProvider;
            b.remotePath = cloudSettings.remotePath + "backup-2025-10-18.zip";
            b.createdAt = LocalDateTime.now().minusDays(1).toString();
            b.sizeBytes = 1024 * 1024 * 20;
            return Arrays.asList(b);
        }

        CloudSettings getCloudSettings() { return cloudSettings; }
        void saveCloudSettings(CloudSettings s) { this.cloudSettings = s; System.out.println("Saved cloud settings."); }
        boolean exportClientConfig(String path) { System.out.println("Exported client config to " + path); return true; }
        boolean importClientConfig(String path) { System.out.println("Imported client config from " + path); return true; }

        // Audit
        void audit(String user, String action, String details) {
            // TODO: INSERT into AUDIT_LOGS (user_id, action, object_type, object_id, action_time, details)
            System.out.println("AUDIT: user=" + user + " action=" + action + " details=" + details);
        }

        List<AuditLog> listAuditLogs(int limit) {
            AuditLog a = new AuditLog();
            a.actionTime = LocalDateTime.now().minusHours(3).toString();
            a.user = "system";
            a.action = "SYSTEM_START";
            a.objectType = "SYSTEM";
            a.details = "System started";
            return Arrays.asList(a);
        }

        List<AuditLog> searchAudit(String filter) {
            // TODO: query audit logs for filter
            return listAuditLogs(100);
        }
    }
}

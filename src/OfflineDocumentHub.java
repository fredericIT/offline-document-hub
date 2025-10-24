import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// Main class
public class OfflineDocumentHub {

    // In-memory storage
    static Map<String, String> users = new HashMap<>(); // username -> password
    static Map<String, Document> documents = new HashMap<>(); // document name -> Document object

    public static void main(String[] args) {
        // Default users
        users.put("admin", "admin");
        users.put("user", "user");
        users.put("Iradukunda", "Kigali@123");

        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}

// Document class
class Document {
    String name;
    String content;
    List<String> comments = new ArrayList<>();
    Map<String, String> replies = new HashMap<>();

    public Document(String name, String content) {
        this.name = name;
        this.content = content;
    }
}

// ====================== LOGIN PAGE ======================
class LoginPage extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login - Offline Document Hub");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(230, 240, 255));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Offline Document Hub", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(25, 50, 100));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField();
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        add(loginBtn, gbc);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(34, 139, 34));
        registerBtn.setForeground(Color.WHITE);
        gbc.gridx = 1;
        add(registerBtn, gbc);

        JButton viewDocsBtn = new JButton("View Documents");
        viewDocsBtn.setBackground(new Color(255, 165, 0));
        viewDocsBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(viewDocsBtn, gbc);

        // ===== Actions =====
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegistrationPage().setVisible(true);
            dispose();
        });
        viewDocsBtn.addActionListener(e -> showDocuments());
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if(OfflineDocumentHub.users.containsKey(username) &&
                OfflineDocumentHub.users.get(username).equals(password)) {
            if(username.equals("admin")) {
                new AdminPanel().setVisible(true);
            } else {
                new Dashboard(username).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }

    private void showDocuments() {
        if(OfflineDocumentHub.documents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No documents available.");
            return;
        }

        String[] columns = {"Document Name", "Preview"};
        Object[][] data = OfflineDocumentHub.documents.values().stream()
                .map(doc -> new Object[]{doc.name, doc.content.length() > 20 ? doc.content.substring(0,20)+"..." : doc.content})
                .toArray(Object[][]::new);

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame docFrame = new JFrame("All Documents");
        docFrame.add(scrollPane);
        docFrame.setSize(500, 300);
        docFrame.setLocationRelativeTo(null);
        docFrame.setVisible(true);
    }
}

// ====================== REGISTRATION PAGE ======================
class RegistrationPage extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;

    public RegistrationPage() {
        setTitle("Register - Offline Document Hub");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(230, 240, 255));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Register New User", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField();
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(34, 139, 34));
        registerBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        add(registerBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setBackground(new Color(70, 130, 180));
        backBtn.setForeground(Color.WHITE);
        gbc.gridx = 1;
        add(backBtn, gbc);

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        if(OfflineDocumentHub.users.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists!");
            return;
        }

        OfflineDocumentHub.users.put(username, password);
        JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
        new LoginPage().setVisible(true);
        dispose();
    }
}

// ====================== USER DASHBOARD ======================
// ====================== USER DASHBOARD ======================
class Dashboard extends JFrame {
    String username;

    public Dashboard(String username) {
        this.username = username;
        setTitle(username + " Dashboard - Offline Document Hub");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));
        setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(new Color(25, 50, 100));
        add(welcome, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBackground(new Color(240, 245, 250));

        JButton showDocs = new JButton("Show Documents");
        JButton uploadDoc = new JButton("Upload Document");
        JButton downloadDoc = new JButton("Download Document");
        JButton deleteDoc = new JButton("Delete Document");
        JButton backupDocs = new JButton("Backup Documents");
        JButton replyDoc = new JButton("Reply to Document");
        JButton commentDoc = new JButton("Comment on Document");
        JButton reviewDocs = new JButton("Review Documents");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {showDocs, uploadDoc, downloadDoc, deleteDoc, backupDocs, replyDoc, commentDoc, reviewDocs, logoutBtn};
        Color[] colors = {new Color(70,130,180), new Color(34,139,34), new Color(255,140,0),
                new Color(220,20,60), new Color(138,43,226), new Color(255,105,180),
                new Color(60,179,113), new Color(255,69,0), new Color(128,128,128)};

        for(int i=0; i<buttons.length; i++) {
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBackground(colors[i]);
            panel.add(buttons[i]);
        }

        add(panel, BorderLayout.CENTER);

        // ===== Actions =====
        showDocs.addActionListener(e -> viewDocuments());
        uploadDoc.addActionListener(e -> createDocument());
        downloadDoc.addActionListener(e -> downloadDocument());
        deleteDoc.addActionListener(e -> deleteDocument());
        backupDocs.addActionListener(e -> backupDocuments());
        replyDoc.addActionListener(e -> replyDocument());
        commentDoc.addActionListener(e -> commentDocument());
        reviewDocs.addActionListener(e -> reviewDocuments());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void viewDocuments() {
        if(OfflineDocumentHub.documents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No documents available.");
            return;
        }
        StringBuilder sb = new StringBuilder("Documents:\n");
        OfflineDocumentHub.documents.keySet().forEach(doc -> sb.append("- ").append(doc).append("\n"));
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void createDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name:");
        if(docName == null || docName.isEmpty()) return;

        String content = JOptionPane.showInputDialog(this, "Enter content:");
        if(content == null) return;

        OfflineDocumentHub.documents.put(docName, new Document(docName, content));
        JOptionPane.showMessageDialog(this, "Document uploaded successfully!");
    }

    private void downloadDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to download:");
        if(!OfflineDocumentHub.documents.containsKey(docName)) {
            JOptionPane.showMessageDialog(this, "Document not found!");
            return;
        }
        Document doc = OfflineDocumentHub.documents.get(docName);
        JOptionPane.showMessageDialog(this, "Simulated Download:\nDocument Name: " + doc.name + "\nContent: " + doc.content);
    }

    private void deleteDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to delete:");
        if(OfflineDocumentHub.documents.containsKey(docName)) {
            OfflineDocumentHub.documents.remove(docName);
            JOptionPane.showMessageDialog(this, "Document deleted!");
        } else {
            JOptionPane.showMessageDialog(this, "Document not found!");
        }
    }

    private void backupDocuments() {
        JOptionPane.showMessageDialog(this, "Backup simulated: " + OfflineDocumentHub.documents.size() + " documents backed up!");
    }

    private void replyDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to reply to:");
        if(!OfflineDocumentHub.documents.containsKey(docName)) {
            JOptionPane.showMessageDialog(this, "Document not found!");
            return;
        }
        String reply = JOptionPane.showInputDialog(this, "Enter your reply:");
        if(reply != null) {
            OfflineDocumentHub.documents.get(docName).replies.put(username, reply);
            JOptionPane.showMessageDialog(this, "Reply added!");
        }
    }

    private void commentDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to comment on:");
        if(!OfflineDocumentHub.documents.containsKey(docName)) {
            JOptionPane.showMessageDialog(this, "Document not found!");
            return;
        }
        String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
        if(comment != null) {
            OfflineDocumentHub.documents.get(docName).comments.add(username + ": " + comment);
            JOptionPane.showMessageDialog(this, "Comment added!");
        }
    }

    private void reviewDocuments() {
        StringBuilder sb = new StringBuilder("Document Reviews:\n");
        OfflineDocumentHub.documents.forEach((name, doc) -> {
            sb.append("\nDocument: ").append(name);
            sb.append("\nComments: ").append(doc.comments.isEmpty() ? "None" : String.join(", ", doc.comments));
            sb.append("\nReplies: ").append(doc.replies.isEmpty() ? "None" : doc.replies.toString());
        });
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}


// ====================== ADMIN PANEL ======================
class AdminPanel extends JFrame {

    public AdminPanel() {
        setTitle("Admin Panel - Offline Document Hub");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));
        setLayout(new BorderLayout());

        JLabel welcome = new JLabel("Welcome, Admin!", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(new Color(25, 50, 100));
        add(welcome, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBackground(new Color(240, 245, 250));

        JButton showDocs = new JButton("Show Documents");
        JButton uploadDoc = new JButton("Upload Document");
        JButton downloadDoc = new JButton("Download Document");
        JButton deleteDoc = new JButton("Delete Document");
        JButton backupDocs = new JButton("Backup Documents");
        JButton replyDoc = new JButton("Reply to Document");
        JButton commentDoc = new JButton("Comment on Document");
        JButton manageUsers = new JButton("Manage Users");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {showDocs, uploadDoc, downloadDoc, deleteDoc, backupDocs, replyDoc, commentDoc, manageUsers, logoutBtn};
        Color[] colors = {new Color(70,130,180), new Color(34,139,34), new Color(255,140,0),
                new Color(220,20,60), new Color(138,43,226), new Color(255,105,180),
                new Color(60,179,113), new Color(205,133,63), new Color(128,128,128)};

        for(int i=0; i<buttons.length; i++) {
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBackground(colors[i]);
            panel.add(buttons[i]);
        }

        add(panel, BorderLayout.CENTER);

        // Actions
        showDocs.addActionListener(e -> viewDocuments());
        uploadDoc.addActionListener(e -> createDocument());
        downloadDoc.addActionListener(e -> JOptionPane.showMessageDialog(this, "Download feature simulated."));
        deleteDoc.addActionListener(e -> deleteDocument());
        backupDocs.addActionListener(e -> JOptionPane.showMessageDialog(this, "Backup completed!"));
        replyDoc.addActionListener(e -> replyDocument());
        commentDoc.addActionListener(e -> commentDocument());
        manageUsers.addActionListener(e -> manageUsers());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void viewDocuments() {
        if(OfflineDocumentHub.documents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No documents available.");
            return;
        }
        StringBuilder sb = new StringBuilder("Documents:\n");
        OfflineDocumentHub.documents.keySet().forEach(doc -> sb.append("- ").append(doc).append("\n"));
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void createDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name:");
        if(docName == null || docName.isEmpty()) return;

        String content = JOptionPane.showInputDialog(this, "Enter content:");
        if(content == null) return;

        OfflineDocumentHub.documents.put(docName, new Document(docName, content));
        JOptionPane.showMessageDialog(this, "Document uploaded successfully!");
    }

    private void deleteDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to delete:");
        if(OfflineDocumentHub.documents.containsKey(docName)) {
            OfflineDocumentHub.documents.remove(docName);
            JOptionPane.showMessageDialog(this, "Document deleted!");
        } else {
            JOptionPane.showMessageDialog(this, "Document not found!");
        }
    }

    private void replyDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to reply to:");
        if(!OfflineDocumentHub.documents.containsKey(docName)) {
            JOptionPane.showMessageDialog(this, "Document not found!");
            return;
        }
        String reply = JOptionPane.showInputDialog(this, "Enter your reply:");
        if(reply != null) {
            OfflineDocumentHub.documents.get(docName).replies.put("Admin", reply);
            JOptionPane.showMessageDialog(this, "Reply added!");
        }
    }

    private void commentDocument() {
        String docName = JOptionPane.showInputDialog(this, "Enter document name to comment on:");
        if(!OfflineDocumentHub.documents.containsKey(docName)) {
            JOptionPane.showMessageDialog(this, "Document not found!");
            return;
        }
        String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
        if(comment != null) {
            OfflineDocumentHub.documents.get(docName).comments.add("Admin: " + comment);
            JOptionPane.showMessageDialog(this, "Comment added!");
        }
    }

    private void manageUsers() {
        StringBuilder sb = new StringBuilder("Registered Users:\n");
        OfflineDocumentHub.users.keySet().forEach(u -> sb.append("- ").append(u).append("\n"));
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}

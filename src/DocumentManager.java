import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*; // This imports List, ArrayList, HashMap, etc.
import javax.swing.*;
import java.awt.Desktop; // Import Desktop specifically

public class DocumentManager {
    private static final String DATA_ROOT = "./data";
    private static final SimpleDateFormat TS_FMT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public DocumentManager() {
        ensureDir(DATA_ROOT);
    }

    // Ensure directory exists
    private void ensureDir(String path) {
        File f = new File(path);
        if (!f.exists()) f.mkdirs();
    }

    // Ensure user folders exist
    public void ensureUser(String username) {
        ensureDir(DATA_ROOT + "/" + username + "/files");
        File req = new File(DATA_ROOT + "/" + username + "/requests.txt");
        try {
            if (!req.exists()) req.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Upload file (with confirmation handled in UI). Returns the stored filename path.
    public Path uploadFile(File src, String owner) throws IOException {
        ensureUser(owner);
        Path destDir = Paths.get(DATA_ROOT, owner, "files");
        String timestamp = TS_FMT.format(new Date());
        String safeName = timestamp + "_" + src.getName(); // avoid collision
        Path dest = destDir.resolve(safeName);
        Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        return dest;
    }

    // List files for a user (returns Path list)
    public java.util.List<Path> listFiles(String owner) {
        ensureUser(owner);
        java.util.List<Path> list = new ArrayList<>();
        Path dir = Paths.get(DATA_ROOT, owner, "files");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path p : ds) {
                if (Files.isRegularFile(p)) list.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // sort by name descending (newest first)
        list.sort(Comparator.comparing(Path::getFileName).reversed());
        return list;
    }

    // When someone requests access to a file owned by 'owner', we append a request
    public void createAccessRequest(String owner, String filename, String requester) throws IOException {
        ensureUser(owner);
        String ts = TS_FMT.format(new Date());
        String line = requester + "|" + filename + "|" + ts + "|PENDING";
        Path reqFile = Paths.get(DATA_ROOT, owner, "requests.txt");
        Files.write(reqFile, Collections.singletonList(line), StandardOpenOption.APPEND);
    }

    // Read pending requests for owner; returns lines (including APPROVED/DENIED if present)
    public java.util.List<String> readRequests(String owner) {
        ensureUser(owner);
        Path reqFile = Paths.get(DATA_ROOT, owner, "requests.txt");
        try {
            java.util.List<String> lines = Files.readAllLines(reqFile);
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Update a request line at index (approve=true to approve)
    // If approve -> copy the file from owner->files to requester->files
    public void processRequest(String owner, int requestIndex, boolean approve) throws IOException {
        ensureUser(owner);
        Path reqFile = Paths.get(DATA_ROOT, owner, "requests.txt");
        java.util.List<String> lines = Files.readAllLines(reqFile);
        if (requestIndex < 0 || requestIndex >= lines.size()) return;
        String[] parts = lines.get(requestIndex).split("\\|");
        if (parts.length < 4) return;
        String requester = parts[0];
        String filename = parts[1];
        String ts = parts[2];
        String status = parts[3];

        if (!"PENDING".equalsIgnoreCase(status)) {
            // already processed
            return;
        }

        String newStatus = approve ? "APPROVED" : "DENIED";
        parts[3] = newStatus;
        lines.set(requestIndex, String.join("|", parts));

        // write back
        Files.write(reqFile, lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        if (approve) {
            // copy file to requester folder
            ensureUser(requester);
            Path ownerFile = Paths.get(DATA_ROOT, owner, "files", filename);
            if (!Files.exists(ownerFile)) {
                // maybe owner renamed file — can't find; mark denied
                // update line to DENIED reason
                parts[3] = "DENIED_FILE_NOT_FOUND";
                lines.set(requestIndex, String.join("|", parts));
                Files.write(reqFile, lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                return;
            }
            Path dest = Paths.get(DATA_ROOT, requester, "files", filename);
            Files.copy(ownerFile, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Helper: parse a request line into a map
    public static Map<String, String> parseRequestLine(String line) {
        String[] parts = line.split("\\|");
        Map<String, String> m = new HashMap<>();
        m.put("requester", parts.length > 0 ? parts[0] : "");
        m.put("filename", parts.length > 1 ? parts[1] : "");
        m.put("timestamp", parts.length > 2 ? parts[2] : "");
        m.put("status", parts.length > 3 ? parts[3] : "");
        return m;
    }

    // Basic file open: simply attempt to open using Desktop (best-effort).
    public void openFileInDesktop(Path p) {
        if (p == null || !Files.exists(p)) {
            JOptionPane.showMessageDialog(null, "File not found: " + (p==null?"":p.toString()));
            return;
        }
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(p.toFile());
            } else {
                JOptionPane.showMessageDialog(null, "Opening files is not supported on this platform.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot open file: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot open file: Invalid file path");
        }
    }

    // Additional utility methods that might be needed
    public static void addDocument(String name, String owner, long size, String type) {
        // Mock implementation - in real app, this would save to database
        System.out.println("Document added: " + name + " by " + owner);
    }

    public static void shareDocumentWithUser(int docId, String username) {
        // Mock implementation
        System.out.println("Document " + docId + " shared with " + username);
    }

    public static String generateShareableLink(int docId) {
        // Mock implementation
        return "http://localhost:8080/share/doc/" + docId;
    }

    public static java.util.List<String> getAllDocuments() {
        // Mock implementation
        return new ArrayList<>();
    }
}
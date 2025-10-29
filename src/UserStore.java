import java.io.*;
import java.util.*;

public class UserStore {
    private static final String DATA_ROOT = "./data";
    private static final String USERS_FILE = DATA_ROOT + "/users.txt";
    private static final long MAX_STORAGE_BYTES = 16L * 1024 * 1024 * 1024; // 16GB per user
    private static Map<String, String> users = new HashMap<>();
    private static boolean loaded = false;

    static {
        loadUsers();
    }

    private static void ensureDir(String path) {
        File f = new File(path);
        if (!f.exists()) f.mkdirs();
    }

    private static void loadUsers() {
        if (loaded) return;

        users.clear();
        ensureDir(DATA_ROOT);
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Add default admin user
                users.put("admin", "admin");
                saveUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loaded = true;
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
    }

    private static void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String username, String password) {
        loadUsers();
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, password);
        saveUsers();

        // Create user directory
        File userDir = new File(DATA_ROOT + "/" + username + "/files");
        if (!userDir.exists()) userDir.mkdirs();

        return true;
    }

    public static boolean validateUser(String username, String password) {
        loadUsers();
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public static java.util.List<String> getAllUsernames() {
        loadUsers();
        return new ArrayList<>(users.keySet());
    }

    public static boolean isAdmin(String username) {
        return "admin".equals(username);
    }

    public static long getAvailableStorage(String username) {
        File userDir = new File(DATA_ROOT + "/" + username + "/files");
        if (!userDir.exists()) {
            return MAX_STORAGE_BYTES;
        }
        long used = getDirectorySize(userDir);
        return Math.max(0, MAX_STORAGE_BYTES - used);
    }

    public static long getUsedStorage(String username) {
        File userDir = new File(DATA_ROOT + "/" + username + "/files");
        if (!userDir.exists()) return 0;
        return getDirectorySize(userDir);
    }

    public static long getUserStorageLimit(String username) {
        return MAX_STORAGE_BYTES;
    }

    private static long getDirectorySize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }
}
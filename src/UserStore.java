import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class UserStore {
    private static final Map<String, String> users = new ConcurrentHashMap<>();
    private static final Map<String, String> userRoles = new ConcurrentHashMap<>();
    private static final Map<String, Long> userStorage = new ConcurrentHashMap<>();
    private static final long DEFAULT_STORAGE_LIMIT = 16 * 1024 * 1024 * 1024L; // 16GB in bytes

    static {
        // sample default accounts
        users.put("admin", "admin");
        users.put("client1", "client1");
        users.put("client2", "client2");

        // roles: "admin" or "client"
        userRoles.put("admin", "admin");
        userRoles.put("client1", "client");
        userRoles.put("client2", "client");

        // initialize storage
        userStorage.put("admin", DEFAULT_STORAGE_LIMIT);
        userStorage.put("client1", DEFAULT_STORAGE_LIMIT);
        userStorage.put("client2", DEFAULT_STORAGE_LIMIT);
    }

    // returns true if user added successfully, false if username exists
    public static synchronized boolean addUser(String username, String password) {
        if (username == null || username.isEmpty() || password == null) return false;
        if (users.containsKey(username)) return false;
        users.put(username, password);
        userRoles.put(username, "client"); // default role is client
        userStorage.put(username, DEFAULT_STORAGE_LIMIT);
        return true;
    }

    public static boolean validateUser(String username, String password) {
        if (username == null || password == null) return false;
        return password.equals(users.get(username));
    }

    public static String getUserRole(String username) {
        return userRoles.get(username);
    }

    public static long getUserStorageLimit(String username) {
        return userStorage.getOrDefault(username, DEFAULT_STORAGE_LIMIT);
    }

    public static long getUsedStorage(String username) {
        // In a real implementation, this would calculate actual used storage
        // For now, return a mock value
        return 2 * 1024 * 1024 * 1024L; // 2GB used
    }

    public static long getAvailableStorage(String username) {
        return getUserStorageLimit(username) - getUsedStorage(username);
    }

    public static boolean isAdmin(String username) {
        return "admin".equals(getUserRole(username));
    }

    // NEW: Get all usernames for notification system
    public static Set<String> getAllUsernames() {
        return new HashSet<>(users.keySet());
    }
}
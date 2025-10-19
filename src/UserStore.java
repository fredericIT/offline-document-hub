import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class UserStore {
    private static final Map<String, String> users = new ConcurrentHashMap<>();

    static {
        // sample default account (username: admin, password: admin)
        users.put("admin", "admin");
    }

    // returns true if user added successfully, false if username exists
    public static synchronized boolean addUser(String username, String password) {
        if (username == null || username.isEmpty() || password == null) return false;
        if (users.containsKey(username)) return false;
        users.put(username, password);
        return true;
    }

    public static boolean validateUser(String username, String password) {
        if (username == null || password == null) return false;
        return password.equals(users.get(username));
    }
}

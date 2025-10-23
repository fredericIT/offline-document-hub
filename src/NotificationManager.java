import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationManager {
    private static final Map<String, List<Notification>> userNotifications = new ConcurrentHashMap<>();

    public static class Notification {
        public String id;
        public String title;
        public String message;
        public Date date;
        public boolean read;
        public String type; // "upload", "approval", "system"

        public Notification(String title, String message, String type) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.message = message;
            this.date = new Date();
            this.read = false;
            this.type = type;
        }
    }

    public static void notifyAllUsers(String title, String message, String type) {
        // FIXED: Use the getter method instead of accessing private field directly
        for (String username : UserStore.getAllUsernames()) {
            addNotification(username, title, message, type);
        }
    }

    public static void addNotification(String username, String title, String message, String type) {
        Notification notification = new Notification(title, message, type);
        userNotifications.computeIfAbsent(username, k -> new ArrayList<>()).add(0, notification); // Add to beginning
    }

    public static List<Notification> getUserNotifications(String username) {
        return userNotifications.getOrDefault(username, new ArrayList<>());
    }

    public static int getUnreadCount(String username) {
        List<Notification> notifications = getUserNotifications(username);
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.read) {
                count++;
            }
        }
        return count;
    }

    public static void markAsRead(String username, String notificationId) {
        List<Notification> notifications = getUserNotifications(username);
        for (Notification notification : notifications) {
            if (notification.id.equals(notificationId)) {
                notification.read = true;
                break;
            }
        }
    }

    public static void markAllAsRead(String username) {
        List<Notification> notifications = getUserNotifications(username);
        for (Notification notification : notifications) {
            notification.read = true;
        }
    }
}
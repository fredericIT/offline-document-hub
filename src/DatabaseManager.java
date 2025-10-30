import java.sql.*;
import java.util.*;
import oracle.jdbc.pool.OracleDataSource;

public class DatabaseManager {
    private static Connection connection;
    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String DB_USER = "document_hub";
    private static final String DB_PASSWORD = "hub123";

    public static void initialize() {
        try {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(DB_URL);
            ds.setUser(DB_USER);
            ds.setPassword(DB_PASSWORD);
            connection = ds.getConnection();
            createTablesIfNotExists();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    private static void createTablesIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            // Users table
            stmt.execute(
                    "CREATE TABLE users (" +
                            "username VARCHAR2(50) PRIMARY KEY, " +
                            "password VARCHAR2(100) NOT NULL, " +
                            "is_admin NUMBER(1) DEFAULT 0, " +
                            "created_date DATE DEFAULT SYSDATE)"
            );

            // Documents table
            stmt.execute(
                    "CREATE TABLE documents (" +
                            "doc_id NUMBER PRIMARY KEY, " +
                            "doc_name VARCHAR2(255) NOT NULL, " +
                            "owner VARCHAR2(50) NOT NULL, " +
                            "file_size NUMBER, " +
                            "file_type VARCHAR2(50), " +
                            "upload_date DATE DEFAULT SYSDATE, " +
                            "file_path VARCHAR2(500), " +
                            "cloud_synced NUMBER(1) DEFAULT 0, " +
                            "FOREIGN KEY (owner) REFERENCES users(username))"
            );

            // Document shares table
            stmt.execute(
                    "CREATE TABLE document_shares (" +
                            "share_id NUMBER PRIMARY KEY, " +
                            "doc_id NUMBER NOT NULL, " +
                            "shared_with VARCHAR2(50) NOT NULL, " +
                            "shared_by VARCHAR2(50) NOT NULL, " +
                            "share_date DATE DEFAULT SYSDATE, " +
                            "permission_level VARCHAR2(20) DEFAULT 'VIEW', " +
                            "FOREIGN KEY (doc_id) REFERENCES documents(doc_id), " +
                            "FOREIGN KEY (shared_with) REFERENCES users(username))"
            );

            // Access requests table
            stmt.execute(
                    "CREATE TABLE access_requests (" +
                            "request_id NUMBER PRIMARY KEY, " +
                            "doc_id NUMBER NOT NULL, " +
                            "requester VARCHAR2(50) NOT NULL, " +
                            "request_date DATE DEFAULT SYSDATE, " +
                            "status VARCHAR2(20) DEFAULT 'PENDING', " +
                            "FOREIGN KEY (doc_id) REFERENCES documents(doc_id), " +
                            "FOREIGN KEY (requester) REFERENCES users(username))"
            );

            // Cloud sync log table
            stmt.execute(
                    "CREATE TABLE cloud_sync_log (" +
                            "sync_id NUMBER PRIMARY KEY, " +
                            "doc_id NUMBER NOT NULL, " +
                            "sync_date DATE DEFAULT SYSDATE, " +
                            "sync_type VARCHAR2(20), " +
                            "status VARCHAR2(20), " +
                            "cloud_provider VARCHAR2(50), " +
                            "FOREIGN KEY (doc_id) REFERENCES documents(doc_id))"
            );

            // Insert default admin user
            stmt.execute(
                    "INSERT INTO users (username, password, is_admin) VALUES " +
                            "('admin', 'admin', 1)"
            );

        } catch (SQLException e) {
            if (e.getErrorCode() != 955) { // Table already exists
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initialize();
        }
        return connection;
    }

    public static boolean validateUser(String username, String password) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT username FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addUser(String username, String password) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAdmin(String username) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT is_admin FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("is_admin") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = "jdbc:sqlite:students.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("✅ SQLite driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ SQLite JDBC driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
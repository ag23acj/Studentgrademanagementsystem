package util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static final String DB_FOLDER = "data";
    private static final String DB_PATH = DB_FOLDER + "/sgms.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        try {
            Files.createDirectories(Path.of(DB_FOLDER));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DB folder", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
}
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {


    public static void initialize() {
        initializeDatabase();
    }

    private static final File CSV_FILE = new File(System.getProperty("user.dir"), "students.csv");
    private static boolean initialized = false;

    private static void initializeDatabase() {
        if (initialized) return;

        String createStudentsTable = """
                CREATE TABLE IF NOT EXISTS students (
                    student_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,

                    module1_name TEXT NOT NULL,
                    sub1 REAL NOT NULL,
                    sub1_status TEXT NOT NULL,

                    module2_name TEXT NOT NULL,
                    sub2 REAL NOT NULL,
                    sub2_status TEXT NOT NULL,

                    module3_name TEXT NOT NULL,
                    sub3 REAL NOT NULL,
                    sub3_status TEXT NOT NULL,

                    upgrade_approved INTEGER NOT NULL DEFAULT 0
                );
                """;

        String createBackupTable = """
                CREATE TABLE IF NOT EXISTS students_backup (
                    student_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,

                    module1_name TEXT NOT NULL,
                    sub1 REAL NOT NULL,
                    sub1_status TEXT NOT NULL,

                    module2_name TEXT NOT NULL,
                    sub2 REAL NOT NULL,
                    sub2_status TEXT NOT NULL,

                    module3_name TEXT NOT NULL,
                    sub3 REAL NOT NULL,
                    sub3_status TEXT NOT NULL,

                    upgrade_approved INTEGER NOT NULL DEFAULT 0
                );
                """;

                     String createUsersTable = """
                     CREATE TABLE IF NOT EXISTS users (
                     username TEXT PRIMARY KEY,
                     password TEXT NOT NULL,
                     role TEXT NOT NULL
                );
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createStudentsTable);
            stmt.execute(createBackupTable);
            stmt.execute(createUsersTable);

            ensureDefaultUsers(conn);

            initialized = true;
            migrateCsvToDatabaseIfNeeded(conn);

            System.out.println("✅ Database initialized.");

        } catch (SQLException e) {
            System.out.println("❌ Database init error: " + e.getMessage());
        }
    }

    private static void ensureDefaultUsers(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            int count = rs.next() ? rs.getInt(1) : 0;

            if (count == 0) {
                String insertSql = """
                    INSERT INTO users(username, password, role)
                    VALUES
                    ('admin', 'admin123', 'ADMIN'),
                    ('tutor', 'tutor123', 'TUTOR')
                    """;

                stmt.executeUpdate(insertSql);
                System.out.println("✅ Default users created.");
            }

        } catch (SQLException e) {
            System.out.println("❌ User init error: " + e.getMessage());
        }
    }

    private static void migrateCsvToDatabaseIfNeeded(Connection conn) {
        if (!CSV_FILE.exists()) return;

        String countSql = "SELECT COUNT(*) FROM students";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            int count = rs.next() ? rs.getInt(1) : 0;
            if (count > 0) return; // already migrated

        } catch (SQLException e) {
            System.out.println("❌ Count check error: " + e.getMessage());
            return;
        }

        List<Student> csvStudents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 11) continue;

                String id = p[0].trim();
                String name = p[1].trim();

                String m1 = p[2].trim();
                double s1 = Double.parseDouble(p[3].trim());
                ExamStatus st1 = parseStatus(p[4]);

                String m2 = p[5].trim();
                double s2 = Double.parseDouble(p[6].trim());
                ExamStatus st2 = parseStatus(p[7]);

                String m3 = p[8].trim();
                double s3 = Double.parseDouble(p[9].trim());
                ExamStatus st3 = parseStatus(p[10]);

                boolean approved = false;
                if (p.length >= 12) {
                    approved = Boolean.parseBoolean(p[11].trim());
                }

                Student student = new Student(
                        id, name,
                        m1, s1, st1,
                        m2, s2, st2,
                        m3, s3, st3
                );

                student.setUpgradeApproved(approved);
                student.applyApprovedUpgrade();

                csvStudents.add(student);
            }

            insertStudentsDirect(conn, csvStudents);
            System.out.println("✅ CSV data migrated to SQLite.");

        } catch (Exception e) {
            System.out.println("❌ CSV migration error: " + e.getMessage());
        }
    }

    private static void insertStudentsDirect(Connection conn, List<Student> students) throws SQLException {
        String deleteSql = "DELETE FROM students";
        String insertSql = """
                INSERT INTO students (
                    student_id, name,
                    module1_name, sub1, sub1_status,
                    module2_name, sub2, sub2_status,
                    module3_name, sub3, sub3_status,
                    upgrade_approved
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        conn.setAutoCommit(false);

        try (Statement deleteStmt = conn.createStatement()) {
            deleteStmt.executeUpdate(deleteSql);
        }

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (Student s : students) {
                ps.setString(1, s.getStudentId());
                ps.setString(2, s.getName());

                ps.setString(3, s.getModule1Name());
                ps.setDouble(4, s.getSub1());
                ps.setString(5, s.getSub1Status().name());

                ps.setString(6, s.getModule2Name());
                ps.setDouble(7, s.getSub2());
                ps.setString(8, s.getSub2Status().name());

                ps.setString(9, s.getModule3Name());
                ps.setDouble(10, s.getSub3());
                ps.setString(11, s.getSub3Status().name());

                ps.setInt(12, s.isUpgradeApproved() ? 1 : 0);

                ps.addBatch();
            }

            ps.executeBatch();
        }

        conn.commit();
    }

    public static void saveStudents(List<Student> students) {
        initializeDatabase();

        try (Connection conn = DBConnection.getConnection()) {
            insertStudentsDirect(conn, students);
            System.out.println("✅ Students saved to database.");
        } catch (SQLException e) {
            System.out.println("❌ Save error: " + e.getMessage());
        }
    }

    public static List<Student> loadStudents() {
        initializeDatabase();

        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT
                    student_id, name,
                    module1_name, sub1, sub1_status,
                    module2_name, sub2, sub2_status,
                    module3_name, sub3, sub3_status,
                    upgrade_approved
                FROM students
                ORDER BY student_id
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("student_id");
                String name = rs.getString("name");

                String m1 = rs.getString("module1_name");
                double s1 = rs.getDouble("sub1");
                ExamStatus st1 = parseStatus(rs.getString("sub1_status"));

                String m2 = rs.getString("module2_name");
                double s2 = rs.getDouble("sub2");
                ExamStatus st2 = parseStatus(rs.getString("sub2_status"));

                String m3 = rs.getString("module3_name");
                double s3 = rs.getDouble("sub3");
                ExamStatus st3 = parseStatus(rs.getString("sub3_status"));

                boolean approved = rs.getInt("upgrade_approved") == 1;

                Student student = new Student(
                        id, name,
                        m1, s1, st1,
                        m2, s2, st2,
                        m3, s3, st3
                );

                student.setUpgradeApproved(approved);
                student.applyApprovedUpgrade();

                students.add(student);
            }

        } catch (SQLException e) {
            System.out.println("❌ Load error: " + e.getMessage());
        }

        return students;
    }

    public static void backupStudents() {
        initializeDatabase();

        String clearBackup = "DELETE FROM students_backup";
        String copyToBackup = """
                INSERT INTO students_backup (
                    student_id, name,
                    module1_name, sub1, sub1_status,
                    module2_name, sub2, sub2_status,
                    module3_name, sub3, sub3_status,
                    upgrade_approved
                )
                SELECT
                    student_id, name,
                    module1_name, sub1, sub1_status,
                    module2_name, sub2, sub2_status,
                    module3_name, sub3, sub3_status,
                    upgrade_approved
                FROM students
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);
            stmt.executeUpdate(clearBackup);
            stmt.executeUpdate(copyToBackup);
            conn.commit();

            System.out.println("💾 Backup created in database.");

        } catch (SQLException e) {
            System.out.println("❌ Backup error: " + e.getMessage());
        }
    }

    public static boolean restoreBackup() {
        initializeDatabase();

        String backupCountSql = "SELECT COUNT(*) FROM students_backup";
        String clearStudents = "DELETE FROM students";
        String restoreSql = """
                INSERT INTO students (
                    student_id, name,
                    module1_name, sub1, sub1_status,
                    module2_name, sub2, sub2_status,
                    module3_name, sub3, sub3_status,
                    upgrade_approved
                )
                SELECT
                    student_id, name,
                    module1_name, sub1, sub1_status,
                    module2_name, sub2, sub2_status,
                    module3_name, sub3, sub3_status,
                    upgrade_approved
                FROM students_backup
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(backupCountSql)) {

                int count = rs.next() ? rs.getInt(1) : 0;
                if (count == 0) return false;
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(clearStudents);
                stmt.executeUpdate(restoreSql);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Restore error: " + e.getMessage());
            return false;
        }
    }

    private static ExamStatus parseStatus(String value) {
        try {
            return ExamStatus.valueOf(value.trim());
        } catch (Exception e) {
            return ExamStatus.FIRST_SITTING;
        }
    }
}
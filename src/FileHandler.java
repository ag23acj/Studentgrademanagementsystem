import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Always save & load from the SAME place
    private static final File FILE =
            new File(System.getProperty("user.dir"), "students.csv");

    private static final File BACKUP_FILE =
            new File(System.getProperty("user.dir"), "students_backup.csv");

    // ================= SAVE =================
    public static void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {

            for (Student s : students) {
                writer.println(
                        s.getStudentId() + "," +
                                s.getName() + "," +

                                s.getModule1Name() + "," +
                                s.getSub1() + "," +
                                s.getSub1Status() + "," +

                                s.getModule2Name() + "," +
                                s.getSub2() + "," +
                                s.getSub2Status() + "," +

                                s.getModule3Name() + "," +
                                s.getSub3() + "," +
                                s.getSub3Status()
                );
            }

            System.out.println("✅ Students saved to: " + FILE.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("❌ Save error: " + e.getMessage());
        }
    }

    // ================= LOAD =================
    public static List<Student> loadStudents() {

        List<Student> students = new ArrayList<>();

        if (!FILE.exists()) {
            System.out.println("ℹ students.csv not found yet.");
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] p = line.split(",");

                // Expecting 11 columns
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

                students.add(
                        new Student(id, name,
                                m1, s1, st1,
                                m2, s2, st2,
                                m3, s3, st3)
                );
            }

            System.out.println("📂 Students loaded from: " + FILE.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("❌ Load error: " + e.getMessage());
        }

        return students;
    }

    // ================= BACKUP =================
    public static void backupStudents() {

        if (!FILE.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE));
             PrintWriter writer = new PrintWriter(new FileWriter(BACKUP_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

            System.out.println("💾 Backup created.");

        } catch (IOException e) {
            System.out.println("❌ Backup failed: " + e.getMessage());
        }
    }

    // ================= RESTORE =================
    public static boolean restoreBackup() {

        if (!BACKUP_FILE.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(BACKUP_FILE));
             PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

            System.out.println("🔁 Backup restored.");
            return true;

        } catch (IOException e) {
            System.out.println("❌ Restore failed: " + e.getMessage());
            return false;
        }
    }

    // ================= SAFE ENUM PARSER =================
    private static ExamStatus parseStatus(String value) {

        try {
            return ExamStatus.valueOf(value.trim());
        } catch (Exception e) {
            // Default for old CSV values like "NORMAL"
            return ExamStatus.FIRST_SITTING;
        }
    }
}

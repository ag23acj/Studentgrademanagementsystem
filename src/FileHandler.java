import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final File FILE = new File(System.getProperty("user.dir"), "students.csv");
    private static final File BACKUP_FILE = new File(System.getProperty("user.dir"), "students_backup.csv");

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
                                s.getSub3Status() + "," +

                                s.isUpgradeApproved()
                );
            }

            System.out.println("✅ Saved: " + FILE.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("❌ Save error: " + e.getMessage());
        }
    }

    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();

        if (!FILE.exists()) return students;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {

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

                Student student = new Student(id, name,
                        m1, s1, st1,
                        m2, s2, st2,
                        m3, s3, st3
                );

                student.setUpgradeApproved(approved);
                student.applyApprovedUpgrade();

                students.add(student);
            }

        } catch (Exception e) {
            System.out.println("❌ Load error: " + e.getMessage());
        }

        return students;
    }

    public static void backupStudents() {
        if (!FILE.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE));
             PrintWriter writer = new PrintWriter(new FileWriter(BACKUP_FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

            System.out.println("💾 Backup created.");

        } catch (Exception e) {
            System.out.println("❌ Backup error: " + e.getMessage());
        }
    }

    public static boolean restoreBackup() {
        if (!BACKUP_FILE.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(BACKUP_FILE));
             PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

            return true;

        } catch (Exception e) {
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

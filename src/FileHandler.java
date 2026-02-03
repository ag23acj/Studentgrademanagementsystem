import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static ExamStatus parseStatus(String raw) {
        if (raw == null) return ExamStatus.FIRST_SITTING;

        String s = raw.trim().toUpperCase();

        // handle old values or friendly labels
        if (s.equals("NORMAL")) return ExamStatus.FIRST_SITTING;
        if (s.equals("FIRST SITTING")) return ExamStatus.FIRST_SITTING;
        if (s.equals("FIRSTSITTING")) return ExamStatus.FIRST_SITTING;
        if (s.equals("FIRST_SITTING")) return ExamStatus.FIRST_SITTING;

        if (s.equals("ABSENT") || s.equals("AB")) return ExamStatus.ABSENT;

        if (s.equals("RESIT") || s.equals("REPEAT")) return ExamStatus.RESIT;

        // default fallback
        return ExamStatus.FIRST_SITTING;
    }


    private static final String FILE_NAME = "students.csv";

    public static void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                writer.println(
                        s.getStudentId() + "," +
                                s.getName() + "," +
                                s.getSub1() + "," + s.getSub1Status().name() + "," +
                                s.getSub2() + "," + s.getSub2Status().name() + "," +
                                s.getSub3() + "," + s.getSub3Status().name()
                );

            }

            System.out.println("✅ Students saved successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error saving file: " + e.getMessage());
        }
    }

    public static void backupStudents() {
        File source = new File(FILE_NAME);
        File backup = new File("students_backup.csv");

        if (!source.exists()) return; // nothing to back up

        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             PrintWriter writer = new PrintWriter(new FileWriter(backup))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

        } catch (IOException e) {
            System.out.println("❌ Backup failed: " + e.getMessage());
        }
    }

    public static boolean restoreBackup() {
        File backup = new File("students_backup.csv");
        File target = new File(FILE_NAME);

        if (!backup.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(backup));
             PrintWriter writer = new PrintWriter(new FileWriter(target))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
            return true;

        } catch (IOException e) {
            System.out.println("❌ Restore failed: " + e.getMessage());
            return false;
        }
    }


    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 8) {
                    String studentId = parts[0].trim();
                    String name = parts[1].trim();

                    double s1 = Double.parseDouble(parts[2].trim());
                    ExamStatus st1 = parseStatus(parts[3]);

                    double s2 = Double.parseDouble(parts[4].trim());
                    ExamStatus st2 = parseStatus(parts[5]);

                    double s3 = Double.parseDouble(parts[6].trim());
                    ExamStatus st3 = parseStatus(parts[7]);

                    students.add(new Student(studentId, name, s1, st1, s2, st2, s3, st3));
                }

            }
            System.out.println("📂 Students loaded successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error loading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ CSV contains invalid number format: " + e.getMessage());
        }

        return students;
    }
}

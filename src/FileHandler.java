import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String FILE_NAME = "students.csv";

    public static void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                writer.println(s.getStudentId() + "," +
                        s.getName() + "," +
                        s.getSub1() + "," +
                        s.getSub2() + "," +
                        s.getSub3());
            }
            System.out.println("✅ Students saved successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error saving file: " + e.getMessage());
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

                if (parts.length == 5) {
                    String studentId = parts[0].trim();
                    String name = parts[1].trim();
                    double s1 = Double.parseDouble(parts[2].trim());
                    double s2 = Double.parseDouble(parts[3].trim());
                    double s3 = Double.parseDouble(parts[4].trim());

                    students.add(new Student(studentId, name, s1, s2, s3));
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

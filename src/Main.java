import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== Student Grade Management System ===");

        List<Student> students = FileHandler.loadStudents();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice == 1) {
                addStudent(students, scanner);
            } else if (choice == 2) {
                viewStudents(students);
            } else if (choice == 3) {
                searchStudent(students, scanner);
            } else if (choice == 4) {
                deleteStudent(students, scanner);
            } else if (choice == 5) {
                FileHandler.saveStudents(students);
                System.out.println("Exiting... Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please select 1 to 5.");
            }
        }

        scanner.close();
    }

    private static void addStudent(List<Student> students, Scanner scanner) {

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        double s1 = readMark(scanner, "Subject 1");
        double s2 = readMark(scanner, "Subject 2");
        double s3 = readMark(scanner, "Subject 3");

        Student s = new Student(studentId, name, s1, s2, s3);
        students.add(s);

        System.out.println("✅ Student added successfully!");
    }

    private static double readMark(Scanner scanner, String subjectName) {
        while (true) {
            System.out.print("Enter mark for " + subjectName + " (0-100): ");
            String input = scanner.nextLine().trim();
            try {
                double mark = Double.parseDouble(input);
                if (mark < 0 || mark > 100) {
                    System.out.println("Mark must be between 0 and 100.");
                } else {
                    return mark;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void viewStudents(List<Student> students) {

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.printf("%-10s %-15s %7s %7s %7s %8s %-18s%n",
                "ID", "Name", "Sub1", "Sub2", "Sub3", "Average", "Class");
        System.out.println("----------------------------------------------------------------------------");

        for (Student s : students) {
            s.display();
        }
    }

    private static void searchStudent(List<Student> students, Scanner scanner) {

        if (students.isEmpty()) {
            System.out.println("No students available to search.");
            return;
        }

        System.out.print("Enter Student ID to search: ");
        String id = scanner.nextLine().trim();

        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(id)) {

                System.out.printf("%-10s %-15s %7s %7s %7s %8s %-18s%n",
                        "ID", "Name", "Sub1", "Sub2", "Sub3", "Average", "Class");
                System.out.println("----------------------------------------------------------------------------");

                s.display();
                return;
            }
        }

        System.out.println("❌ Student with ID " + id + " not found.");
    }

    private static void deleteStudent(List<Student> students, Scanner scanner) {

        if (students.isEmpty()) {
            System.out.println("No students available to delete.");
            return;
        }

        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equalsIgnoreCase(id)) {
                students.remove(i);
                System.out.println("✅ Student deleted successfully.");
                return;
            }
        }

        System.out.println("❌ Student with ID " + id + " not found.");
    }
}

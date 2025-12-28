import java.util.ArrayList;
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
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice == 1) {
                addStudent(students, scanner);
            }
            else if (choice == 2) {
                viewStudents(students);
            }
            else if (choice == 3) {
                FileHandler.saveStudents(students);
                System.out.println("Exiting... Goodbye!");
                break;
            }
            else {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }

        }

        scanner.close();
    }

    private static void addStudent(List<Student> students, Scanner scanner) {
        System.out.print("Enter Student Id: ");
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

        System.out.printf("%-8s %-15s %7s %7s %7s %8s %s%n",
                "ID", "Name", "Sub1", "Sub2", "Sub3", "Average", "G");
        System.out.println("------------------------------------------------------------------");

        for (Student s : students) {
            s.display();
        }
    }
}

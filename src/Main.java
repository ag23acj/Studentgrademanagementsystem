import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("Student Grade Management System\n");

        List<Student> students = new ArrayList<>();

        // Hard-coded sample students for testing
        Student s1 = new Student("1", "Anjana", 90, 85, 88);
        Student s2 = new Student("2", "Rahul", 70, 65, 75);

        students.add(s1);
        students.add(s2);

        // Print header
        System.out.printf("%-8s %-15s %7s %7s %7s %8s %s%n",
                "RollNo", "Name", "Sub1", "Sub2", "Sub3", "Average", "G");
        System.out.println("------------------------------------------------------------------");

        // Print all students
        for (Student s : students) {
            s.display();
        }
    }
}

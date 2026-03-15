import java.util.ArrayList;
import java.util.List;

public class StudentService {

    public List<Student> getAllStudents() {
        return FileHandler.loadStudents();
    }

    public void saveAllStudents(List<Student> students) {
        FileHandler.saveStudents(students);
    }

    public Student findStudentById(String studentId) {
        List<Student> students = FileHandler.loadStudents();

        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {
                return s;
            }
        }
        return null;
    }

    public boolean addStudent(Student student) {
        List<Student> students = FileHandler.loadStudents();

        for (Student s : students) {
            if (s.getStudentId().equals(student.getStudentId())) {
                return false; // duplicate ID
            }
        }

        students.add(student);
        FileHandler.saveStudents(students);
        return true;
    }

    public boolean deleteStudent(String studentId) {
        List<Student> students = FileHandler.loadStudents();
        Student found = null;

        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {
                found = s;
                break;
            }
        }

        if (found == null) {
            return false;
        }

        students.remove(found);
        FileHandler.saveStudents(students);
        return true;
    }

    public boolean updateStudent(String studentId, Student updatedStudent) {
        List<Student> students = FileHandler.loadStudents();

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(studentId)) {
                students.set(i, updatedStudent);
                FileHandler.saveStudents(students);
                return true;
            }
        }

        return false;
    }

    public boolean approveUpgrade(String studentId) {
        List<Student> students = FileHandler.loadStudents();

        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {
                if (!s.isBorderline() || s.isUpgradeApproved()) {
                    return false;
                }

                s.setUpgradeApproved(true);
                s.applyApprovedUpgrade();
                FileHandler.saveStudents(students);
                return true;
            }
        }

        return false;
    }

    public List<Student> getAtRiskStudents() {
        List<Student> students = FileHandler.loadStudents();
        List<Student> atRiskStudents = new ArrayList<>();

        for (Student s : students) {
            if (RiskUtils.isAtRisk(s)) {
                atRiskStudents.add(s);
            }
        }

        return atRiskStudents;
    }

    public void backupStudents() {
        FileHandler.backupStudents();
    }

    public boolean restoreBackup() {
        return FileHandler.restoreBackup();
    }
}
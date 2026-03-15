public class RiskUtilsTest {

    public static void main(String[] args) {
        testAbsentStudentAtRisk();
        testResitStudentAtRisk();
        testLowAverageAtRisk();
        testSafeStudentNotAtRisk();
    }

    private static void testAbsentStudentAtRisk() {
        Student s = new Student(
                "1001", "Test Student",
                "Programming", 70, ExamStatus.ABSENT,
                "Database", 65, ExamStatus.FIRST_SITTING,
                "SE", 75, ExamStatus.FIRST_SITTING
        );

        printResult("Absent student at risk", RiskUtils.isAtRisk(s));
    }

    private static void testResitStudentAtRisk() {
        Student s = new Student(
                "1002", "Test Student",
                "Programming", 35, ExamStatus.FIRST_SITTING,
                "Database", 65, ExamStatus.FIRST_SITTING,
                "SE", 75, ExamStatus.FIRST_SITTING
        );

        printResult("Resit student at risk", RiskUtils.isAtRisk(s));
    }

    private static void testLowAverageAtRisk() {
        Student s = new Student(
                "1003", "Test Student",
                "Programming", 42, ExamStatus.FIRST_SITTING,
                "Database", 44, ExamStatus.FIRST_SITTING,
                "SE", 46, ExamStatus.FIRST_SITTING
        );

        printResult("Low average at risk", RiskUtils.isAtRisk(s));
    }

    private static void testSafeStudentNotAtRisk() {
        Student s = new Student(
                "1004", "Test Student",
                "Programming", 68, ExamStatus.FIRST_SITTING,
                "Database", 70, ExamStatus.FIRST_SITTING,
                "SE", 72, ExamStatus.FIRST_SITTING
        );

        printResult("Safe student not at risk", !RiskUtils.isAtRisk(s));
    }

    private static void printResult(String testName, boolean passed) {
        if (passed) {
            System.out.println("PASS: " + testName);
        } else {
            System.out.println("FAIL: " + testName);
        }
    }
}
public class GradeUtilsTest {

    public static void main(String[] args) {
        testAverageCalculation();
        testDegreeClassification();
        testFailOnAbsence();
        testResitRequired();
        testBorderlineDetection();
        testUpgradeLogic();
    }

    private static void testAverageCalculation() {
        double avg = GradeUtils.calculateAverage(60, 70, 80);
        printResult("Average calculation", avg == 70.0);
    }

    private static void testDegreeClassification() {
        printResult("First classification",
                GradeUtils.calculateDegreeClass(72).equals("First (1st)"));

        printResult("Upper Second classification",
                GradeUtils.calculateDegreeClass(65).equals("Upper Second (2:1)"));

        printResult("Lower Second classification",
                GradeUtils.calculateDegreeClass(55).equals("Lower Second (2:2)"));

        printResult("Third classification",
                GradeUtils.calculateDegreeClass(45).equals("Third (3rd)"));

        printResult("Fail classification",
                GradeUtils.calculateDegreeClass(35).equals("Fail"));
    }

    private static void testFailOnAbsence() {
        String result = GradeUtils.calculateFinalOutcome(
                70, 65, 80,
                ExamStatus.FIRST_SITTING,
                ExamStatus.ABSENT,
                ExamStatus.FIRST_SITTING
        );

        printResult("Fail on absence", result.equals("Fail"));
    }

    private static void testResitRequired() {
        String result = GradeUtils.calculateFinalOutcome(
                70, 35, 80,
                ExamStatus.FIRST_SITTING,
                ExamStatus.FIRST_SITTING,
                ExamStatus.FIRST_SITTING
        );

        printResult("Resit required", result.equals("Resit Required"));
    }

    private static void testBorderlineDetection() {
        printResult("Borderline true", GradeUtils.isBorderline(69.5));
        printResult("Borderline false", !GradeUtils.isBorderline(67.0));
    }

    private static void testUpgradeLogic() {
        printResult("Upgrade Third to 2:2",
                GradeUtils.upgradeOneLevel("Third (3rd)").equals("Lower Second (2:2)"));

        printResult("Upgrade 2:2 to 2:1",
                GradeUtils.upgradeOneLevel("Lower Second (2:2)").equals("Upper Second (2:1)"));

        printResult("Upgrade 2:1 to 1st",
                GradeUtils.upgradeOneLevel("Upper Second (2:1)").equals("First (1st)"));
    }

    private static void printResult(String testName, boolean passed) {
        if (passed) {
            System.out.println("PASS: " + testName);
        } else {
            System.out.println("FAIL: " + testName);
        }
    }
}
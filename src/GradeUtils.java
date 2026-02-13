public class GradeUtils {

    private static final double PASS_MARK = 40.0;
    private static final double BORDERLINE_RANGE = 1.0; // within 1 mark of boundary

    public static double calculateAverage(double s1, double s2, double s3) {
        return (s1 + s2 + s3) / 3.0;
    }

    public static String calculateFinalOutcome(
            double s1, double s2, double s3,
            ExamStatus st1, ExamStatus st2, ExamStatus st3
    ) {
        // If any module ABSENT -> fail
        if (st1 == ExamStatus.ABSENT || st2 == ExamStatus.ABSENT || st3 == ExamStatus.ABSENT) {
            return "Fail";
        }

        // If any mark < 40 -> resit required
        if (s1 < PASS_MARK || s2 < PASS_MARK || s3 < PASS_MARK) {
            return "Resit Required";
        }

        double avg = calculateAverage(s1, s2, s3);
        return calculateDegreeClass(avg);
    }

    public static String calculateDegreeClass(double avg) {
        if (avg >= 70) return "First (1st)";
        if (avg >= 60) return "Upper Second (2:1)";
        if (avg >= 50) return "Lower Second (2:2)";
        if (avg >= 40) return "Third (3rd)";
        return "Fail";
    }

    // ✅ borderline means avg within 1 mark of boundaries
    public static boolean isBorderline(double avg) {
        double[] boundaries = {40, 50, 60, 70};
        for (double b : boundaries) {
            if (Math.abs(avg - b) <= BORDERLINE_RANGE) return true;
        }
        return false;
    }

    // ✅ upgrade one level up
    public static String upgradeOneLevel(String current) {
        if (current.equals("Third (3rd)")) return "Lower Second (2:2)";
        if (current.equals("Lower Second (2:2)")) return "Upper Second (2:1)";
        if (current.equals("Upper Second (2:1)")) return "First (1st)";
        return current; // First stays first
    }
}

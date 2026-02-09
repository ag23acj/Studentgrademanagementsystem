public class GradeUtils {

    private static final double PASS_MARK = 40.0;

    public static double calculateAverage(double s1, double s2, double s3) {
        return (s1 + s2 + s3) / 3.0;
    }

    public static String calculateDegreeClass(double avg) {
        if (avg >= 70) return "First (1st)";
        if (avg >= 60) return "Upper Second (2:1)";
        if (avg >= 50) return "Lower Second (2:2)";
        if (avg >= 40) return "Third (3rd)";
        return "Fail";
    }

    public static String calculateFinalOutcome(
            double s1, double s2, double s3,
            ExamStatus st1, ExamStatus st2, ExamStatus st3
    ) {

        // Any ABSENT -> Fail
        if (st1 == ExamStatus.ABSENT ||
                st2 == ExamStatus.ABSENT ||
                st3 == ExamStatus.ABSENT) {
            return "Fail";
        }

        // Any failed subject -> Resit required
        if (s1 < PASS_MARK || s2 < PASS_MARK || s3 < PASS_MARK) {
            return "Resit Required";
        }

        // Otherwise classify
        double avg = calculateAverage(s1, s2, s3);
        return calculateDegreeClass(avg);
    }
}

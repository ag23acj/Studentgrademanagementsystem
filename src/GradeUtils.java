public class GradeUtils {

    public static double calculateAverage(double s1, double s2, double s3) {
        return (s1 + s2 + s3) / 3.0;
    }

    // NEW: overall classification with pass rule
    public static String calculateFinalOutcome(double s1, double s2, double s3) {

        // If any module failed (<40), overall is resit required
        if (s1 < 40 || s2 < 40 || s3 < 40) {
            return "Fail";
        }

        double avg = calculateAverage(s1, s2, s3);

        if (avg >= 70) return "First (1st)";
        if (avg >= 60) return "Upper Second (2:1)";
        if (avg >= 50) return "Lower Second (2:2)";
        if (avg >= 40) return "Third (3rd)";

        return "Fail / Resit Required";
    }
}

public class GradeUtils {

    public static double calculateAverage(double s1, double s2, double s3) {
        double avg = (s1 + s2 + s3) / 3.0;
        return Math.round(avg * 100.0) / 100.0; // 2 decimal places
    }

    // UK degree classification
    public static String calculateGrade(double avg) {
        if (avg >= 70) return "First (1st)";
        else if (avg >= 60) return "Upper Second (2:1)";
        else if (avg >= 50) return "Lower Second (2:2)";
        else if (avg >= 40) return "Third (3rd)";
        else return "Fail";
    }
}

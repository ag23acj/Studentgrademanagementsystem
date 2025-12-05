public class GradeUtils {

    public static double calculateAverage(double s1, double s2, double s3) {
        double avg = (s1 + s2 + s3) / 3.0;
        // round to 2 decimal places
        return Math.round(avg * 100.0) / 100.0;
    }

    public static String calculateGrade(double avg) {
        if (avg >= 90) return "A+";
        else if (avg >= 80) return "A";
        else if (avg >= 70) return "B";
        else if (avg >= 60) return "C";
        else if (avg >= 50) return "D";
        else return "F";
    }
}

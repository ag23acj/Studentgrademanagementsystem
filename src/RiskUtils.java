import java.util.ArrayList;
import java.util.List;

public class RiskUtils {

    private static final double EARLY_WARNING_AVG = 45.0;

    public static boolean isAtRisk(Student s) {
        return !getRiskReasons(s).isEmpty();
    }

    public static List<String> getRiskReasons(Student s) {
        List<String> reasons = new ArrayList<>();

        // 1) Absent
        if (s.getSub1Status() == ExamStatus.ABSENT
                || s.getSub2Status() == ExamStatus.ABSENT
                || s.getSub3Status() == ExamStatus.ABSENT) {
            reasons.add("Absent in at least one module");
        }

        // 2) Resit required
        if ("Resit Required".equalsIgnoreCase(s.getGrade())) {
            reasons.add("Resit required (one or more marks below 40)");
        }

        // 3) Borderline (needs attention)
        if (s.isBorderline() && !s.isUpgradeApproved()) {
            reasons.add("Borderline classification (pending review)");
        }

        // 4) Early warning low average
        if (s.getAverage() < EARLY_WARNING_AVG && !"Fail".equalsIgnoreCase(s.getGrade())) {
            reasons.add("Low average (< " + EARLY_WARNING_AVG + ")");
        }

        // 5) Multiple resits
        int resitCount = 0;
        if (s.getSub1Status() == ExamStatus.RESIT) resitCount++;
        if (s.getSub2Status() == ExamStatus.RESIT) resitCount++;
        if (s.getSub3Status() == ExamStatus.RESIT) resitCount++;
        if (resitCount >= 2) {
            reasons.add("Multiple resits (" + resitCount + " modules)");
        }

        return reasons;
    }
}

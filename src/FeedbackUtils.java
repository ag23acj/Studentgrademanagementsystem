import java.util.ArrayList;
import java.util.List;

public class FeedbackUtils {

    public static String generateFeedback(Student s) {

        // If any subject absent -> fail message
        if (s.getSub1Status() == ExamStatus.ABSENT ||
                s.getSub2Status() == ExamStatus.ABSENT ||
                s.getSub3Status() == ExamStatus.ABSENT) {

            List<String> absentModules = new ArrayList<>();
            if (s.getSub1Status() == ExamStatus.ABSENT) absentModules.add(s.getModule1Name());
            if (s.getSub2Status() == ExamStatus.ABSENT) absentModules.add(s.getModule2Name());
            if (s.getSub3Status() == ExamStatus.ABSENT) absentModules.add(s.getModule3Name());

            return "Result: FAIL (ABSENT)\n\n" +
                    "You were marked absent for: " + String.join(", ", absentModules) + ".\n" +
                    "Please contact your tutor/module leader to discuss next steps and eligibility for reassessment.\n";
        }

        // If resit required -> show exactly which modules failed
        if (s.getGrade().equalsIgnoreCase("Resit Required")) {

            List<String> failedModules = new ArrayList<>();
            if (s.getSub1() < 40) failedModules.add(s.getModule1Name() + " (" + s.getSub1() + ")");
            if (s.getSub2() < 40) failedModules.add(s.getModule2Name() + " (" + s.getSub2() + ")");
            if (s.getSub3() < 40) failedModules.add(s.getModule3Name() + " (" + s.getSub3() + ")");

            return "Result: RESIT REQUIRED\n\n" +
                    "You have not met the minimum pass mark (40) in:\n" +
                    "• " + String.join("\n• ", failedModules) + "\n\n" +
                    "Advice:\n" +
                    "• Review lecture slides + labs for the module(s) above.\n" +
                    "• Practice past papers / sample questions.\n" +
                    "• Attend support sessions and ask for feedback on weak topics.\n";
        }

        // Normal degree classification feedback
        String base;
        if (s.getGrade().startsWith("First")) {
            base = "Excellent work — your performance is at First-class level.\n" +
                    "Keep maintaining strong study habits and continue practicing exam-style questions.";
        } else if (s.getGrade().startsWith("Upper Second")) {
            base = "Strong result — you are performing at 2:1 level.\n" +
                    "To push to a First, focus on improving consistency across all modules and exam technique.";
        } else if (s.getGrade().startsWith("Lower Second")) {
            base = "Good effort — you achieved a 2:2.\n" +
                    "To improve, build stronger fundamentals and practice more structured answers and coursework quality.";
        } else if (s.getGrade().startsWith("Third")) {
            base = "You passed, but improvement is needed.\n" +
                    "Focus on understanding core concepts and seek support early, especially in weaker areas.";
        } else {
            // fallback
            base = "Please review your results and speak to your tutor if you need support.";
        }

        // Weakest module advice
        String weakestModule = s.getModule1Name();
        double weakestMark = s.getSub1();

        if (s.getSub2() < weakestMark) {
            weakestMark = s.getSub2();
            weakestModule = s.getModule2Name();
        }
        if (s.getSub3() < weakestMark) {
            weakestMark = s.getSub3();
            weakestModule = s.getModule3Name();
        }

        String focus = "\n\nFocus area:\n" +
                "• Your lowest module is " + weakestModule + " (" + weakestMark + ").\n" +
                "  Spend extra time reviewing weak topics and doing practice questions for this module.";

        // Borderline info (if borderline but not approved)
        String borderlineNote = "";
        if (s.isBorderline() && !s.isUpgradeApproved()) {
            borderlineNote = "\n\nBorderline notice:\n" +
                    "• Your average is close to a classification boundary.\n" +
                    "• An academic review may be required (admin approval workflow).";
        }

        // Approved infos
        String approvedNote = "";
        if (s.isUpgradeApproved()) {
            approvedNote = "\n\nApproval status:\n" +
                    "• Borderline upgrade has been approved by an administrator.";
        }

        return "Result: " + s.getGrade() + "\n\n" +
                base +
                focus +
                borderlineNote +
                approvedNote;
    }
}

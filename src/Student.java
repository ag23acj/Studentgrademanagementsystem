public class Student {

    private final String studentId;
    private final String name;

    private final String module1Name;
    private final String module2Name;
    private final String module3Name;

    private final double sub1;
    private final double sub2;
    private final double sub3;

    private final ExamStatus sub1Status;
    private final ExamStatus sub2Status;
    private final ExamStatus sub3Status;

    private final double average;

    // outcome + borderline workflow
    private String grade;                 // displayed final outcome
    private final boolean borderline;     // computed from avg
    private boolean upgradeApproved;      // admin toggles this

    // ✅ NEW: automatic feedback (generated)
    private final String feedback;

    public Student(String studentId, String name,
                   String module1Name, double sub1, ExamStatus sub1Status,
                   String module2Name, double sub2, ExamStatus sub2Status,
                   String module3Name, double sub3, ExamStatus sub3Status) {

        this.studentId = studentId;
        this.name = name;

        this.module1Name = module1Name;
        this.module2Name = module2Name;
        this.module3Name = module3Name;

        this.sub1 = sub1;
        this.sub2 = sub2;
        this.sub3 = sub3;

        this.sub1Status = sub1Status;
        this.sub2Status = sub2Status;
        this.sub3Status = sub3Status;

        this.average = GradeUtils.calculateAverage(sub1, sub2, sub3);

        this.grade = GradeUtils.calculateFinalOutcome(
                sub1, sub2, sub3,
                sub1Status, sub2Status, sub3Status
        );

        // Borderline only if NOT Fail/Resit
        this.borderline = GradeUtils.isBorderline(this.average)
                && !this.grade.equals("Fail")
                && !this.grade.equals("Resit Required");

        this.upgradeApproved = false;

        // ✅ NEW: feedback is generated automatically
        this.feedback = FeedbackUtils.generateFeedback(this);
    }

    public void setUpgradeApproved(boolean upgradeApproved) {
        this.upgradeApproved = upgradeApproved;
    }

    public boolean isUpgradeApproved() {
        return upgradeApproved;
    }

    public boolean isBorderline() {
        return borderline;
    }

    public void applyApprovedUpgrade() {
        if (borderline && upgradeApproved) {
            this.grade = GradeUtils.upgradeOneLevel(this.grade);
            // feedback won't auto-update here (that’s OK because we rebuild Student on reload/update)
            // if you want feedback to update instantly, we can regenerate it too.
        }
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }

    public String getModule1Name() { return module1Name; }
    public String getModule2Name() { return module2Name; }
    public String getModule3Name() { return module3Name; }

    public double getSub1() { return sub1; }
    public double getSub2() { return sub2; }
    public double getSub3() { return sub3; }

    public ExamStatus getSub1Status() { return sub1Status; }
    public ExamStatus getSub2Status() { return sub2Status; }
    public ExamStatus getSub3Status() { return sub3Status; }

    public double getAverage() { return average; }
    public String getGrade() { return grade; }

    // ✅ NEW: expose feedback
    public String getFeedback() { return feedback; }
}

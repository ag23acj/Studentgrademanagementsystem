public class Student {

    private String studentId;
    private String name;

    private String module1Name;
    private String module2Name;
    private String module3Name;

    private double sub1;
    private double sub2;
    private double sub3;

    private ExamStatus sub1Status;
    private ExamStatus sub2Status;
    private ExamStatus sub3Status;

    private double average;
    private String grade;

    // Borderline + approval
    private boolean borderline;
    private boolean upgradeApproved;

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

        // Calculate average and grade
        this.average = GradeUtils.calculateAverage(sub1, sub2, sub3);

        this.grade = GradeUtils.calculateFinalOutcome(
                sub1, sub2, sub3,
                sub1Status, sub2Status, sub3Status
        );

        // Borderline only if not Fail/Resit
        this.borderline = GradeUtils.isBorderline(this.average)
                && !this.grade.equals("Fail")
                && !this.grade.equals("Resit Required");

        this.upgradeApproved = false;
    }

    public void applyApprovedUpgrade() {
        if (borderline && upgradeApproved) {
            this.grade = GradeUtils.upgradeOneLevel(this.grade);
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

    public boolean isBorderline() { return borderline; }
    public boolean isUpgradeApproved() { return upgradeApproved; }

    public void setUpgradeApproved(boolean upgradeApproved) {
        this.upgradeApproved = upgradeApproved;
    }
}

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

    // Borderline upgrade workflow
    private boolean upgradeApproved = false;
    private String upgradedClass = "";
    private String borderlineFlag = "-";

    public Student(String studentId, String name,
                   String module1Name,
                   double sub1, ExamStatus sub1Status,
                   String module2Name,
                   double sub2, ExamStatus sub2Status,
                   String module3Name,
                   double sub3, ExamStatus sub3Status) {

        this.studentId = studentId;
        this.name = name;

        this.module1Name = module1Name;
        this.module2Name = module2Name;
        this.module3Name = module3Name;

        this.sub1Status = sub1Status;
        this.sub2Status = sub2Status;
        this.sub3Status = sub3Status;

        // Apply status rules to marks (absent -> mark becomes 0)
        this.sub1 = applyStatusRule(sub1, sub1Status);
        this.sub2 = applyStatusRule(sub2, sub2Status);
        this.sub3 = applyStatusRule(sub3, sub3Status);

        // Calculate avg
        this.average = GradeUtils.calculateAverage(this.sub1, this.sub2, this.sub3);

        // Calculate final outcome based on marks + status
        this.grade = GradeUtils.calculateFinalOutcome(
                this.sub1, this.sub2, this.sub3,
                this.sub1Status, this.sub2Status, this.sub3Status
        );

        // Borderline only if PASS classification (not Fail/Resit)
        if (this.grade.equals("Fail") || this.grade.equals("Resit Required")) {
            this.borderlineFlag = "-";
        } else {
            String target = GradeUtils.eligibleUpgradeTo(this.average);
            this.borderlineFlag = (target == null) ? "-" : "Eligible for Review (" + target + ")";
        }

        // If already approved (loaded later), override grade
        if (upgradeApproved && upgradedClass != null && !upgradedClass.isEmpty()) {
            this.grade = upgradedClass;
            this.borderlineFlag = "Approved";
        }
    }

    private double applyStatusRule(double mark, ExamStatus status) {
        if (status == ExamStatus.ABSENT) return 0.0;
        return mark;
    }

    // Admin approves upgrade
    public void approveUpgrade(String targetClass) {
        this.upgradeApproved = true;
        this.upgradedClass = targetClass;
        this.grade = targetClass;
        this.borderlineFlag = "Approved";
    }

    // --- getters ---
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

    public boolean isUpgradeApproved() { return upgradeApproved; }
    public String getUpgradedClass() { return upgradedClass; }
    public String getBorderlineFlag() { return borderlineFlag; }

    // Used when loading from CSV
    public void setUpgradeApproved(boolean approved) {
        this.upgradeApproved = approved;
    }

    public void setUpgradedClass(String upgradedClass) {
        this.upgradedClass = upgradedClass;

        if (upgradeApproved && upgradedClass != null && !upgradedClass.isEmpty()) {
            this.grade = upgradedClass;
            this.borderlineFlag = "Approved";
        }
    }
}

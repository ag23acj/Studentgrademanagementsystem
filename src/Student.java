public class Student {

    private String studentId;
    private String name;

    private double sub1;
    private double sub2;
    private double sub3;

    private ExamStatus sub1Status;
    private ExamStatus sub2Status;
    private ExamStatus sub3Status;

    private double average;
    private String grade;

    public Student(String studentId, String name,
                   double sub1, ExamStatus sub1Status,
                   double sub2, ExamStatus sub2Status,
                   double sub3, ExamStatus sub3Status) {

        this.studentId = studentId;
        this.name = name;

        this.sub1Status = sub1Status;
        this.sub2Status = sub2Status;
        this.sub3Status = sub3Status;

        this.sub1 = applyStatusRule(sub1, sub1Status);
        this.sub2 = applyStatusRule(sub2, sub2Status);
        this.sub3 = applyStatusRule(sub3, sub3Status);

        this.average = GradeUtils.calculateAverage(this.sub1, this.sub2, this.sub3);

        this.grade = GradeUtils.calculateFinalOutcome(
                this.sub1, this.sub2, this.sub3,
                this.sub1Status, this.sub2Status, this.sub3Status
        );


    }

    // Backwards compatibility (if any old code still calls old constructor)
    public Student(String studentId, String name, double sub1, double sub2, double sub3) {
        this(studentId, name,
                sub1, ExamStatus.FIRST_SITTING,
                sub2, ExamStatus.FIRST_SITTING,
                sub3, ExamStatus.FIRST_SITTING);
    }

    private double applyStatusRule(double mark, ExamStatus status) {
        if (status == ExamStatus.ABSENT) return 0;
        if (status == ExamStatus.RESIT) return Math.min(mark, 40);
        return mark;
    }

    public String getResitSubjects() {
        StringBuilder sb = new StringBuilder();

        if (sub1 < 40) sb.append("Subject 1, ");
        if (sub2 < 40) sb.append("Subject 2, ");
        if (sub3 < 40) sb.append("Subject 3, ");

        if (sb.length() == 0) return "None";
        return sb.substring(0, sb.length() - 2);
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }

    public double getSub1() { return sub1; }
    public double getSub2() { return sub2; }
    public double getSub3() { return sub3; }

    public ExamStatus getSub1Status() { return sub1Status; }
    public ExamStatus getSub2Status() { return sub2Status; }
    public ExamStatus getSub3Status() { return sub3Status; }

    public double getAverage() { return average; }
    public String getGrade() { return grade; }

    public String getResitSubjects(String sub1Name, String sub2Name, String sub3Name) {
        StringBuilder sb = new StringBuilder();

        if (sub1Status == ExamStatus.ABSENT || sub1 < 40) sb.append(sub1Name).append(", ");
        if (sub2Status == ExamStatus.ABSENT || sub2 < 40) sb.append(sub2Name).append(", ");
        if (sub3Status == ExamStatus.ABSENT || sub3 < 40) sb.append(sub3Name).append(", ");

        if (sb.length() == 0) return "None";
        return sb.substring(0, sb.length() - 2);
    }

}


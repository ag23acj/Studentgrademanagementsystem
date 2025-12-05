public class Student {
    private String rollNo;
    private String name;
    private double sub1;
    private double sub2;
    private double sub3;
    private double average;
    private String grade;

    public Student(String rollNo, String name, double sub1, double sub2, double sub3) {
        this.rollNo = rollNo;
        this.name = name;
        this.sub1 = sub1;
        this.sub2 = sub2;
        this.sub3 = sub3;

        this.average = GradeUtils.calculateAverage(sub1, sub2, sub3);
        this.grade = GradeUtils.calculateGrade(this.average);
    }

    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public double getSub1() { return sub1; }
    public double getSub2() { return sub2; }
    public double getSub3() { return sub3; }
    public double getAverage() { return average; }
    public String getGrade() { return grade; }

    public void display() {
        System.out.printf("%-8s %-15s %7.2f %7.2f %7.2f %8.2f %-3s%n",
                rollNo, name, sub1, sub2, sub3, average, grade);
    }
}

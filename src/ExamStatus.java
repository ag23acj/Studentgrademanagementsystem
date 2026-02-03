public enum ExamStatus {
    FIRST_SITTING,
    ABSENT,
    RESIT;

    @Override
    public String toString() {
        switch (this) {
            case FIRST_SITTING:
                return "First Sitting";
            case ABSENT:
                return "Absent";
            case RESIT:
                return "Resit";
            default:
                return this.name();
        }
    }
}

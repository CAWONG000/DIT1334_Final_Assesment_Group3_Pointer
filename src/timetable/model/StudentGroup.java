package timetable.model;

public class StudentGroup {
    private final String id, programme;
    private final int studentCount;
    public StudentGroup(String id, String programme, int studentCount) {
        if (studentCount < 1) throw new IllegalArgumentException("Student count must be positive.");
        this.id=id.trim(); this.programme=programme.trim(); this.studentCount=studentCount;
    }
    public String getGroupId() { return id; }
    public String getProgramme() { return programme; }
    public int getStudentCount() { return studentCount; }
    @Override public String toString() { return id + " (" + studentCount + ")"; }
}

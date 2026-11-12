package timetable.model;

public class Course {
    private final String code, name;
    private final boolean laboratoryRequired;

    public Course(String code, String name, boolean laboratoryRequired) {
        this.code = code.trim();
        this.name = name.trim();
        this.laboratoryRequired = laboratoryRequired;
    }
    public String getCourseCode() { return code; }
    public String getCourseName() { return name; }
    public boolean requiresLaboratory() { return laboratoryRequired; }
    @Override public String toString() { return code + " - " + name; }
}

package timetable.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class ClassSession {
    private final String sessionId;
    private final Course course;
    private final Lecturer lecturer;
    private final StudentGroup group;
    private final Resource resource;
    private final DayOfWeek day;
    private final LocalTime start, end;
    private final boolean extraClass;

    public ClassSession(Course course, Lecturer lecturer, StudentGroup group, Resource resource,
                        DayOfWeek day, LocalTime start, LocalTime end, boolean extraClass) {
        if (course == null || lecturer == null || group == null || resource == null || day == null)
            throw new IllegalArgumentException("Select course, lecturer, group, resource and day.");
        if (start == null || end == null || !start.isBefore(end))
            throw new IllegalArgumentException("Start time must be before end time.");
        this.sessionId=UUID.randomUUID().toString();
        this.course=course; this.lecturer=lecturer; this.group=group; this.resource=resource;
        this.day=day; this.start=start; this.end=end; this.extraClass=extraClass;
    }
    public String getSessionId() { return sessionId; }
    public Course getCourse() { return course; }
    public Lecturer getLecturer() { return lecturer; }
    public StudentGroup getGroup() { return group; }
    public Resource getResource() { return resource; }
    public DayOfWeek getDay() { return day; }
    public LocalTime getStartTime() { return start; }
    public LocalTime getEndTime() { return end; }
    public boolean isExtraClass() { return extraClass; }
    public boolean overlaps(ClassSession other) {
        return day == other.day && start.isBefore(other.end) && other.start.isBefore(end);
    }
        
}

package timetable.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Availability {
    private final String resourceId;
    private final DayOfWeek day;
    private final LocalTime start, end;
    public Availability(String resourceId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) throw new IllegalArgumentException("Start must be before end.");
        this.resourceId=resourceId; this.day=day; this.start=start; this.end=end;
    }
    public String getResourceId() { return resourceId; }
    public DayOfWeek getDay() { return day; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public boolean contains(DayOfWeek d, LocalTime s, LocalTime e) {
        return day == d && !s.isBefore(start) && !e.isAfter(end);
    }
}

package timetable.service;

import timetable.model.*;
import java.util.*;
import java.time.*;

public class ConflictValidator {
    private final List<Availability> availability;
    public ConflictValidator(List<Availability> availability) { this.availability=availability; }

    public List<String> validate(ClassSession candidate, List<ClassSession> existing) {
        List<String> errors=new ArrayList<>();
        if (!candidate.getResource().isSuitableFor(candidate.getCourse(),
                candidate.getGroup().getStudentCount()))
            errors.add(candidate.getCourse().requiresLaboratory()
                    ? "Course requires a laboratory or suitable capacity."
                    : "Room capacity is insufficient.");

        boolean lecturerAvailable=availability.stream().anyMatch(a ->
                a.getResourceId().equals(candidate.getLecturer().getLecturerId())
                && a.contains(candidate.getDay(),candidate.getStartTime(),candidate.getEndTime()));
        boolean resourceAvailable=availability.stream().anyMatch(a ->
                a.getResourceId().equals(candidate.getResource().getResourceId())
                && a.contains(candidate.getDay(),candidate.getStartTime(),candidate.getEndTime()));
        if (!lecturerAvailable) errors.add("Lecturer is not available for this period.");
        if (!resourceAvailable) errors.add("Room/resource is not available for this period.");

        for (ClassSession s: existing) {
            if (s.getSessionId().equals(candidate.getSessionId())) continue;
            if (!candidate.overlaps(s)) continue;
            if (candidate.getLecturer().getLecturerId().equals(s.getLecturer().getLecturerId()))
                errors.add("Lecturer has an overlapping class.");
            if (candidate.getGroup().getGroupId().equals(s.getGroup().getGroupId()))
                errors.add("Student group has an overlapping class.");
            if (candidate.getResource().getResourceId().equals(s.getResource().getResourceId()))
                errors.add("Room/resource is already allocated.");
        }
        return errors;
    }
}

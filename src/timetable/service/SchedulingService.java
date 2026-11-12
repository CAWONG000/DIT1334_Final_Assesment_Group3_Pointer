package timetable.service;

import timetable.model.*;
import timetable.strategy.SchedulingStrategy;
import java.util.*;

public class SchedulingService {
    private final ConflictValidator validator;
    private final Timetable timetable = new Timetable("TT001", "Semester 1");

    public SchedulingService(ConflictValidator validator) {
        this.validator = validator;
    }

    public synchronized List<String> addSession(ClassSession session) {
        List<String> errors = validator.validate(session, timetable.getSessions());
        if (errors.isEmpty()) {
            timetable.addSession(session);
        }
        return errors;
    }

    public synchronized List<ClassSession> getSessions() {
        return timetable.getSessions();
    }

    public synchronized Timetable getTimetable() {
        return timetable;
    }

    /** Checks every session against the others and returns a ConflictReport. */
    public synchronized ConflictReport validateAll() {
        List<String> all = new ArrayList<>();
        List<ClassSession> sessions = timetable.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            List<ClassSession> other = new ArrayList<>(sessions);
            ClassSession current = other.remove(i);
            for (String error : validator.validate(current, other)) {
                all.add(current.getCourse().getCourseCode() + ": " + error);
            }
        }
        return new ConflictReport(all);
    }

    /** The strategy picks the first conflict-free slot, and the session is added to the timetable. */
    public synchronized ClassSession generateSession(SchedulingStrategy strategy, Course course,
            Lecturer lecturer, StudentGroup group, Resource resource, int hours) {
        ClassSession s = strategy.findSlot(course, lecturer, group, resource, hours,
                candidate -> validator.validate(candidate, timetable.getSessions()).isEmpty());
        if (s != null) {
            timetable.addSession(s);
        }
        return s;
    }
}

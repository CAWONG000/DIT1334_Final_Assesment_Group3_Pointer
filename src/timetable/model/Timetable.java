package timetable.model;

import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;
import java.util.stream.Collectors;
import java.util.List;

/** Holds all the class sessions of one timetable (matches the class diagram). */
public class Timetable {
    private final String timetableID;
    private final String semester;
    private final List<ClassSession> sessions = new ArrayList<>();

    public Timetable(String timetableID, String semester) {
        this.timetableID = timetableID;
        this.semester = semester;
    }

    public void addSession(ClassSession session) {
        sessions.add(session);
    }

    /** Returns a copy, so other classes cannot change the list directly. */
    public List<ClassSession> getSessions() {
        return new ArrayList<>(sessions);
    }

    public String getTimetableID() { return timetableID; }
    public String getSemester() { return semester; }

    public String getDetails() {
        return "Timetable " + timetableID + " (" + semester + "): " + sessions.size() + " sessions";
    }
    
 // Feature: Search / Filter schedules
    public List<ClassSession> filterByCourse(String courseCode) {
        return sessions.stream()
                .filter(session -> session.getCourse().getCourseCode().equalsIgnoreCase(courseCode))
                .collect(Collectors.toList());
    }

    // Feature: Export timetable summaries
    public boolean exportSummary(String filename) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("=== TIMETABLE SUMMARY ===");
            writer.println(getDetails());
            writer.println("-------------------------");
            for (ClassSession session : sessions) {
                // matching dis diag-- getSessionId() and getResource()
            	writer.println(session.getDay() + " " + session.getStartTime() + "-" + session.getEndTime()
                + " | " + session.getCourse().getCourseCode()
                + " | " + session.getLecturer().getLecturerName()
                + " | " + session.getGroup().getGroupId()
                + " | " + session.getResource().getName()
                + (session.isExtraClass() ? " | Extra Class" : " | Lecture"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Quick scan: true if two sessions overlap in time and share a lecturer, group or room. */
    public boolean checkConflicts() {
        for (int i = 0; i < sessions.size(); i++) {
            for (int j = i + 1; j < sessions.size(); j++) {
                ClassSession a = sessions.get(i);
                ClassSession b = sessions.get(j);
                if (a.overlaps(b)
                        && (a.getLecturer().getLecturerId().equals(b.getLecturer().getLecturerId())
                        || a.getGroup().getGroupId().equals(b.getGroup().getGroupId())
                        || a.getResource().getResourceId().equals(b.getResource().getResourceId()))) {
                    return true;
                }
            }
        }
        return false;
    }
}

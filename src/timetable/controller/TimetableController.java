package timetable.controller;

import timetable.adapter.TimetableApiAdapter;
import timetable.model.*;
import timetable.queue.TimetableExportQueue;
import timetable.service.ConflictValidator;
import timetable.service.SchedulingService;
import timetable.strategy.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Sits between the GUI (MainFrame) and the domain/service classes.
 * The GUI only calls this class, so it never touches the scheduling logic directly.
 * Singleton: one shared instance, so data survives logout and login.
 */
public class TimetableController {
    private static TimetableController instance;

    public static synchronized TimetableController getInstance() {
        if (instance == null) {
            instance = new TimetableController();
        }
        return instance;
    }

    private final List<Course> courses = new ArrayList<>();
    private final List<Lecturer> lecturers = new ArrayList<>();
    private final List<StudentGroup> groups = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();
    private final List<Availability> availability = new CopyOnWriteArrayList<>();
    private final TimetableApiAdapter adapter = new TimetableApiAdapter();
    private final TimetableExportQueue exportQueue = new TimetableExportQueue();
    private SchedulingService service;

    private TimetableController() {
        reset();
    }

    // ---------- data ----------

    /** Clears everything and loads the demo data and one demo session. */
    public synchronized void reset() {
        courses.clear();
        lecturers.clear();
        groups.clear();
        resources.clear();
        availability.clear();

        courses.add(new Course("JAVA101", "Java Programming", false));
        courses.add(new Course("NET201", "Networking Lab", true));
        lecturers.add(new Lecturer("L001", "Mr Arif"));
        lecturers.add(new Lecturer("L002", "Ms Sarah"));
        groups.add(new StudentGroup("IT1", "Certificate IT", 25));
        groups.add(new StudentGroup("IT2", "Certificate IT", 20));
        resources.add(new Room("R101", "Classroom 101", 40));
        resources.add(new Laboratory("LAB1", "Computer Lab 1", 30, "Computers"));
        for (Lecturer l : lecturers) {
            addDefaultAvailability(l.getLecturerId());
        }
        for (Resource r : resources) {
            addDefaultAvailability(r.getResourceId());
        }

        service = new SchedulingService(new ConflictValidator(availability));
        service.addSession(new ClassSession(courses.get(0), lecturers.get(0), groups.get(0),
                resources.get(0), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0), false));
    }

    private void addDefaultAvailability(String id) {
        for (DayOfWeek d : DayOfWeek.values()) {
            availability.add(new Availability(id, d, LocalTime.of(8, 0), LocalTime.of(18, 0)));
        }
    }

    public synchronized List<Course> getCourses() { return new ArrayList<>(courses); }
    public synchronized List<Lecturer> getLecturers() { return new ArrayList<>(lecturers); }
    public synchronized List<StudentGroup> getGroups() { return new ArrayList<>(groups); }
    public synchronized List<Resource> getResources() { return new ArrayList<>(resources); }

    // ---------- viewing and filtering ----------

    /** Programme Leader and Student see every session; a Lecturer sees only their own. */
    public List<ClassSession> getSessionsFor(User viewer) {
        return forViewer(service.getSessions(), viewer);
    }

    public List<ClassSession> getSessionsByCourse(User viewer, String courseCode) {
        return forViewer(service.getTimetable().filterByCourse(courseCode), viewer);
    }

    private List<ClassSession> forViewer(List<ClassSession> sessions, User viewer) {
        if (viewer instanceof Lecturer) {
            return sessions.stream()
                    .filter(s -> s.getLecturer().getUserID().equals(viewer.getUserID()))
                    .collect(Collectors.toList());
        }
        return sessions;
    }

    // ---------- features ----------

    /** Returns the conflict messages (empty list means the session was added). */
    public List<String> addSession(Course course, Lecturer lecturer, StudentGroup group,
                                   Resource resource, DayOfWeek day, String start, String end,
                                   boolean extraClass) {
        ClassSession s = new ClassSession(course, lecturer, group, resource, day,
                LocalTime.parse(start.trim()), LocalTime.parse(end.trim()), extraClass);
        return service.addSession(s);
    }

    /** Runs the chosen strategy and returns the new session, or null if no slot is free. */
    public ClassSession generateSession(String strategyName, Course course, Lecturer lecturer,
                                        StudentGroup group, Resource resource, int hours) {
        SchedulingStrategy strategy = strategyName.equals("Greedy")
                ? new GreedyStrategy() : new PriorityStrategy();
        return service.generateSession(strategy, course, lecturer, group, resource, hours);
    }

    public ConflictReport validateAll() {
        return service.validateAll();
    }

    /** Registers a room or laboratory, with default availability so it can be used straight away. */
    public synchronized Resource registerResource(String type, String id, String name,
                                                  String capacityText, String equipment) {
        String cleanId = id.trim();
        String cleanName = name.trim();
        if (cleanId.isEmpty() || cleanName.isEmpty()) {
            throw new IllegalArgumentException("ID and name cannot be empty.");
        }
        int capacity = Integer.parseInt(capacityText.trim());   // NumberFormatException if not a number
        for (Resource r : resources) {
            if (r.getResourceId().equalsIgnoreCase(cleanId)) {
                throw new IllegalArgumentException("ID " + cleanId + " is already registered.");
            }
        }
        Resource created = type.equals("Room")
                ? new Room(cleanId, cleanName, capacity)
                : new Laboratory(cleanId, cleanName, capacity, equipment.trim());
        resources.add(created);
        addDefaultAvailability(created.getResourceId());
        return created;
    }

    /** Replaces the lecturer's availability for that day. */
    public synchronized String submitAvailability(Lecturer lecturer, DayOfWeek day,
                                                  String start, String end) {
        Availability a = new Availability(lecturer.getUserID(), day,
                LocalTime.parse(start.trim()), LocalTime.parse(end.trim()));
        availability.removeIf(x -> x.getResourceId().equals(lecturer.getUserID()) && x.getDay() == day);
        availability.add(a);
        String text = day + " " + a.getStart() + "-" + a.getEnd();
        lecturer.submitAvailability(text);
        return text;
    }

    // ---------- export ----------

    public void exportCsv(File file, List<ClassSession> sessions) throws IOException {
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("Day,Start,End,Course,Lecturer,Group,Room,Type");
            for (ClassSession s : sessions) {
                out.println(String.join(",",
                        csv(s.getDay()), csv(s.getStartTime()), csv(s.getEndTime()),
                        csv(s.getCourse().getCourseCode()), csv(s.getLecturer().getLecturerName()),
                        csv(s.getGroup().getGroupId()), csv(s.getResource().getName()),
                        csv(s.isExtraClass() ? "Extra Class" : "Lecture")));
            }
        }
    }

    private String csv(Object value) {
        return "\"" + String.valueOf(value).replace("\"", "\"\"") + "\"";
    }

    public boolean exportSummary(String path) {
        return service.getTimetable().exportSummary(path);
    }

    // ---------- cloud record queue ----------

    public int queueRecords() {
        int added = 0;
        for (ClassSession s : service.getSessions()) {
            if (exportQueue.enqueue(adapter.toCloudRecord(s))) {
                added++;
            }
        }
        return added;
    }

    public Map<String, Object> pollRecord() {
        return exportQueue.poll();
    }
}

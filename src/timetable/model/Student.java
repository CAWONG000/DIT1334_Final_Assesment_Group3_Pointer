package timetable.model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    public Student(String userID, String name, String faculty,
                   String username, String password) {
        super(userID, name, faculty, username, password);
    }

    @Override
    public String getRole() {
        return "Student";
    }

    /** Returns only the sessions that match the keyword (course, lecturer, group, room or day). */
    public List<ClassSession> filterTimetable(String keyword, List<ClassSession> sessions) {
        String k = keyword.trim().toLowerCase();
        List<ClassSession> result = new ArrayList<>();
        for (ClassSession s : sessions) {
            String text = (s.getCourse().getCourseCode() + " "
                    + s.getLecturer().getLecturerName() + " "
                    + s.getGroup().getGroupId() + " "
                    + s.getResource().getResourceId() + " "
                    + s.getDay()).toLowerCase();
            if (text.contains(k)) {
                result.add(s);
            }
        }
        return result;
    }
}
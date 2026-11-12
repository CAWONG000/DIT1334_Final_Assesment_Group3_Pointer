package timetable.model;

import java.util.List;

public class ProgrammeLeader extends User {

    public ProgrammeLeader(String userID, String name, String faculty,
                           String username, String password) {
        super(userID, name, faculty, username, password);
    }

    @Override
    public String getRole() {
        return "Programme Leader";
    }

    public String enterTimetableData() {
        return getName() + " is entering timetable data.";
    }

    /** An entry is valid when the conflict validator returned no errors. */
    public boolean validateEntry(List<String> errors) {
        return errors.isEmpty();
    }

    public String saveTimetable() {
        return "Timetable saved by " + getName() + ".";
    }
}
package timetable.model;

public class Lecturer extends User {
    private String availability;

    public Lecturer(String userID, String name, String faculty,
                    String username, String password) {
        super(userID, name, faculty, username, password);
        this.availability = "Mon-Sun 08:00-18:00";
    }

    /** Keeps the old 2-argument constructor so the existing seed data still compiles. */
    public Lecturer(String id, String name) {
        this(id.trim(), name.trim(), "", id.trim(), "");
    }

    public void submitAvailability(String availability) {
        this.availability = availability;
    }

    public String getAvailability() { return availability; }

    // Old getters, so ClassSession, ConflictValidator and MainFrame keep working
    public String getLecturerId() { return getUserID(); }
    public String getLecturerName() { return getName(); }

    @Override
    public String getRole() {
        return "Lecturer";
    }

    @Override
    public String toString() {
        return getUserID() + " - " + getName();
    }
}
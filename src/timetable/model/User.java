package timetable.model;

/** Abstract parent of ProgrammeLeader, Lecturer and Student (matches the class diagram). */
public abstract class User {
    private final String userID;
    private final String name;
    private final String faculty;
    private final String username;
    private final String password;

    public User(String userID, String name, String faculty, String username, String password) {
        this.userID = userID;
        this.name = name;
        this.faculty = faculty;
        this.username = username;
        this.password = password;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String logout() {
        return name + " logged out.";
    }

    public String viewTimetable() {
        return "Viewing timetable as " + getRole() + ": " + name;
    }

    /** Each subclass returns its own role name (polymorphism). */
    public abstract String getRole();

    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getFaculty() { return faculty; }
    public String getUsername() { return username; }
}
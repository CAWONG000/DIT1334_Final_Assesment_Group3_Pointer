package timetable.service;

import timetable.model.*;
import java.util.ArrayList;
import java.util.List;

/** Demo login only: accounts are hard-coded for this assignment (no database). */
public class AuthService {
    private final List<User> users = new ArrayList<>();

    public AuthService() {
        users.add(new ProgrammeLeader("PL101", "Dr Diana", "Computing", "leader", "leader123"));
        users.add(new Lecturer("L001", "Mr Arif", "Computing", "arif", "arif123"));
        users.add(new Lecturer("L002", "Ms Sarah", "Computing", "sarah", "sarah123"));
        users.add(new Student("S001", "Aina", "Computing", "student", "student123"));
    }

    /** Returns the matching user, or null if the username or password is wrong. */
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.login(username, password)) {
                return u;
            }
        }
        return null;
    }
}

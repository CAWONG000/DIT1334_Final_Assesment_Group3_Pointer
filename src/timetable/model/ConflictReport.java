package timetable.model;

import java.util.ArrayList;
import java.util.List;

/** The result of a conflict check: a list of messages, empty when there are no conflicts. */
public class ConflictReport {
    private final List<String> messages;

    public ConflictReport(List<String> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public boolean hasConflicts() {
        return !messages.isEmpty();
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public String getDetails() {
        return messages.isEmpty() ? "No conflicts found." : String.join("\n", messages);
    }
}

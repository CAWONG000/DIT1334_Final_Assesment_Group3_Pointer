package timetable.strategy;

import timetable.model.*;
import java.util.function.Predicate;

public interface SchedulingStrategy {
    /**
     * Finds a free slot for the session, trying slots in this strategy's own order.
     * isFree tells the strategy whether a candidate session has no conflicts.
     * Returns the first conflict-free session, or null if no slot fits.
     */
    ClassSession findSlot(Course course, Lecturer lecturer, StudentGroup group,
                          Resource resource, int hours, Predicate<ClassSession> isFree);

    String getName();
}
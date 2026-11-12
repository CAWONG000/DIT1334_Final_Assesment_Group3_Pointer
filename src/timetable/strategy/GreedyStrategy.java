package timetable.strategy;

import timetable.model.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.function.Predicate;

/** Greedy: takes the earliest free slot, Monday to Friday, 08:00 to 18:00. */
public class GreedyStrategy implements SchedulingStrategy {
    @Override
    public ClassSession findSlot(Course course, Lecturer lecturer, StudentGroup group,
                                 Resource resource, int hours, Predicate<ClassSession> isFree) {
        DayOfWeek[] days = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        for (DayOfWeek day : days) {
            for (int h = 8; h + hours <= 18; h++) {
                ClassSession s = new ClassSession(course, lecturer, group, resource,
                        day, LocalTime.of(h, 0), LocalTime.of(h + hours, 0), false);
                if (isFree.test(s)) {
                    return s;
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "Greedy";
    }
}

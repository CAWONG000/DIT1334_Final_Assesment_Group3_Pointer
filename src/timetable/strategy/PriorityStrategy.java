package timetable.strategy;

import timetable.model.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/** Priority-based: lab courses get morning slots first, other courses get afternoon slots first. */
public class PriorityStrategy implements SchedulingStrategy {
    @Override
    public ClassSession findSlot(Course course, Lecturer lecturer, StudentGroup group,
                                 Resource resource, int hours, Predicate<ClassSession> isFree) {
        List<Integer> starts = new ArrayList<>();
        for (int h = 8; h + hours <= 18; h++) {
            starts.add(h);
        }
        if (!course.requiresLaboratory()) {
            Collections.reverse(starts);   // afternoon first for non-lab courses
        }
        DayOfWeek[] days = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        for (DayOfWeek day : days) {
            for (int h : starts) {
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
        return "Priority-Based";
    }
}

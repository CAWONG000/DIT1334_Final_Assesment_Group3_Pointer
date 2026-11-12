package timetable.adapter;

import timetable.model.ClassSession;
import java.util.*;

public class TimetableApiAdapter {
    public Map<String,Object> toCloudRecord(ClassSession s) {
        Map<String,Object> r=new LinkedHashMap<>();
        r.put("sessionId",s.getSessionId());
        r.put("courseCode",s.getCourse().getCourseCode());
        r.put("lecturerId",s.getLecturer().getLecturerId());
        r.put("groupId",s.getGroup().getGroupId());
        r.put("resourceId",s.getResource().getResourceId());
        r.put("day",s.getDay().toString());
        r.put("startTime",s.getStartTime().toString());
        r.put("endTime",s.getEndTime().toString());
        r.put("extraClass",s.isExtraClass());
        return r;
    }
}

package timetable.queue;

import java.util.*;
import java.util.concurrent.*;

public class TimetableExportQueue {
    private final BlockingQueue<Map<String,Object>> queue=new LinkedBlockingQueue<>();
    private final Set<String> queuedOrPublished=ConcurrentHashMap.newKeySet();

    public boolean enqueue(Map<String,Object> record) {
        Object raw=record.get("sessionId");
        if (!(raw instanceof String) || ((String)raw).isBlank()) return false;
        if (!queuedOrPublished.add((String)raw)) return false;
        return queue.offer(Collections.unmodifiableMap(new LinkedHashMap<>(record)));
    }
    public Map<String,Object> poll() { return queue.poll(); }
    public int size() { return queue.size(); }
}

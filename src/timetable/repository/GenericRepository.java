package timetable.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GenericRepository<T> {
    private final Map<String,T> data = new ConcurrentHashMap<>();
    private final Function<T,String> idFunction;
    public GenericRepository(Function<T,String> idFunction) { this.idFunction=idFunction; }
    public void add(T item) {
        String id=idFunction.apply(item);
        if (data.putIfAbsent(id,item) != null) throw new IllegalArgumentException("Duplicate ID: " + id);
    }
    public Optional<T> findById(String id) { return Optional.ofNullable(data.get(id)); }
    public List<T> findAll() { return new ArrayList<>(data.values()); }
    public boolean delete(String id) { return data.remove(id) != null; }
    public void update(T item) {
        String id=idFunction.apply(item);
        if (!data.containsKey(id)) throw new NoSuchElementException("ID not found: " + id);
        data.put(id,item);
    }
}

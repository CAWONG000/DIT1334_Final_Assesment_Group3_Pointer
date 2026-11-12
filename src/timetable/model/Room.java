package timetable.model;

public class Room extends Resource {
    public Room(String id, String name, int capacity) { super(id,name,capacity); }
    @Override public boolean isLaboratory() { return false; }
}

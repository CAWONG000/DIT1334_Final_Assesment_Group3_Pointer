package timetable.model;

public abstract class Resource {
    private final String id, name;
    private final int capacity;
    protected Resource(String id, String name, int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
        this.id=id.trim(); this.name=name.trim(); this.capacity=capacity;
    }
    public String getResourceId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public abstract boolean isLaboratory();
    public boolean isSuitableFor(Course course, int students) {
        return capacity >= students && (!course.requiresLaboratory() || isLaboratory());
    }
    @Override public String toString() { return id + " - " + name + " (cap " + capacity + ")"; }
}

package timetable.model;

public class Laboratory extends Resource {
    private final String equipment;
    public Laboratory(String id, String name, int capacity, String equipment) {
        super(id,name,capacity); this.equipment=equipment == null ? "" : equipment.trim();
    }
    public String getEquipment() { return equipment; }
    @Override public boolean isLaboratory() { return true; }
}

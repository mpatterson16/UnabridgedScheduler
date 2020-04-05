package net.augustana.maegan.unabridgedscheduler;

public class Event {
    String name;
    String date;
    String desc;
    String location;
    String id;

    public Event(String name, String date, String desc, String loc, String id) {
        this.name = name;
        this.date = date;
        this.desc = desc;
        this.location = loc;
        this.id = id;
    }
}

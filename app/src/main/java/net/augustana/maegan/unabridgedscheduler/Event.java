package net.augustana.maegan.unabridgedscheduler;

public class Event implements Comparable<Event> {
    private String name;
    private String date;
    private String desc;
    private String location;
    private String id;

    public Event(String name, String date, String desc, String loc, String id) {
        this.name = name;
        this.date = date;
        this.desc = desc;
        this.location = loc;
        this.id = id;
    }

    //sort by earliest date
    public int compareTo(Event other) {
        return date.compareTo(other.date);
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getLocation() {
        return location;
    }

    //the randomly generated ID used to identify the event in the database
    public String getId() {
        return id;
    }
}

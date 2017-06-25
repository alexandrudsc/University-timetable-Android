package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 2/25/2017.
 * Class holding data about a timetable: usually id and name;
 */
public final class Timetable {
    private int id;
    private String name;
    private Type type;

    private Timetable(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static final class Creator {
        public static Timetable create(String[] data) throws NumberFormatException, IndexOutOfBoundsException {
            Timetable timetable = new Timetable();
            timetable.id = Integer.valueOf(data[0]);
            timetable.type = Type.values()[Integer.valueOf(data[1])];
            timetable.name = data[2];
            return timetable;
        }
    }

    public enum Type {
        Student,
        Professor
    }
}

package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 2/25/2017.
 * Class holding data about a timetable: usually id and name;
 */
public final class Timetable {
    private int id;
    private String name;

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

    public static final class Creator {
        public static Timetable create(String[] data) throws NumberFormatException, IndexOutOfBoundsException {
            Timetable timetable = new Timetable();
            timetable.id = Integer.valueOf(data[0]);
            timetable.name = data[1];
            return timetable;
        }
    }
}

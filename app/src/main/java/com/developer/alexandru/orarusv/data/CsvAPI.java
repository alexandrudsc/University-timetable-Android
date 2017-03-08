package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 10/20/16.
 * Very Raw API of USV server.
 */
public final class CsvAPI {

    public static final int CSV_COUNT = 21;

    /**
     * Public URLs.
     */
    public static final String TIME_URL = "http://www.usv.ro/orar/vizualizare/data/zoneinterzise.php";
    public static final String PROFS_URL = "http://www.usv.ro/orar/vizualizare/data/cadre.php";
    public static final String PARTIAL_GROUP_TIMETABLE_URL = "http://www.usv.ro/orar/vizualizare/data/orarSPG.php?mod=grupa&ID="; // Partial URl for non_modular timetables
    public static final String PARTIAL_PROF_TIMETABLE_URL = "http://www.usv.ro/orar/vizualizare/data/orarSPG.php?mod=prof&ID="; // Partial URl for profs timetables


    /**
     * Result format for timetable of group.
     * Result format for timetable of professor.
     */
    public static final int PROF_ID = 3;
    public static final int PROF_LAST_NAME = 4;
    public static final int PROF_FIRST_NAME = 5;
    public static final int RANK = 6;
    public static final int HAS_PHD= 7;
    public static final int OTHER_TITLES = 8;
    public static final int BUILDING = 10;
    public static final int ROOM = 11;
    public static final int ROOM_SHORT_NAME = 12;
    public static final int COURSE_FULL_NAME = 13;
    public static final int COURSE_NAME = 14;
    public static final int DAY = 15;
    public static final int START = 16;
    public static final int STOP = 17;
    public static final int PARITY = 18;
    public static final int INFO = 19;
    public static final int TYPE = 21;

    /**
     * Parity encoded
     */
    public static final String EVEN_WEEK = "p";
    public static final String ODD_WEEK = "i";

    /**
     * Timetable mode:
     */
    public static final int TIMETABLE_GROUP = 0;
    public static final int TIMETABLE_PROF = 1;
}

package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 11/26/2016.
 * Local sqlite database structure and contract
 */

public final class SqliteDatabaseContract {

    public static final String DB_NAME ="usv_timetable.db";
    public static final String DB_TMP_NAME ="usv_timetable.tmp";
    public static int DB_VERSION = 2;

    // tables
    public final static String TIMETABLES_TABLE= "TIMETABLES";
    public final static String COURSES_TABLE = "COURSES";

    // temporary tables
    public final static String COURSES_TMP_TABLE = "_COURSES";

    // COURSES table columns
    public final static String ID = "_id";
    public final static String NAME = "name";
    public final static String FULL_NAME = "full_name";
    public final static String TYPE = "type";
    public final static String LOCATION = "location";
    public final static String FULL_LOCATION = "full_location";
    public final static String START_TIME = "start_time";
    public final static String END_TIME = "end_time";
    public final static String PROF = "prof";
    public final static String PROF_ID = "prof_id";
    public final static String DAY = "day";
    public final static String PARITY = "parity";
    public final static String INFO = "info";
    public static final String COURSE_TIMETABLE_ID = "timetable_id";

    // TIMETABLES table columns
    // ------------------------ _id
    public final static String ENTITY_ID = "entity_id";
    public static final String ENTITY_TYPE = "entity_type";
    public final static String ENTITY_NAME = "entity_name";
}

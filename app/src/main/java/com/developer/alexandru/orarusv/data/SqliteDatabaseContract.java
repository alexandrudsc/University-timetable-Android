package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 11/26/2016.
 * Local sqlite database structure and contract
 */

public final class SqliteDatabaseContract {

    // tables
    public final static String COURSES_TABLE = "COURSES";
    public final static String FACULTIES_TABLE = "FACULTIES";
    public final static String MASTERS_GROUPS_TABLE = "MASTERS_GROUPS";
    public final static String UNDERGRADUATES_GROUPS_TABLE = "UNDERGRADUATES_GROUPS";
    public final static String PHD_GROUPS_TABLE = "PHD_GROUPS";

    // temporary tables
    public final static String COURSES_TMP_TABLE = "_COURSES";
    public final static String FACULTIES_TMP_TABLE = "_FACULTIES";

    // COURSES table columns
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

    // GROUPS table columns
    public static final String GROUP_ID = "ID";
    public static final String FACULTY_FROM_ID = "FACULTY_ID";

    // FACULTIES table columns
    public static final String FACULTY_ID = "_id";
    public static final String FACULTY_LINK = "link";
}

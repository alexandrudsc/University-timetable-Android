package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 8/14/16.
 */
public final class SQLStmtHelper {

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

    // CREATE TABLES statements
    public final static String CREATE_COURSES_TABLE = "CREATE TABLE \"COURSES\"(\n" +
            "  _id INT,\n" +
            "  name TEXT,\n" +
            "  full_name TEXT,\n" +
            "  type TEXT,\n" +
            "  location TEXT,\n" +
            "  full_location TEXT,\n" +
            "  start_time INT,\n" +
            "  end_time INT,\n" +
            "  day TEXT,\n" +
            "  prof TEXT,\n" +
            "  prof_id TEXT,\n" +
            "  parity TEXT,\n" +
            "  info TEXT\n" +
            ");";

    public final static String CREATE_FACULTIES_TABLE = "CREATE TABLE \"FACULTIES\"(_id INT,name TEXT,link TEXT); ";

    public final static String CREATE_UNDERGRADUATES_TABLE = "CREATE TABLE \"UNDERGRADUATES_GROUPS\"(\n" +
            "  _id INT,\n" +
            "  ID INT,\n" +
            "  FACULTY_ID INT,\n" +
            "  name\n" +
            ");";

    public final static String CREATE_MASTERS_TABLE = "CREATE TABLE \"MASTERS_GROUPS\"(\n" +
            "  _id INT,\n" +
            "  ID INT,\n" +
            "  FACULTY_ID INT,\n" +
            "  name\n" +
            ");\n";

    public final static String CREATE_PHD_TABLE = "CREATE TABLE \"PHD_GROUPS\"(\n" +
            "  _id INT,\n" +
            "  ID INT,\n" +
            "  FACULTY_ID INT,\n" +
            "  name\n" +
            ");\n ";

    // DROP TABLES statements
    public final static String DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS " + COURSES_TABLE;
    public final static String DELETE_FACULTIES_TABLE = "DROP TABLE IF EXISTS " + FACULTIES_TABLE;
    public final static String DELETE_UNDERGRADUATES_GROUPS_TABLE = "DROP TABLE IF EXISTS " + UNDERGRADUATES_GROUPS_TABLE;
    public final static String DELETE_MASTERS_GROUPS_TABLE = "DROP TABLE IF EXISTS " + MASTERS_GROUPS_TABLE;
    public final static String DELETE_PHD_GROUPS_TABLE = "DROP TABLE IF EXISTS " + PHD_GROUPS_TABLE;

    // constants
    public static final int UNDERGRADUATES = 0;
    public static final int MASTERS = 1;
    public static final int PHD = 2;

}

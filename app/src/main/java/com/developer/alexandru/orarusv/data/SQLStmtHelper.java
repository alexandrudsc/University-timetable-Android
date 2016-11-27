package com.developer.alexandru.orarusv.data;

/**
 * Created by alexandru on 8/14/16.
 * Sqlite statements helper class
 */
public final class SQLStmtHelper {

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
    public final static String DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS " + SqliteDatabaseContract.COURSES_TABLE;
    public final static String DELETE_FACULTIES_TABLE = "DROP TABLE IF EXISTS " + SqliteDatabaseContract.FACULTIES_TABLE;
    public final static String DELETE_UNDERGRADUATES_GROUPS_TABLE = "DROP TABLE IF EXISTS " + SqliteDatabaseContract.UNDERGRADUATES_GROUPS_TABLE;
    public final static String DELETE_MASTERS_GROUPS_TABLE = "DROP TABLE IF EXISTS " + SqliteDatabaseContract.MASTERS_GROUPS_TABLE;
    public final static String DELETE_PHD_GROUPS_TABLE = "DROP TABLE IF EXISTS " + SqliteDatabaseContract.PHD_GROUPS_TABLE;

    // constants
    public static final int UNDERGRADUATES = 0;
    public static final int MASTERS = 1;
    public static final int PHD = 2;

}

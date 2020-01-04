package com.developer.alexandru.orarusv.data;

/** Created by alexandru on 11/26/2016. Local sqlite database structure and contract */
public final class SqliteDatabaseContract {

  public static final String DB_NAME = "usv_timetable.db";
  public static final String DB_TMP_NAME = "usv_timetable.tmp";
  public static int DB_VERSION = 2;

  // tables
  public static final String TIMETABLES_TABLE = "TIMETABLES";
  public static final String COURSES_TABLE = "COURSES";

  // temporary tables
  public static final String COURSES_TMP_TABLE = "_COURSES";

  // COURSES table columns
  public static final String ID = "_id";
  public static final String NAME = "name";
  public static final String FULL_NAME = "full_name";
  public static final String TYPE = "type";
  public static final String LOCATION = "location";
  public static final String FULL_LOCATION = "full_location";
  public static final String START_TIME = "start_time";
  public static final String END_TIME = "end_time";
  public static final String PROF = "prof";
  public static final String PROF_ID = "prof_id";
  public static final String DAY = "day";
  public static final String PARITY = "parity";
  public static final String INFO = "info";
  public static final String COURSE_TIMETABLE_ID = "timetable_id";

  // TIMETABLES table columns
  // ------------------------ _id
  public static final String ENTITY_ID = "entity_id";
  public static final String ENTITY_TYPE = "entity_type";
  public static final String ENTITY_NAME = "entity_name";
}

package com.developer.alexandru.orarusv.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alexandru on 7/6/14. Helper class managing the various queries for the local database.
 * Allows to CRUD operations upon the database. See SQLStmtHelper for all the data and queries
 */
public class DBAdapter {
  // debug
  public static final boolean D = true;
  public static final String TAG = "DBAdapter";

  private DatabaseOpenHelper dbHelper;
  private SQLiteDatabase database;

  public DBAdapter(Context context) {
    dbHelper = new DatabaseOpenHelper(context);
  }

  // Create a temporary .db file; used when downloading data
  public DBAdapter(Context context, boolean isTemporary) {
    dbHelper = new DatabaseOpenHelper(context, isTemporary);
  }

  public void open() throws SQLiteException {
    if (database == null) {
      database = dbHelper.getWritableDatabase();
    }
  }

  public void close() {
    if (database != null && database.isOpen()) database.close();
  }

  public void create() {
    if (database == null) database = dbHelper.getWritableDatabase();
    dbHelper.onCreate(database);
  }

  /**
   * Delete the timetable and associated courses from the database depending on the id of this
   * timetable
   *
   * @param timetable - timetable to be deleted from the database
   */
  void deleteTimetableAndCourses(Timetable timetable) {
    deleteTimetable(timetable);
  }

  /**
   * Insert the course in the database
   *
   * @param course course to be inserted
   */
  void insertCourse(Course course, int timetableId) {
    ContentValues values = createValuesForInsertingCourse(course);
    values.put(SqliteDatabaseContract.COURSE_TIMETABLE_ID, timetableId);
    try {
      database.insert(SqliteDatabaseContract.COURSES_TABLE, null, values);
    }
    catch (SQLiteException ex) {
      Log.e(TAG, "error inserting course [" + values.toString() + "]" + ex.getMessage());
    }
  }

  /**
   * Insert the timetable in the database. If there is an id with that timetable, just replace the
   * name;
   *
   * @param timetable timetable to be inserted
   */
  void insertTimetable(Timetable timetable) {
    ContentValues values = new ContentValues();

    values.put(SqliteDatabaseContract.ENTITY_ID, timetable.getId());
    values.put(SqliteDatabaseContract.ENTITY_NAME, timetable.getName());
    values.put(SqliteDatabaseContract.ENTITY_TYPE, timetable.getType().ordinal());
    try {
      database.insert(SqliteDatabaseContract.TIMETABLES_TABLE, null, values);
    }
    catch (SQLiteException ex) {
      Log.e(TAG, "error inserting timetable [" + values.toString() + "]" + ex.getMessage());
    }
  }

  // Used only in DbAdapter.java as a helper method.
  // Create a ContentValues object representing a course in the database.
  private ContentValues createValuesForInsertingCourse(Course course) {

    ContentValues values = new ContentValues();
    values.put(SqliteDatabaseContract.NAME, course.getName());
    values.put(SqliteDatabaseContract.FULL_NAME, course.getFullName());
    values.put(SqliteDatabaseContract.TYPE, course.getType());
    values.put(SqliteDatabaseContract.LOCATION, course.getLocation());
    values.put(SqliteDatabaseContract.FULL_LOCATION, course.getFullLocation());
    values.put(SqliteDatabaseContract.START_TIME, course.getStartTime());
    values.put(SqliteDatabaseContract.END_TIME, course.getEndTime());
    values.put(SqliteDatabaseContract.DAY, course.getDay());
    values.put(SqliteDatabaseContract.PROF, course.getProf());
    values.put(SqliteDatabaseContract.PROF_ID, course.getProfID());
    values.put(SqliteDatabaseContract.PARITY, course.getParity());
    values.put(SqliteDatabaseContract.INFO, course.getInfo());
    return values;
  }

  /**
   * Query for all the courses from a day from a week of the semester.
   *
   * @param week the week of the semester
   * @param day the day of the week
   * @return an ArrayList object containing only the courses that respect the type of week (odd or
   *     even) in the right order
   */
  public ArrayList<Course> getCourses(int week, int day, int timetableId) throws SQLiteException {
    String[] mProjection = {
      SqliteDatabaseContract.NAME,
      SqliteDatabaseContract.FULL_NAME,
      SqliteDatabaseContract.TYPE,
      SqliteDatabaseContract.LOCATION,
      SqliteDatabaseContract.FULL_LOCATION,
      SqliteDatabaseContract.START_TIME,
      SqliteDatabaseContract.END_TIME,
      SqliteDatabaseContract.PROF,
      SqliteDatabaseContract.PROF_ID,
      SqliteDatabaseContract.PARITY,
      SqliteDatabaseContract.INFO
    };

    String selection =
        SqliteDatabaseContract.DAY
            + " == ? AND ("
            + SqliteDatabaseContract.PARITY
            + " == ? OR "
            + SqliteDatabaseContract.PARITY
            + " == ? OR "
            + SqliteDatabaseContract.PARITY
            + " == ?) AND "
            + SqliteDatabaseContract.COURSE_TIMETABLE_ID
            + " == ? ";
    String[] selectionArgs = new String[5];
    String orderBy = " CAST (" + SqliteDatabaseContract.START_TIME + " AS INTEGER) ";
    selectionArgs[0] = String.valueOf(day);
    selectionArgs[2] = "-";
    if (week % 2 == 0) selectionArgs[1] = CsvAPI.EVEN_WEEK;
    else selectionArgs[1] = CsvAPI.ODD_WEEK;
    selectionArgs[3] = "10 sapt.+1h";
    selectionArgs[4] = String.valueOf(timetableId);
    Cursor cursor = database.query(
            SqliteDatabaseContract.COURSES_TABLE,
            mProjection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);
    if (cursor == null) return null;
    ArrayList<Course> courses = new ArrayList<>();

    while (cursor.moveToNext()) {
      Course course = getCourseFromCursor(cursor);
      courses.add(course);
    }
    cursor.close();
    return courses;
  }

  private Course getCourseFromCursor(Cursor cursor) {
    if (cursor == null) return null;
    String name, fullName, type, time, info, location, fullLocation, parity, prof, profID;
    name = cursor.getString(0);
    fullName = cursor.getString(1);
    type = cursor.getString(2);
    location = cursor.getString(3);
    fullLocation = cursor.getString(4);
    time = cursor.getString(5) + ":00 - " + cursor.getString(6) + ":00";
    prof = cursor.getString(7);
    profID = cursor.getString(8);
    parity = cursor.getString(9);
    info = cursor.getString(10);

    return new Course(
        name, fullName, type, location, fullLocation, time, prof, profID, parity, info);
  }

  private Timetable getTimetableFromCursor(Cursor cursor) {
    if (cursor == null) return null;
    Timetable timetable = null;
    try {
      timetable =
          Timetable.Creator.INSTANCE.create(
              new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2)});
    } catch (Exception ex) {
      Log.d(TAG, ex.toString());
    }
    return timetable;
  }

  // Used by content provider for search suggestions
  Cursor fullQuery(
      String[] projection, String selection, String[] selectionArgs, String order) {
    if (D) Log.d(TAG, "query for search");
    if (D) Log.d(TAG, selection + " " + selectionArgs[0] + " " + order);
    String[] selArgs = new String[] {selectionArgs[0] + "%"};
    return database.query(
        SqliteDatabaseContract.COURSES_TABLE, projection, selection, selArgs, null, null, order);
  }

  /**
   * Query for a row in the courses table. Used by SearchableActivity when searching was made
   * specifically
   *
   * @param name the name of the course
   * @param type the type of the course (COURSE, LAB or SEMINAR)
   * @return a Course object
   */
  public Course getCourse(String name, String type) {
    if (name == null || type == null) return null;

    String[] mProjection = {
      SqliteDatabaseContract.NAME,
      SqliteDatabaseContract.FULL_NAME,
      SqliteDatabaseContract.TYPE,
      SqliteDatabaseContract.LOCATION,
      SqliteDatabaseContract.FULL_LOCATION,
      SqliteDatabaseContract.START_TIME,
      SqliteDatabaseContract.END_TIME,
      SqliteDatabaseContract.PROF,
      SqliteDatabaseContract.PROF_ID,
      SqliteDatabaseContract.PARITY,
      SqliteDatabaseContract.INFO
    };

    String selection = "LOWER(name) == ? AND LOWER(type) == ?";
    String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
    Cursor result =
        database.query(
            SqliteDatabaseContract.COURSES_TABLE,
            mProjection,
            selection,
            selectionArgs,
            null,
            null,
            null);
    if (result == null || result.getCount() == 0) return null;
    result.moveToFirst();
    return getCourseFromCursor(result);
  }

  /**
   * Updates a course time and day in the database
   *
   * @param newValue the new course object
   */
  void updateCourseTime(Course newValue) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(SqliteDatabaseContract.DAY, newValue.getDay());
    contentValues.put(SqliteDatabaseContract.START_TIME, newValue.getStartTime());
    contentValues.put(SqliteDatabaseContract.END_TIME, newValue.getEndTime());
    database.update(
        SqliteDatabaseContract.COURSES_TABLE,
        contentValues,
        SqliteDatabaseContract.NAME
            + " = '"
            + newValue.getName()
            + "' "
            + " AND "
            + SqliteDatabaseContract.TYPE
            + " = '"
            + newValue.getType()
            + "' ",
        null);
  }

  public boolean isOpen() {
    return database.isOpen();
  }

  public ArrayList<Timetable> getAllTimetables() {
    ArrayList<Timetable> allTimetables = new ArrayList<>();

    String[] mProjection = {
      SqliteDatabaseContract.ENTITY_TYPE,
      SqliteDatabaseContract.ENTITY_ID,
      SqliteDatabaseContract.ENTITY_NAME
    };

    Cursor result =
        database.query(
            SqliteDatabaseContract.TIMETABLES_TABLE, mProjection, null, null, null, null, null);
    if (result == null || result.getCount() == 0) return allTimetables;
    while (result.moveToNext()) {
      allTimetables.add(getTimetableFromCursor(result));
    }
    result.close();
    return allTimetables;
  }

  /**
   * Executes a delete query on the timetable table which will delete in cascade all courses from
   * the COURSES table which have as foreign key the id of that timetable
   *
   * @param timetable - the timetable to be deleted
   */
  public void deleteTimetable(Timetable timetable) {
    database.delete(
            SqliteDatabaseContract.TIMETABLES_TABLE,
            SqliteDatabaseContract.ENTITY_ID + "=" + timetable.getId(),
            null);
  }
}

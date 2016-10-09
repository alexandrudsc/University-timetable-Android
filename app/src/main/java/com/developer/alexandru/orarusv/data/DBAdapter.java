package com.developer.alexandru.orarusv.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.developer.alexandru.orarusv.view_pager.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Alexandru on 7/6/14.
 * Helper class managing the various queries for the local database.
 * Allows to CRUD operations upon the database.
 * See SQLStmtHelper for all the data and queries
 */
public class DBAdapter {
    //debug
    public static final boolean D = true;
    public static final String TAG = "DBAdapter";

    public static final String DB_NAME ="usv_timetable.db";
    public static final String DB_TMP_NAME ="usv_timetable.tmp";
    private static String DB_DIR;
    public static int DB_VER = 1;


    private DBOpenHelper dbHelper;
    private SQLiteDatabase database;

    public DBAdapter(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    // Create a temporary .db file; used when downloading data
    public DBAdapter(Context context, boolean isTemporary) {
        dbHelper = new DBOpenHelper(context, isTemporary);
    }

    public void open() throws SQLiteException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null && database.isOpen())
            database.close();
    }

    public void create(){
        if(database == null)
            database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);
    }

    /**
     * Create a temporary table to insert downloading courses: if there are any courses downloaded
     * this will not block the access to the current courses, and it used as safety if network access fails during com.developer.alexandru.orarusv.download
     */
    protected void createTMPCoursesTable(){
        if (!dbHelper.tableExists(database, SQLStmtHelper.COURSES_TMP_TABLE)) {
            if (!dbHelper.tableExists(database, SQLStmtHelper.COURSES_TABLE))
                database.execSQL(SQLStmtHelper.CREATE_COURSES_TABLE);
            database.execSQL("CREATE TABLE " + SQLStmtHelper.COURSES_TMP_TABLE + " AS SELECT * FROM " + SQLStmtHelper.COURSES_TABLE);
        }
        database.delete(SQLStmtHelper.COURSES_TMP_TABLE, null, null);           // DELETE the rows in the tmp table
    }

    protected void deleteTMPCourses(){
        database.execSQL("DROP TABLE IF EXISTS " + SQLStmtHelper.COURSES_TMP_TABLE);
    }

    /**
     * Before calling this function there should be 2 databases, an old one and the newly created one
     * Replace the old one, as there is no need for it
     * @return true if operation was successful, false otherwise
     */
    protected boolean replaceOldCourses(){
        database.execSQL(SQLStmtHelper.DELETE_COURSES_TABLE);
        database.execSQL("ALTER TABLE " + SQLStmtHelper.COURSES_TMP_TABLE + " RENAME TO " + SQLStmtHelper.COURSES_TABLE);
        return true;
    }

    /**
     * Create a temporary table to insert downloading faculties: if there are any faculties  downloaded
     * this will not block the access to the current faculties, and it used as safety if network access fails during com.developer.alexandru.orarusv.download
     */
    protected void createTMPFaculties(){
        if (!dbHelper.tableExists(database, SQLStmtHelper.FACULTIES_TABLE))
            database.execSQL(SQLStmtHelper.CREATE_FACULTIES_TABLE);
        database.execSQL("CREATE TABLE " + SQLStmtHelper.FACULTIES_TMP_TABLE + " AS SELECT * FROM " + SQLStmtHelper.FACULTIES_TABLE);
        database.delete(SQLStmtHelper.FACULTIES_TMP_TABLE, null, null);
    }

    /**
     * Delete the courses table from the database
     */
    public void deleteCourses(){
        database.execSQL(SQLStmtHelper.DELETE_COURSES_TABLE);
    }

    /**
     * Delete the faculties table and the table describing the structures of groups
     */
    public void deleteFaculties(){
        database.execSQL(SQLStmtHelper.DELETE_FACULTIES_TABLE);
        database.execSQL(SQLStmtHelper.DELETE_UNDERGRADUATES_GROUPS_TABLE);
        database.execSQL(SQLStmtHelper.DELETE_MASTERS_GROUPS_TABLE);
        database.execSQL(SQLStmtHelper.DELETE_PHD_GROUPS_TABLE);
    }

    /**
     * Insert the course in the database
     * @param course course to be inserted
     * @param day the day of this course
     * @return the ID of the row if insertion was successful, -1 otherwise
     */
    public long insertCourse(Course course, String day){

        ContentValues values = createValuesForInserting(course, day);

        return database.insert(SQLStmtHelper.COURSES_TABLE, null, values);
    }

    /**
     * Same as insertCourse() except data is inserted into a temporary table. Used when downloading new courses.
     * @param course course to be inserted
     * @param day the day of this course
     * @return the ID of the row if insertion was successful, -1 otherwise
     */
    public long insertTmpCourse(Course course, String day){
        ContentValues values = createValuesForInserting(course, day);

        return database.insert(SQLStmtHelper.COURSES_TMP_TABLE, null, values);
    }

    /**
     * Replace the course1 with the course 2 (course 2 will be placed in day of week passed by the "day" param)
     * Used when user customizes his timetable.
     * @param course1 the course currently in the database
     * @param course2 the new course
     * @param day the day of the new course
     * @return the number of rows affected
     */
    public int replaceCourse(Course course1, Course course2, String day){

        ContentValues values = createValuesForInserting(course2, day);

        String whereClause = "name = ? AND type = ?";
        String[] whereArgs = {course1.name, course1.type};
        return database.update(SQLStmtHelper.COURSES_TABLE, values, whereClause, whereArgs);
    }

    // Used only in DbAdapter.java as a helper method.
    // Create a ContentValues object representing a course in the database.
    private ContentValues createValuesForInserting(Course course, String day){

        ContentValues values = new ContentValues();
        values.put(SQLStmtHelper.NAME, course.name);
        values.put(SQLStmtHelper.FULL_NAME, course.fullName);
        values.put(SQLStmtHelper.TYPE, course.type);
        values.put(SQLStmtHelper.LOCATION, course.location);
        values.put(SQLStmtHelper.FULL_LOCATION, course.fullLocation);
        values.put(SQLStmtHelper.START_TIME, course.startTime);
        values.put(SQLStmtHelper.END_TIME, course.endTime);
        values.put(SQLStmtHelper.DAY, day);
        values.put(SQLStmtHelper.PROF, course.prof);
        values.put(SQLStmtHelper.PROF_ID, course.profID);
        values.put(SQLStmtHelper.PARITY, course.parity);
        values.put(SQLStmtHelper.INFO, course.info);
        return values;
    }

    /**
     * Insert a faculty in the database
     * @param _id the ID of the faculty
     * @param name the name of the faculty
     * @param link the link to courses (if necessary)
     * @return the ID of the row if insertion was successful, -1 otherwise
     */
    public long insertFaculty(int _id, String name, String link){
        ContentValues values = new ContentValues();
        values.put(SQLStmtHelper.FACULTY_ID, _id);
        values.put(SQLStmtHelper.NAME, name);
        values.put(SQLStmtHelper.FACULTY_LINK, link);
        return database.insert(SQLStmtHelper.FACULTIES_TABLE, null, values);
    }

    /**
     * Insert a group (students from a certain faculty; a faculty can have multiple groups) in the database
     * @param table table where to insert (UNDERGRADUATES, MASTERS, PHD)
     * @param name the the name of the group
     * @param groupID the ID of the group
     * @param facultyID the ID of the faculty containing this group
     * @return the ID of the row if insertion was successful, -1 otherwise
     */
    public long insertGroup(String table, String name, int groupID, int facultyID){
        ContentValues values = new ContentValues();
        values.put(SQLStmtHelper.GROUP_ID, groupID);
        values.put(SQLStmtHelper.FACULTY_FROM_ID, facultyID);
        values.put(SQLStmtHelper.NAME, name);
        return database.insert(table, null, values);
    }


    /**
     * Query for all the faculties
     * @return a Cursor containing the data
     */
    public Cursor getFaculties(){

        return database.query(SQLStmtHelper.FACULTIES_TABLE,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
    }

    /**`
     * Query for all the groups within a faculty.
     * @param facultyID the ID of the faculty
     * @param type the type of groups (UNDERGRADUATES, MASTERS, PHD)
     * @return a Cursor object containing the data
     */
    public Cursor getGroupsFromFaculty(int facultyID, int type){
        String table;
        String mSelection = SQLStmtHelper.FACULTY_FROM_ID + " = ?";
        String[] mSelectionArgs = {String.valueOf(facultyID)};
        switch (type){
            case SQLStmtHelper.UNDERGRADUATES:
                table = SQLStmtHelper.UNDERGRADUATES_GROUPS_TABLE;
                break;
            case SQLStmtHelper.MASTERS:
                table = SQLStmtHelper.MASTERS_GROUPS_TABLE;
                break;
            case SQLStmtHelper.PHD:
                table = SQLStmtHelper.PHD_GROUPS_TABLE;
                break;
            default:
                table = null;
        }
        if (table == null)
            return null;
        return database.query(table,
                              null,
                              mSelection,
                              mSelectionArgs,
                              null,
                              null,
                              null);
    }

    /**
     * Query for all the courses from a day from a week of the semester.
     * @param week the week of the semester
     * @param day the day of the week
     * @return an ArrayList object containing only the courses that respect the type of week (odd or even) in the right order
     */
    public ArrayList<Course> getCourses(int week, int day) throws SQLiteException{
        String[] mProjection = {SQLStmtHelper.NAME,
                SQLStmtHelper.FULL_NAME,
                SQLStmtHelper.TYPE,
                SQLStmtHelper.LOCATION,
                SQLStmtHelper.FULL_LOCATION,
                SQLStmtHelper.START_TIME,
                SQLStmtHelper.END_TIME,
                SQLStmtHelper.PROF,
                SQLStmtHelper.PROF_ID,
                SQLStmtHelper.PARITY,
                SQLStmtHelper.INFO
        };

        String selection = SQLStmtHelper.DAY + " == ? AND (" + SQLStmtHelper.PARITY + " == ? OR " + SQLStmtHelper.PARITY + " == ? OR " +
                                                SQLStmtHelper.PARITY + " == ?)";
        String[] selectionArgs = new String[4];
        String orderBy = SQLStmtHelper.START_TIME;
        selectionArgs[0] = String.valueOf(day);
        selectionArgs[2] = "-";
        if (week % 2 == 0)
            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_EVEN_WEEK;
        else
            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_ODD_WEEK;
        selectionArgs[3] = "10 sapt.+1h";
        Cursor cursor = null;
        cursor = database.query(SQLStmtHelper.COURSES_TABLE,
                mProjection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);
        if (cursor == null)
            return null;
        ArrayList<Course> courses = new ArrayList<>();

        while (cursor.moveToNext()) {
            Course course = getCourseFromCursor(cursor);
            courses.add(course);

        }
        cursor.close();
        return courses;
    }

    private Course getCourseFromCursor(Cursor cursor) {
        if (cursor == null)
            return null;
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

        return new Course(name, fullName, type, location, fullLocation,
                time, prof, profID, parity, info);
    }

    // Used by content provider for search suggestions
    public Cursor fullQuery(String[] projection, String selection, String[] selectionArgs, String order){
        if (D) Log.d(TAG, "query for search");
        if (D) Log.d(TAG, selection + " " + selectionArgs[0] + " " + order);
        String[] selArgs = new String[]{selectionArgs[0]+"%"};
        return database.query(SQLStmtHelper.COURSES_TABLE,
                            projection,
                            selection,
                            selArgs,
                            null,
                            null,
                            order);
    }

    /**
     * Query for all the fields of a specific row. NOT USED
     * @param name the name of the course
     * @param type the type of the course (COURSE, LAB or SEMINAR)
     * @return an array of strings with the fields
     */
    public String[] getInfoAboutCourse(String name, String type){
        //Used by SearchableActivity when searching was made specifically
        String[] projection={ SQLStmtHelper.NAME,
                SQLStmtHelper.FULL_NAME,
                SQLStmtHelper.TYPE,
                SQLStmtHelper.LOCATION,
                SQLStmtHelper.FULL_LOCATION,
                SQLStmtHelper.START_TIME,
                SQLStmtHelper.PROF,
                SQLStmtHelper.PARITY,
                SQLStmtHelper.INFO
        };
        String selection = "LOWER(name) == ? AND LOWER(type) == ?";
        String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
        Cursor result = database.query(SQLStmtHelper.COURSES_TABLE,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null);
        if(result == null || result.getCount() == 0)
            return null;
        result.moveToFirst();
        return new String[]{result.getString(0), result.getString(1), result.getString(2)};
    }

    /**
     * Query for a row in the courses table. Used by SearchableActivity when searching was made specifically
     * @param name the name of the course
     * @param type the type of the course (COURSE, LAB or SEMINAR)
     * @return a Course object
     */
    public Course getCourse(String name, String type){
        if(name == null || type == null)
            return null;

        String[] mProjection={ SQLStmtHelper.NAME,
                SQLStmtHelper.FULL_NAME,
                SQLStmtHelper.TYPE,
                SQLStmtHelper.LOCATION,
                SQLStmtHelper.FULL_LOCATION,
                SQLStmtHelper.START_TIME,
                SQLStmtHelper.END_TIME,
                SQLStmtHelper.PROF,
                SQLStmtHelper.PROF_ID,
                SQLStmtHelper.PARITY,
                SQLStmtHelper.INFO
        };

        String selection = "LOWER(name) == ? AND LOWER(type) == ?";
        String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
        Cursor result = database.query(SQLStmtHelper.COURSES_TABLE,
                mProjection,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if(result == null || result.getCount() == 0)
            return null;
        result.moveToFirst();
        return  getCourseFromCursor(result);

    }

    public boolean isOpen(){
        return database.isOpen();
    }

    private class DBOpenHelper extends SQLiteOpenHelper{

        public DBOpenHelper(Context context){
                super(context, DB_NAME, null, DB_VER);
        }

        public DBOpenHelper(Context context, boolean isTemporary){
            super(context, DB_TMP_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            if (!tableExists(sqLiteDatabase, SQLStmtHelper.COURSES_TABLE))
                sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_COURSES_TABLE);
            if (!tableExists(sqLiteDatabase, SQLStmtHelper.FACULTIES_TABLE))
                sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_FACULTIES_TABLE);
            if (!tableExists(sqLiteDatabase, SQLStmtHelper.UNDERGRADUATES_GROUPS_TABLE))
                sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_UNDERGRADUATES_TABLE);
            if (!tableExists(sqLiteDatabase, SQLStmtHelper.MASTERS_GROUPS_TABLE))
                sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_MASTERS_TABLE);
            if (!tableExists(sqLiteDatabase, SQLStmtHelper.PHD_GROUPS_TABLE))
                sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_PHD_TABLE);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, newVersion, oldVersion);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            DB_VER = newVersion;
            sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_COURSES_TABLE);
            sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_FACULTIES_TABLE);
            sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_UNDERGRADUATES_GROUPS_TABLE);
            sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_MASTERS_GROUPS_TABLE);
            sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_PHD_GROUPS_TABLE);

            onCreate(sqLiteDatabase);
        }
        // Used to avoid unnecessary creation of tables
        private boolean tableExists(SQLiteDatabase sqLiteDatabase, String table){
            String[] mProj ={"name"};

            String mSelect = "type='table' AND name=?";
            String mSelectArgs[] ={table};
            Cursor c = sqLiteDatabase.query("sqlite_master",
                                            mProj,
                                            mSelect,
                                            mSelectArgs,
                                            null,
                                            null,
                                            null);
            Log.d("DB_ADAPTER", c.getCount() + "");
            return c.getCount() > 0;
        }
    }
}

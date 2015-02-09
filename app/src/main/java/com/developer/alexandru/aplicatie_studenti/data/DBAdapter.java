package com.developer.alexandru.aplicatie_studenti.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Alexandru on 7/6/14.
 * Helper class managing the various queries for the local database
 */
public class DBAdapter {
    //debug
    public static final boolean D = true;
    public static final String TAG = "DBAdapter";

    public static final String DB_NAME ="usv_timetable.db";
    public static int DB_VER = 1;

    //Tables
    public static final String COURSES_TABLE = "COURSES";
    public static final String FACULTIES_TABLE = "FACULTIES";
    public static final String UNDERGRADUATES_GROUPS_TABLE = "UNDERGRADUATES_GROUPS";
    public static final String MASTERS_GROUPS_TABLE = "MASTERS_GROUPS";
    public static final String PHD_GROUPS_TABLE = "PHD_GROUPS";

    //Common columns of tables
    public static final String ID = "_id";
    public static final String NAME = "name";

    //Columns of courses' table
    public static final String FULL_NAME = "full_name";
    public static final String TYPE = "type";
    public static final String LOCATION = "location";
    public static final String TIME = "time";
    public static final String DAY = "day";
    public static final String PROF = "prof";
    public static final String INFO = "info";

    //Columns of faculties' table
    public static final String LINK = "link";
    public static final String FACULTY_ID = "_id";

    //Columns for groups' table
    public static final String GROUP_ID = "ID";
    public static final String FACULTY_FROM_ID = "FACULTY_ID";
    public static final int UNDERGRADUATES = 0;
    public static final int MASTERS = 1;
    public static final int PHD = 2;

    // SQLite statements
    private static final String TYPE_TEXT_NOT_NULL = " TEXT NOT NULL";
    private static final String TYPE_INTEGER_NOT_NULL = " INTEGER NOT NULL";
    private static final String COMMA  = ",";
    private static final String END_STATEMENT = " );" ;

    //SQLite statements for courses table
    private static final String CREATE_COURSES_TABLE = "CREATE TABLE " + COURSES_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY," +
            NAME + TYPE_TEXT_NOT_NULL + COMMA +
            FULL_NAME + " TEXT" + COMMA +
            TYPE + " TEXT," +
            LOCATION + TYPE_TEXT_NOT_NULL + COMMA +
            TIME + TYPE_TEXT_NOT_NULL + COMMA +
            DAY + TYPE_TEXT_NOT_NULL + COMMA +
            PROF + " TEXT,"+
            INFO + TYPE_TEXT_NOT_NULL + END_STATEMENT;
    private static final String DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS "+ COURSES_TABLE;

    //SQLite statements for faculties table
    private static final String CREATE_FACULTIES_TABLE = "CREATE TABLE " + FACULTIES_TABLE + " (" +
            ID + TYPE_INTEGER_NOT_NULL + COMMA +
            NAME + TYPE_TEXT_NOT_NULL + COMMA +
            LINK + " TEXT" + END_STATEMENT;
    private static final String DELETE_FACULTIES_TABLE = "DROP TABLE IF EXISTS "+ FACULTIES_TABLE;

    //SQLite statements for groups
    private static final String CREATE_UNDERGRADUATES_TABLE = "CREATE TABLE " + UNDERGRADUATES_GROUPS_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY," +
            GROUP_ID + " INTEGER NOT NULL," +
            FACULTY_FROM_ID + " INTEGER NOT NULL," +
            NAME + END_STATEMENT;
    private static final String CREATE_MASTERS_TABLE = "CREATE TABLE " + MASTERS_GROUPS_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY," +
            GROUP_ID + " INTEGER NOT NULL," +
            FACULTY_FROM_ID + " INTEGER NOT NULL," +
            NAME + END_STATEMENT;
    private static final String CREATE_PHD_TABLE = "CREATE TABLE " + PHD_GROUPS_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY," +
            GROUP_ID + " INTEGER NOT NULL," +
            FACULTY_FROM_ID + " INTEGER NOT NULL," +
            NAME + END_STATEMENT;
    private static final String DELETE_UNDERGRADUATES_GROUPS_TABLE = "DROP TABLE IF EXISTS "+ UNDERGRADUATES_GROUPS_TABLE;
    private static final String DELETE_MASTERS_GROUPS_TABLE = "DROP TABLE IF EXISTS "+ MASTERS_GROUPS_TABLE;
    private static final String DELETE_PHD_GROUPS_TABLE = "DROP TABLE IF EXISTS "+ PHD_GROUPS_TABLE;

    private DBOpenHelper dbHelper;
    private SQLiteDatabase database;

    public DBAdapter(Context context) {
        dbHelper = new DBOpenHelper(context);
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

    public void deleteCourses(){
        database.execSQL(DELETE_COURSES_TABLE);
    }

    public void deleteFaculties(){
        database.execSQL(DELETE_FACULTIES_TABLE);
        database.execSQL(DELETE_UNDERGRADUATES_GROUPS_TABLE);
        database.execSQL(DELETE_MASTERS_GROUPS_TABLE);
        database.execSQL(DELETE_PHD_GROUPS_TABLE);
    }

    public long insertCourse(Course course, String day){

        ContentValues values = new ContentValues();
        values.put(NAME, course.name);
        values.put(FULL_NAME, course.fullName);
        values.put(TYPE, course.type);
        values.put(LOCATION, course.location);
        values.put(TIME, course.time);
        values.put(DAY, day);
        values.put(PROF, course.prof);
        values.put(INFO, course.info);
        return database.insert(COURSES_TABLE, null, values);
    }

    public long insertFaculty(int _id, String name, String link){
        ContentValues values = new ContentValues();
        values.put(FACULTY_ID, _id);
        values.put(NAME, name);
        values.put(LINK, link);
        return database.insert(FACULTIES_TABLE, null, values);
    }

    public long insertGroup(String table, String name, int groupID, int facultyID){
        ContentValues values = new ContentValues();
        values.put(GROUP_ID, groupID);
        values.put(FACULTY_FROM_ID, facultyID);
        values.put(NAME, name);
        return database.insert(table, null, values);
    }

    public Cursor getFaculties(){

        return database.query(FACULTIES_TABLE,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
    }

    public Cursor getGroupsFromFaculty(int facultyID, int type){
        String table;
        String mSelection = FACULTY_FROM_ID + " = ?";
        String[] mSelectionArgs = {String.valueOf(facultyID)};
        switch (type){
            case UNDERGRADUATES:
                table = UNDERGRADUATES_GROUPS_TABLE;
                break;
            case MASTERS:
                table = MASTERS_GROUPS_TABLE;
                break;
            case PHD:
                table = PHD_GROUPS_TABLE;
                break;
            default:
                table = null;
        }
        return database.query(table,
                              null,
                              mSelection,
                              mSelectionArgs,
                              null,
                              null,
                              null);
    }

    public ArrayList<Course> getCourses(int week, String day){
        String[] mProjection={ "name",
                               "full_name",
                               "type",
                               "location",
                               "time",
                               "prof",
                               "info"
        };

        String selection ="day == ? AND ( info == ? OR info == ? OR info == ?)";
        String[] selectionArgs = new String[4];
        String orderBy = "time";
        selectionArgs[0] = day;
        selectionArgs[2] = "";
        if(week % 2 == 0)
            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_EVEN_WEEK;
        else
            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_ODD_WEEK;
        selectionArgs[3] = "10 sapt.+1h";
        Cursor cursor = database.query(COURSES_TABLE,
                                       mProjection,
                                       selection,
                                       selectionArgs,
                                       null,
                                       null,
                                       orderBy);
        if(cursor == null)
            return null;
        ArrayList<Course> courses = new ArrayList<Course>();
        String name, fullName, type, time, info, location, prof;
        while(cursor.moveToNext()){
            name = cursor.getString(0);
            fullName = cursor.getString(1);
            type = cursor.getString(2);
            location = cursor.getString(3);
            time = cursor.getString(4);
            prof = cursor.getString(5);
            info = cursor.getString(6);

            courses.add(new Course(name, fullName, type, location, time, prof, info));

        }
        return courses;
    }

    public Cursor fullQuery(String[] projection, String selection, String[] selectionArgs, String order){
        // Used by content provider for search suggestions
        if (D) Log.d(TAG, "query for search");
        if (D) Log.d(TAG, selection + " " + selectionArgs[0] + " " + order);
        String[] selArgs = new String[]{selectionArgs[0]+"%"};
        return database.query(COURSES_TABLE,
                            projection,
                            selection,
                            selArgs,
                            null,
                            null,
                            order);
    }

    public String[] getInfoAboutCourse(String name, String type){
        //Used by SearchableActivity when searching was made specifically
        String[] projection={ "name",
                            "full_name",
                            "type",
                            "location",
                            "time",
                            "prof",
                            "info"
        };
        String selection = "LOWER(name) == ? AND LOWER(type) == ?";
        String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
        Cursor result = database.query(COURSES_TABLE,
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

    public Course getCourse(String name, String type){
        //Used by SearchableActivity when searching was made specifically

        if(name == null || type == null)
            return null;

        String[] mProjection={ "name",
                "full_name",
                "type",
                "location",
                "time",
                "prof",
                "info"
        };

        String selection = "LOWER(name) == ? AND LOWER(type) == ?";
        String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
        Cursor result = database.query(COURSES_TABLE,
                mProjection,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if(result == null || result.getCount() == 0)
            return null;
        result.moveToFirst();
        return  new Course(result.getString(0), result.getString(1), result.getString(2),
                            result.getString(3), result.getString(4), result.getString(5),
                            result.getString(6));

    }

    public boolean isOpen(){
        return database.isOpen();
    }

    private class DBOpenHelper extends SQLiteOpenHelper{

        public DBOpenHelper(Context context){
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            if (!tableExists(sqLiteDatabase, COURSES_TABLE))
                sqLiteDatabase.execSQL(CREATE_COURSES_TABLE);
            if (!tableExists(sqLiteDatabase, FACULTIES_TABLE))
                sqLiteDatabase.execSQL(CREATE_FACULTIES_TABLE);
            if (!tableExists(sqLiteDatabase, UNDERGRADUATES_GROUPS_TABLE))
                sqLiteDatabase.execSQL(CREATE_UNDERGRADUATES_TABLE);
            if (!tableExists(sqLiteDatabase, MASTERS_GROUPS_TABLE))
                sqLiteDatabase.execSQL(CREATE_MASTERS_TABLE);
            if (!tableExists(sqLiteDatabase, PHD_GROUPS_TABLE))
                sqLiteDatabase.execSQL(CREATE_PHD_TABLE);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, newVersion, oldVersion);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            DB_VER = newVersion;
            sqLiteDatabase.execSQL(DELETE_COURSES_TABLE);
            sqLiteDatabase.execSQL(DELETE_FACULTIES_TABLE);
            sqLiteDatabase.execSQL(DELETE_UNDERGRADUATES_GROUPS_TABLE);
            sqLiteDatabase.execSQL(DELETE_MASTERS_GROUPS_TABLE);
            sqLiteDatabase.execSQL(DELETE_PHD_GROUPS_TABLE);

            onCreate(sqLiteDatabase);
        }

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

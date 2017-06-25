package com.developer.alexandru.orarusv.data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.developer.alexandru.orarusv.data.SqliteDatabaseContract.DB_NAME;
import static com.developer.alexandru.orarusv.data.SqliteDatabaseContract.DB_TMP_NAME;
import static com.developer.alexandru.orarusv.data.SqliteDatabaseContract.DB_VERSION;

/**
 * Created by alexandru on 11/26/2016.
 * Local sqlite database open/update helper
 */

public final class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseOpenHelper(Context context, boolean isTemporary){
        super(context, DB_TMP_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.COURSES_TABLE))
            sqLiteDatabase.execSQL(SQLStmtHelper. CREATE_COURSES_TABLE);
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.TIMETABLES_TABLE))
            sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_TIMETABLES_TABLE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, newVersion, oldVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        DB_VERSION = newVersion;
        sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_COURSES_TABLE);
        sqLiteDatabase.execSQL(SQLStmtHelper.DELETE_TIMETABLES_TABLE);
        onCreate(sqLiteDatabase);
    }
    // Used to avoid unnecessary creation of tables
    public boolean tableExists(SQLiteDatabase sqLiteDatabase, String table){
        String[] mProj ={"name"};

        String mSelect = "type='table' AND name=?";
        String mSelectArgs[] ={table};
        final Cursor cursor = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + table + "'", null);
        return cursor != null && cursor.getCount() > 0;
//        Cursor c = sqLiteDatabase.query("sqlite_master",
//                mProj,
//                mSelect,
//                mSelectArgs,
//                null,
//                null,
//                null);
//        Log.d("DB_ADAPTER", c.getCount() + "");
//        return c.getCount() > 0;
    }
}

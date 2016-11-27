package com.developer.alexandru.orarusv.data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.developer.alexandru.orarusv.data.DBAdapter.DB_NAME;
import static com.developer.alexandru.orarusv.data.DBAdapter.DB_TMP_NAME;
import static com.developer.alexandru.orarusv.data.DBAdapter.DB_VER;

/**
 * Created by alexandru on 11/26/2016.
 * Local sqlite database open/update helper
 */

public final class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    public DatabaseOpenHelper(Context context, boolean isTemporary){
        super(context, DB_TMP_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.COURSES_TABLE))
            sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_COURSES_TABLE);
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.FACULTIES_TABLE))
            sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_FACULTIES_TABLE);
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.UNDERGRADUATES_GROUPS_TABLE))
            sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_UNDERGRADUATES_TABLE);
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.MASTERS_GROUPS_TABLE))
            sqLiteDatabase.execSQL(SQLStmtHelper.CREATE_MASTERS_TABLE);
        if (!tableExists(sqLiteDatabase, SqliteDatabaseContract.PHD_GROUPS_TABLE))
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
    public boolean tableExists(SQLiteDatabase sqLiteDatabase, String table){
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

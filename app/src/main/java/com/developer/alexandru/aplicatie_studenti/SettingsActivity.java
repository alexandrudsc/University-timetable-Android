package com.developer.alexandru.aplicatie_studenti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.developer.alexandru.aplicatie_studenti.data.DBAdapter;
import com.developer.alexandru.aplicatie_studenti.data.SyncFinishedReceiver;
import com.developer.alexandru.aplicatie_studenti.data.Synchronizer;
import com.developer.alexandru.aplicatie_studenti.data.TimetableDownloader;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Created by Alexandru on 7/30/14.
 * Download page.Gets strings to form url then starts AsyncTask.
 * Preferences associated with this activity contain data about the user (settings activity)
 */

public class SettingsActivity extends FragmentActivity {

    // Partial URl for non_modular timetables
    public static final String PARTIAL_TIMETABLE_URL = "http://orar.usv.ro/vizualizare/orarSPG.php?mod=grupa&back=&mod2=vizual&print=da&ID=";

    // Dialog displayed while downloading
    AlertDialog dialog;

    // Three spinners: undergraduates, masters, phd
    Spinner groupUndergraduates, groupMasters, groupPhd;

    // Main spinner for choosing faculty. Hint: always visible
    Spinner faculties;;

    SharedPreferences prefs;
    String studiesLevel;
    int facultyID;
    // Local receiver for sync finished event
    private SyncFinishedReceiver syncFinishedReceiver;

    // Preferences
    private static final String FACULTY_NAME_PREF = "faculty";
    private static final String FACULTY_ID_PREF = "faculty_id";
    private static final String GROUP_NAME_PREF = "group_name";
    private static final String GROUP_ID_PREF = "group_id";
    private static final String LEVEL_PREF = "studies_level";
    private static final String LICENCE = "licence";
    private static final String MASTERS = "masters";
    private static final String PHD = "phd";

    DBAdapter dbAdapter;

    //Columns for faculties and groups database query
    public static int ID = 0;
    public static int FACULTY_NAME = 1;
    public static int GEOUP_NAME = 3;
    public static int LINK = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getPreferences(MODE_PRIVATE);
        facultyID = prefs.getInt(FACULTY_ID_PREF, 1);

        setContentView(R.layout.activity_settings);

        // The helper for SQLite database operations
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();

        // Spinners used by the user
        faculties = (Spinner)findViewById(R.id.spinner_faculty);
        groupUndergraduates = (Spinner)findViewById(R.id.spinner_group_undergraduate);
        groupMasters = (Spinner)findViewById(R.id.spinner_group_master);
        groupPhd = (Spinner)findViewById(R.id.spinner_group_phd);

        // Retrieve the data and create the adapters for the four spinners
        // Faculties spinner
        ArrayList<Elem> facultiesNames = faculties(dbAdapter);
        ArrayAdapter<Elem> facultiesAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, facultiesNames);
        faculties.setAdapter(facultiesAdapter);
        // Reset the last selected faculty
        faculties.setSelection(facultiesNames.indexOf(new Elem(facultyID)));
        faculties.setOnItemSelectedListener(new FacultySelected());

        ArrayList<Elem> groupsUndergraduates = new ArrayList<Elem>();
        ArrayList<Elem> groupsMasters = new ArrayList<Elem>();
        ArrayList<Elem> groupsPhd = new ArrayList<Elem>();

        ArrayAdapter<Elem> groupUndergrAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, groupsUndergraduates);
        ArrayAdapter<Elem> groupMastersAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, groupsMasters);
        ArrayAdapter<Elem> groupPhdAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, groupsPhd);

        groupUndergrAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        groupMastersAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        groupPhdAdapter.setDropDownViewResource(R.layout.spinner_item_layout);

        groupUndergraduates.setAdapter(groupUndergrAdapter);
        groupMasters.setAdapter(groupMastersAdapter);
        groupPhd.setAdapter(groupPhdAdapter);

        // Event for a groups spinner
        GroupSelected groupSelected = new GroupSelected();
        groupUndergraduates.setOnItemSelectedListener(groupSelected);
        groupMasters.setOnItemSelectedListener(groupSelected);
        groupPhd.setOnItemSelectedListener(groupSelected);

        // Event for the download button
        Button downloadBtn = (Button)findViewById(R.id.donwload_btn);
        OnButtonClickListener clickListener = new OnButtonClickListener(this);
        downloadBtn.setOnClickListener(clickListener);

        // Set the selected studies level
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        studiesLevel = prefs.getString(LEVEL_PREF, LICENCE);
        if (studiesLevel.equals(LICENCE))
            radioGroup.check(R.id.licence_btn);
        if(studiesLevel.equals(MASTERS))
            radioGroup.check(R.id.master_btn);
        if(studiesLevel.equals(PHD))
            radioGroup.check(R.id.phd_btn);
        // Event for radio group (studies level)
        radioGroup.setOnCheckedChangeListener(new StudiesLevelSelected());

        //register event receiver for notification of the end of the syncing operation (only if it was successful)
        registerSyncObserver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_btn:
                Log.d("Download", "sync pressed");
                Intent serviceI = new Intent(this, Synchronizer.class);
                startService(serviceI);
                return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        dbAdapter.close();
        super.onStop();
    }

    // Show dialog when downloading starts
    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.
                setCancelable(true).
                setTitle("Descarcare ...")
                .setView(View.inflate(this, R.layout.loading, null));
        dialog = builder.create();
        dialog.show();
    }

    // Remove the dialog when downloading operation finishes
    public void cancelDialog(){
        dialog.cancel();
        this.finish();
    }

    // Used in case the "faculties" change; more often used within syncing operation
    public void resetFacultiesSpinnerData(){
        ArrayAdapter facultiesAdapter = (ArrayAdapter) faculties.getAdapter();
        ArrayList<Elem> faculties = faculties(dbAdapter);
        facultiesAdapter.clear();
        addAll(facultiesAdapter, faculties);
        facultiesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        // If the user presses back, no download was requested
        this.setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        unregisterSyncObserver();
        super.onDestroy();
    }

    // get faculties from the database
    public ArrayList<Elem> faculties(DBAdapter dbAdapter){
        ArrayList<Elem> faculties = new ArrayList<Elem>();
        try {
            Cursor c = dbAdapter.getFaculties();
            c.moveToFirst();
            Elem faculty;

            while (!c.isAfterLast()) {
                faculty = new Elem();
                faculty.id = c.getInt(ID);
                faculty.name = c.getString(FACULTY_NAME);
                if (isFacultyModular(faculty))
                    faculty.link = c.getString(LINK);
                faculties.add(faculty);
                c.moveToNext();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return faculties;
    }

    public boolean isFacultyModular(Elem faculty)
    {
        return faculty.id == 0;
    }

    // Get the groups for a specific faculty (undergraduates, masters, phd)
    public ArrayList<Elem> groups(DBAdapter dbAdapter, int facultyID, int type){
        ArrayList<Elem> groups = new ArrayList<Elem>();
        try {
            Cursor c = dbAdapter.getGroupsFromFaculty(facultyID, type);
            c.moveToFirst();
            Elem group;
            while (!c.isAfterLast()) {
                group = new Elem();
                group.id = c.getInt(ID);
                group.name = c.getString(GEOUP_NAME);
                groups.add(group);
                c.moveToNext();
            }
        }catch (SQLiteException e){

        }
        return groups;
    }

    // Local broadcast receiver. Fired when the syncing operation is successful
    private void registerSyncObserver(){
        syncFinishedReceiver = new SyncFinishedReceiver(this, groupUndergraduates, groupMasters, groupPhd);
        IntentFilter filter = new IntentFilter(Synchronizer.ACTION_SYNC_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncFinishedReceiver, filter);
    }

    private void unregisterSyncObserver(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncFinishedReceiver);
    }

    // Make sure that only one spinner is visible at given time
    private void resetGroupsSpinners(){
        if (groupUndergraduates == null)
            groupUndergraduates = (Spinner)findViewById(R.id.spinner_group_undergraduate);
        groupUndergraduates.setVisibility(View.INVISIBLE);
        if (groupMasters == null)
            groupMasters = (Spinner)findViewById(R.id.spinner_group_master);
        groupMasters.setVisibility(View.INVISIBLE);
        if (groupPhd == null)
            groupPhd = (Spinner)findViewById(R.id.spinner_group_phd);
        groupPhd.setVisibility(View.INVISIBLE);

        if (prefs == null)
            prefs = getPreferences(MODE_PRIVATE);
        String studies = prefs.getString(LEVEL_PREF, LICENCE);
        if (studies.equals(LICENCE))
            groupUndergraduates.setVisibility(View.VISIBLE);
        else
        if (studies.equals(MASTERS))
            groupMasters.setVisibility(View.VISIBLE);
        else
        if(studies.equals(PHD))
            groupPhd.setVisibility(View.VISIBLE);
    }

    // Compatibility for API < 11
    private void addAll(ArrayAdapter adapter, ArrayList<Elem> list){
        for(Elem e : list)
            adapter.add(e);
    }

    private class FacultySelected implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Elem faculty = (Elem) faculties.getAdapter().getItem(position);

            if (prefs == null)
                prefs = getPreferences(MODE_PRIVATE);
            // Save the selected faculty
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FACULTY_ID_PREF, faculty.id);
            editor.putString(FACULTY_NAME_PREF, faculty.name);
            editor.commit();

            // Adapter and an array list for the spinners and their values
            ArrayAdapter adapter;
            ArrayList<Elem> groupsList;

            // Non modular faculty. Has groups
            // Refresh the data of the three types of spinners for the groups
            adapter = (ArrayAdapter) groupUndergraduates.getAdapter();
            groupsList = groups(dbAdapter, faculty.id, DBAdapter.UNDERGRADUATES);
            adapter.clear();
            addAll(adapter, groupsList);
            adapter.notifyDataSetChanged();

            adapter = (ArrayAdapter) groupMasters.getAdapter();
            groupsList = groups(dbAdapter, faculty.id, DBAdapter.MASTERS);
            adapter.clear();
            addAll(adapter, groupsList);
            adapter.notifyDataSetChanged();

            adapter = (ArrayAdapter) groupPhd.getAdapter();
            groupsList = groups(dbAdapter, faculty.id, DBAdapter.PHD);
            adapter.clear();
            addAll(adapter, groupsList);
            adapter.notifyDataSetChanged();

            resetGroupsSpinners();                                          // Only one group spinner must be visible
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    // Radio button selected for studies level
    private class StudiesLevelSelected implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            SharedPreferences.Editor editor = prefs.edit();
            // Save the selected option
            switch (checkedId) {
                case R.id.licence_btn:
                    //Set adapter to group with licence values
                    Log.d("Downloader", "licence");
                    editor.putString(LEVEL_PREF, LICENCE);
                    break;
                case R.id.master_btn:
                    //set adapter to group with master values
                    Log.d("Downloader", "master");
                    editor.putString(LEVEL_PREF, MASTERS);
                    break;
                case R.id.phd_btn:
                    editor.putString(LEVEL_PREF, PHD);
                    break;

                default:
            }
            editor.commit();
            resetGroupsSpinners();
        }
    }

    // ItemSelected listener for groups spinner
    private class GroupSelected implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Elem group = (Elem) parent.getAdapter().getItem(position);
            Log.d("Settings", "saved " + group.name);
            switch (parent.getId()) {
                case R.id.spinner_group_undergraduate:
                    if (studiesLevel.equals(LICENCE))
                        saveSelectedGroup(group);
                    break;
                case R.id.spinner_group_master:
                    if (studiesLevel.equals(MASTERS))
                        saveSelectedGroup(group);
                    break;
                case R.id.spinner_group_phd:
                    if (studiesLevel.equals(PHD))
                        saveSelectedGroup(group);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        private void saveSelectedGroup(Elem group){
            if (prefs == null)
                prefs = getPreferences(MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(GROUP_ID_PREF, group.id);
            editor.putString(GROUP_NAME_PREF, group.name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
                editor.apply();
            else
                editor.commit();
        }

    }

    // Click listener for download button
    private class OnButtonClickListener implements View.OnClickListener{
        private SettingsActivity activity;
        public OnButtonClickListener(SettingsActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View view) {
            String url;
            if(prefs == null)
                prefs = getPreferences(MODE_PRIVATE);
            facultyID = prefs.getInt(FACULTY_ID_PREF, 1);
            long groupID = prefs.getLong(GROUP_ID_PREF, 0);
            switch (view.getId()) {
                case R.id.donwload_btn:
                    if (facultyID != 0) {
                        url = PARTIAL_TIMETABLE_URL + groupID;
                        new TimetableDownloader(activity).execute(url);
                    }
                break;
            }
        }
    }

    // Faculties and groups
    private class Elem{
        public int id;
        public String name;
        public String link;
        Elem () {

        }
        Elem(int id){
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return this.id == ((Elem)o).id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private class SQLiteDataLoader extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }

}

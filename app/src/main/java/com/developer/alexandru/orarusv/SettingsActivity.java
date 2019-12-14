package com.developer.alexandru.orarusv;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.developer.alexandru.orarusv.data.CsvAPI;
import com.developer.alexandru.orarusv.data.DBAdapter;
import com.developer.alexandru.orarusv.data.DownloadFinished;
import com.developer.alexandru.orarusv.data.SQLStmtHelper;
import com.developer.alexandru.orarusv.data.Synchronizer;
import com.developer.alexandru.orarusv.data.TimetableDownloaderService;
import com.developer.alexandru.orarusv.data.TimetableDownloaderTask;

import java.util.ArrayList;

/**
 * Created by Alexandru on 7/30/14.
 * Download page.Gets strings to form url then starts AsyncTask.
 * Preferences associated with this activity contain data about the user (settings activity)
 */
@Deprecated
public class SettingsActivity extends AppCompatActivity {

    // Dialog displayed while downloading
    //AlertDialog dialog;
    private ProgressDialog progressDialog;
    // Three spinners: undergraduates, masters, phd
    private Spinner groupUndergraduates, groupMasters, groupPhd;

    // Main spinner for choosing faculty. Hint: always visible
    private Spinner faculties;
    private ArrayAdapter<Elem> facultiesAdapter;
    private ArrayAdapter<Elem> groupUndergrAdapter;
    private ArrayAdapter<Elem> groupMastersAdapter;
    private ArrayAdapter<Elem> groupPhdAdapter;

    private SharedPreferences prefs;
    private String studiesLevel;
    private int facultyID;
    private int groupID;

    // Local receiver for sync finished event
    private DownloadFinished syncFinishedReceiver;

    private TimetableDownloaderTask downloaderTask;

    // Preferences
    public static final String FACULTY_NAME_PREF = "faculty";
    public static final String FACULTY_ID_PREF = "faculty_id";
    public static final String GROUP_NAME_PREF = "group_name";
    public static final String GROUP_ID_PREF = "group_id";
    public static final String LEVEL_PREF = "studies_level";
    public static final String LICENCE = "licence";
    public static final String MASTERS = "masters";
    public static final String PHD = "phd";

    private DBAdapter dbAdapter;

    //Columns for faculties and groups database query
    public static int FACULTY_NAME = 2;
    public static int LINK = 2;
    public static int ID = 0;

    public static int GROUP_ID = 1;
    public static int GROUP_NAME = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getPreferences(MODE_PRIVATE);

        setContentView(R.layout.activity_settings);
        Toolbar settingsToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // The helper for SQLite database operations
        dbAdapter = new DBAdapter(this);
        try {
            dbAdapter.open();
        }catch (Exception e){
            Log.e("SettingsActivity", "database locked");
        }
        // Spinners used by the user
        faculties = (Spinner)findViewById(R.id.spinner_faculty);
        groupUndergraduates = (Spinner)findViewById(R.id.spinner_group_undergraduate);
        groupMasters = (Spinner)findViewById(R.id.spinner_group_master);
        groupPhd = (Spinner)findViewById(R.id.spinner_group_phd);

        // Retrieve the data and create the adapters for the four spinners
        // Faculties spinner
        ArrayList<Elem> facultiesNames = faculties(dbAdapter);
        facultiesAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, facultiesNames);
        faculties.setAdapter(facultiesAdapter);
        // Reset the last selected faculty
        facultyID = prefs.getInt(FACULTY_ID_PREF, 1);
        faculties.setSelection(facultiesNames.indexOf(new Elem(facultyID)));


        ArrayList<Elem> groupsUndergraduates = new ArrayList<Elem>();
        ArrayList<Elem> groupsMasters = new ArrayList<Elem>();
        ArrayList<Elem> groupsPhd = new ArrayList<Elem>();

        groupUndergrAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, groupsUndergraduates);
        groupMastersAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, groupsMasters);
        groupPhdAdapter = new ArrayAdapter<Elem>(this, R.layout.spinner_item_layout, groupsPhd);

        groupUndergrAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        groupMastersAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        groupPhdAdapter.setDropDownViewResource(R.layout.spinner_item_layout);

        groupUndergraduates.setAdapter(groupUndergrAdapter);
        groupMasters.setAdapter(groupMastersAdapter);
        groupPhd.setAdapter(groupPhdAdapter);

        // Set the selected studies level and the selected group
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        studiesLevel = prefs.getString(LEVEL_PREF, LICENCE);
        groupID = prefs.getInt(SettingsActivity.GROUP_ID_PREF, -1);
        if (studiesLevel.equals(LICENCE)) {
            radioGroup.check(R.id.licence_btn);
        }
        if(studiesLevel.equals(MASTERS)) {
            radioGroup.check(R.id.master_btn);
        }
        if(studiesLevel.equals(PHD)) {
            radioGroup.check(R.id.phd_btn);
        }
        // Event for a groups spinner
        SettingsGroupSelected groupSelected = new SettingsGroupSelected(this.prefs);
        groupUndergraduates.setOnItemSelectedListener(groupSelected);
        groupMasters.setOnItemSelectedListener(groupSelected);
        groupPhd.setOnItemSelectedListener(groupSelected);

        // Event for selecting a faculty
        faculties.setOnItemSelectedListener(new FacultySelected());

        // Event for the com.developer.alexandru.orarusv.download button
        Button downloadBtn = (Button)findViewById(R.id.download_btn);
        OnButtonClickListener clickListener = new OnButtonClickListener();
        downloadBtn.setOnClickListener(clickListener);

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

                if (!Utils.hasInternetAccess(SettingsActivity.this)) {
                    Utils.toastNoInternetAccess(SettingsActivity.this);
                    return true;
                }

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Descarcare ...");
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d("SettingsActivity", "canceled");
                if (downloaderTask != null && !downloaderTask.isCancelled())
                    downloaderTask.cancel(true);
            }
        });
        progressDialog.show();
    }

    // Remove the dialog when downloading operation finishes
    public void cancelDialog(){
        //dialog.cancel();
        progressDialog.cancel();
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
    protected void onDestroy() {
        dbAdapter.close();
        unregisterSyncObserver();
        super.onDestroy();
    }

    // get faculties from the database
    public ArrayList<Elem> faculties(DBAdapter dbAdapter){
        ArrayList<Elem> faculties = new ArrayList<>();
        return faculties;
    }

    public boolean isFacultyModular(Elem faculty)
    {
        return faculty.id == 0;
    }

    public void groups(ArrayAdapter<Elem> arrayAdapter, int facultyID, int type){
        new SQLiteDataLoader(arrayAdapter, facultyID, type).execute(GROUP_ID, GROUP_NAME);
    }

    // Local broadcast receiver. Fired when the syncing operation is successful
    private void registerSyncObserver(){
        syncFinishedReceiver = new DownloadFinished(this, groupUndergraduates, groupMasters, groupPhd);
        IntentFilter filter = new IntentFilter(Synchronizer.ACTION_SYNC_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncFinishedReceiver, filter);
    }

    private void unregisterSyncObserver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncFinishedReceiver);
    }

    // Make sure that only one spinner is visible at given time
    private void resetGroupsSpinners() {
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
        if (studies.equals(LICENCE)) {
            groupUndergraduates.setVisibility(View.VISIBLE);
            saveSelectedGroup((Elem) groupUndergraduates.getSelectedItem());
        }
        else
            if (studies.equals(MASTERS)) {
                groupMasters.setVisibility(View.VISIBLE);
                saveSelectedGroup((Elem) groupMasters.getSelectedItem());
            }
        else
            if(studies.equals(PHD)) {
                groupPhd.setVisibility(View.VISIBLE);
                saveSelectedGroup((Elem)groupPhd.getSelectedItem());
            }
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
            // If there is a previous selected group select it accordingly
            adapter = (ArrayAdapter) groupUndergraduates.getAdapter();
            /*groupsList = groups(dbAdapter, faculty.id, SQLStmtHelper.UNDERGRADUATES);
            adapter.clear();
            addAll(adapter, groupsList);
            adapter.notifyDataSetChanged();
            if (studiesLevel.equals(LICENCE))
                groupUndergraduates.setSelection(groupsList.indexOf(new Elem(groupID)));
            */
            groups(adapter, faculty.id, SQLStmtHelper.UNDERGRADUATES);
            adapter = (ArrayAdapter) groupMasters.getAdapter();
            /*groupsList = groups(dbAdapter, faculty.id, SQLStmtHelper.MASTERS);
            adapter.clear();
            addAll(adapter, groupsList);
            adapter.notifyDataSetChanged();
            if (studiesLevel.equals(MASTERS))
                groupMasters.setSelection(groupsList.indexOf(new Elem(groupID)));
            */
            groups(adapter, faculty.id, SQLStmtHelper.MASTERS);

            adapter = (ArrayAdapter) groupPhd.getAdapter();
            /*groupsList = groups(dbAdapter, faculty.id, SQLStmtHelper.PHD);
            adapter.clear();
            addAll(adapter, groupsList);
            adapter.notifyDataSetChanged();
            if(studiesLevel.equalsIgnoreCase(PHD))
                groupPhd.setSelection(groupsList.indexOf(new Elem(groupID)));
            */
            groups(adapter, faculty.id, SQLStmtHelper.PHD);
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
                    editor.putString(LEVEL_PREF, LICENCE);
                    break;
                case R.id.master_btn:
                    //set adapter to group with master values
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

    // Save selected group to preferences
    public void saveSelectedGroup(Elem group){
        if (prefs == null)
            prefs = getPreferences(MODE_PRIVATE);
        if (group == null)
            return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GROUP_ID_PREF, group.id);
        editor.putString(GROUP_NAME_PREF, group.name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            editor.apply();
        else
            editor.commit();
    }

    // Click listener for com.developer.alexandru.orarusv.download button
    private class OnButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            String url;
            if(prefs == null)
                prefs = getPreferences(MODE_PRIVATE);
            facultyID = prefs.getInt(FACULTY_ID_PREF, 1);
            int groupID = prefs.getInt(GROUP_ID_PREF, 0);
            switch (view.getId()) {
                case R.id.download_btn:

                    if (!Utils.hasInternetAccess(SettingsActivity.this)) {
                        Utils.toastNoInternetAccess(SettingsActivity.this);
                        break;
                    }
                    if (facultyID != 0) {                           // Check if non-modular timetable is requested
                        url = CsvAPI.PARTIAL_GROUP_TIMETABLE_URL + groupID;
//                        downloaderTask = new TimetableDownloaderTask(SettingsActivity.this);
//                        downloaderTask.execute(url);
                        Intent intent = new Intent(SettingsActivity.this, TimetableDownloaderService.class);
                        intent.putExtra(TimetableDownloaderService.EXTRA_URL, url);
                        startService(intent);
                    }
                break;
            }
        }
    }

    private class SQLiteDataLoader extends AsyncTask<Integer, Void, Void>{

        private ArrayAdapter<Elem> adapter;
        private int facultyId;
        private int type;

        private int positionToChoose = 0;

        ArrayList<Elem> elems = new ArrayList<>();

        public SQLiteDataLoader(ArrayAdapter<Elem> adapter, int facultyId, int type) {
            this.adapter = adapter;
            this.facultyId = facultyId;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter.clear();
        }

        @Override
        protected Void doInBackground(Integer... params) {

            try {
                if (!dbAdapter.isOpen())
                    dbAdapter.open();
                dbAdapter.close();
            }catch (SQLiteException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            addAll(adapter, elems);
            adapter.notifyDataSetChanged();
            switch (type){
                case SQLStmtHelper.UNDERGRADUATES:
                    if (studiesLevel.equals(LICENCE))
                        groupUndergraduates.setSelection(positionToChoose);
                    break;
                case SQLStmtHelper.MASTERS:
                    if (studiesLevel.equals(MASTERS))
                        groupMasters.setSelection(positionToChoose);
                    break;
                case SQLStmtHelper.PHD:
                    if(studiesLevel.equalsIgnoreCase(PHD))
                        groupPhd.setSelection(positionToChoose);
                    break;
            }

        }
    }

}

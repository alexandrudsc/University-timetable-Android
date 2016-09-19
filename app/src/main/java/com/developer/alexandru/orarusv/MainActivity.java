package com.developer.alexandru.orarusv;


import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;

import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.DBAdapter;
import com.developer.alexandru.orarusv.navigation_drawer.DrawerToggle;
import com.developer.alexandru.orarusv.navigation_drawer.NavDrawerAdapter;
import com.developer.alexandru.orarusv.navigation_drawer.NavigationItemClickListener;


/**
 * Created by Alexandru
 * The activity_main entry point of the app.
 * Activity responsible for choosing the right design (phones and tablets, one or two panes layout).
 * Implements an interface for easy interaction with the timetable fragment.
 */
public class MainActivity extends ActionBarActivity
        implements TimetableFragment.OnCourseSelected{

    //debug
    private static final boolean D = true;
    private static final String TAG = "MainActivity";

    public static final char REQUEST_CODE_DOWNLOAD = 12;
    public static final char REQUEST_CODE_UNKNOW = 13;  // TODO must be changed

    public static final String TIMETABLE_FRAGMENT_TAG = "this_is_timetable_fragment";

    //General preferences file name
    public static final String PREFERENCES_FILE_NAME = "preferences";

    //File with data relating time (start, end, holidays)
    public static final String TIME_ORGANISER_FILE_NAME = "time_organiser";

    public static final String FIRST_RUN = "first_run";

    //The current week of semester preference name
    public static final String WEEK_OF_SEMESTER = "week_of_semester";

    //The start date preference name
    public static final String START_DATE = "start_date";

    //The end date preference name
    public static final String END_DATE = "stop_date";

    // The preference to keep the time when the activity_main activity was last created
    public static final String LAST_CREATE_TIME = "creation_time";

    //Partial name of a holiday item in prefences file
    public static final String HOLIDAY = "holiday";

    //Number of holidays preference name
    public static final String  NUMBER_OF_HOLIDAYS = "no_of_holiday";

    //Last selected position in the view pager (ast day selected)
    public static final String VP_LAST_POSITION = "vp_last_position";

    //The last week selected from navigation list in action bar
    public static final String PREF_LAST_SELECTED_WEEK = "vp_last_position";

    public static long WEEK_IN_MILLIS = 7 * 24 * 3600 * 1000;
    public static int WEEKS_IN_SEMESTER = 14;

    //Main fragment
    private TimetableFragment timetableFragment = null;

    private  DrawerToggle drawerToggle;
    private  DrawerLayout drawerLayout;
    private NavDrawerAdapter drawerAdapter;

    private android.support.v7.app.ActionBar actionBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "create activity_main");

        Utils.setCurrentWeek(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);

        drawerToggle = new DrawerToggle(this, drawerLayout,
                toolbar,
                R.string.title_nav_opened,
                R.string.title_nav_closed);

        if(savedInstanceState != null){
            Log.d("MAIN ACTIVITY", "create activity_main with saved instance state");
            return; //Must not overlap old fragment
        }

        final FragmentManager fm = getSupportFragmentManager();
        ViewGroup fragmentContainer = (ViewGroup) findViewById(R.id.fragment_container);

       if(fragmentContainer != null){
            //One-pane layout
            timetableFragment = (TimetableFragment) fm.findFragmentByTag(TIMETABLE_FRAGMENT_TAG);
            if(timetableFragment == null)
                timetableFragment = new TimetableFragment();
            fm.beginTransaction().replace(R.id.fragment_container, timetableFragment, TIMETABLE_FRAGMENT_TAG).commit();
        } else {
            //Two pane layout

        }

        Intent intent = getIntent();
        if (intent == null || intent.getAction() == null)
            return;                                                 // App started normal

        // Check if activity was started from app widget. Must show the course
        if (getIntent().getAction().equals(SearchableFragment.actionViewDetails)) {
            final Bundle bundle = getIntent().getExtras();
            Course c;
            if (bundle != null){
                c = bundle.getParcelable(SearchableFragment.EXTRA_COURSE_KEY);
                onCourseClicked(c);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager)this.getSystemService(SEARCH_SERVICE);

        SearchView  searchView = (SearchView) MenuItemCompat.getActionView((menu.findItem(R.id.search_from_menu)));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /*switch (item.getItemId()){
            case R.id.download_from_menu:
                Intent intent = new Intent(this, SettingsActivity.class);

                // Register a receiver for the notification of the downloading status
                /*if (timetableFragment == null)
                        timetableFragment = (TimetableFragment)getSupportFragmentManager().findFragmentByTag(TIMETABLE_FRAGMENT_TAG);
                downloadFinishedReceiver = new DownloadFinishedReceiver(this, timetableFragment.getChildFragmentManager());
                IntentFilter downloadFinishedFilter = new IntentFilter(TimetableDownloaderService.ACTION_DOWNLOAD_FINISHED);
                LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver, downloadFinishedFilter);

                startActivityForResult(intent, REQUEST_CODE_DOWNLOAD);

                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeNavDrawer();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        ((TextView)findViewById(R.id.toolbar_title)).setText(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("nav_drawer_last_pos", drawerToggle.getCurrentPage());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        drawerToggle.setCurrentPage(savedInstanceState.getInt("nav_drawer_last_pos"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Result returned by the downloader activity to this

        if(requestCode == REQUEST_CODE_DOWNLOAD){
            if(resultCode == RESULT_OK) {
               /* Log.d("Main", "downloaded. " + "Refresh data.");
                //Check if two pane layout
                if(timetableFragment == null)
                    timetableFragment = (TimetableFragment)getSupportFragmentManager().findFragmentById(R.id.timetable_fragment);

                if(timetableFragment == null)
                    timetableFragment = (TimetableFragment)getSupportFragmentManager().findFragmentByTag(TIMETABLE_FRAGMENT_TAG);
                try {
                    new DataLoader(this, timetableFragment.getChildFragmentManager(),
                            2, null, null, null).execute();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }*/
            }
            else
                if(resultCode == RESULT_CANCELED) {
                    Log.d("Main", "NOT downloaded");
                    //Toast.makeText(this, "Eroare la descarcare ..,", Toast.LENGTH_SHORT).show();
                    // If the user hasn't downloaded anything, unregister the receiver
                    // (the result will be RESULT_CANCELED if the user hasn't pressed the download button)

                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "new intent" + intent.getAction());

        final Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;
        final Course c;
        String name = null, type = null, info = null;
        String[] typeAndName = null;
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SEARCH)) {
            // Activity called when search was requested
            typeAndName = intent.getStringExtra(SearchManager.QUERY).split(" ");
            try {
                type = typeAndName[1];
                name = typeAndName[0];
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        } else if (action.equals(Intent.ACTION_VIEW)) {
            info = bundle.getString(SearchManager.EXTRA_DATA_KEY);
            typeAndName = intent.getData().toString().split("/");
            type = typeAndName[0];
            name = typeAndName[1];
        } else if (action.equals(SearchableFragment.actionViewDetails)) {
            // Activity started from app widget
            c = bundle.getParcelable(SearchableFragment.EXTRA_COURSE_KEY);
            onCourseClicked(c);
            return;
        } else
            return;

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        c = dbAdapter.getCourse(name, type);
        dbAdapter.close();
        //Searching has the same result as clicking on that course from anywhere;
        onCourseClicked(c);
    }

    @Override
    public void onBackPressed() {
        // If the user presses back and the nav drawer is open, just close it
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(VP_LAST_POSITION);
        editor.commit();
        super.onDestroy();
        if (D) Log.d(TAG, "destroyed");
    }

    public void initializeNavDrawer(){
        if(drawerLayout == null)
            drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);

        final ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        if (drawerAdapter == null)
            drawerAdapter = new NavDrawerAdapter(this);

        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(new NavigationItemClickListener(drawerLayout, drawerToggle));

        if (actionBar == null)
            actionBar =  getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public int retrieveLastPosition(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(VP_LAST_POSITION, -1);
    }

    @Override
    public MainActivity getActivity(){
        return this;
    }

    @Override
    public FragmentManager getFragManager() {
        return getSupportFragmentManager();
    }

    @Override
    public boolean onCourseClicked(Course c) {
        FragmentManager fm = getSupportFragmentManager();
        SearchableFragment searchableFragment;

        searchableFragment = (SearchableFragment) fm.findFragmentById(R.id.searchable_fragment);

        if(searchableFragment != null){
            //Two-pane layout
            if(searchableFragment.getView() == null){
                searchableFragment = new SearchableFragment(c, fm);
                fm.beginTransaction().replace(R.id.searchable_container, searchableFragment).commit();
            }else
                searchableFragment.updateContent(c, fm);
        }else{
            //One pane layout
            searchableFragment = new SearchableFragment(c, fm);
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            ft.replace(R.id.fragment_container, searchableFragment, "replacement_searchable");
            ft.addToBackStack("replacement_searchable");
            ft.commit();
        }

        return true;
    }


}
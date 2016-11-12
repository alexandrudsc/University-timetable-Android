package com.developer.alexandru.orarusv.main;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.Course;
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
        implements MainActivityView, TimetableFragment.OnCourseSelected {

    //debug
    private static final boolean D = true;
    private static final String TAG = "MainActivity";

    public static final char REQUEST_CODE_DOWNLOAD = 12;
    public static final char REQUEST_CODE_UNKNOW = 13;  // TODO must be changed

    //General preferences file name
    public static final String PREFERENCES_FILE_NAME = "preferences";

    //File with data relating time (start, end, holidays)
    public static final String TIME_ORGANISER_FILE_NAME = "time_organiser";

    //File with data relating exams
    public static final String EXAMS_FILE_NAME = "exams";

    public static final String PREF_APP_FIRST_RUN = "first_run";

    //The current week of semester preference name
    public static final String WEEK_OF_SEMESTER = "week_of_semester";

    //The start date preference name
    public static final String START_DATE = "start_date";

    //The end date preference name
    public static final String END_DATE = "stop_date";

    // The preference to keep the time when the activity_main activity was last created
    public static final String LAST_CREATE_TIME = "creation_time";

    //Partial name of a holiday item in prefences file
    public static final String HOLIDAY = "Vacanța";

    //Number of holidays preference name
    public static final String NUMBER_OF_HOLIDAYS = "no_of_holiday";

    //Last selected position in the view pager (ast day selected)
    public static final String VP_LAST_POSITION = "vp_last_position";

    //The last week selected from navigation list in action bar
    public static final String PREF_LAST_SELECTED_WEEK = "vp_last_position";

    public static final long WEEK_IN_MILLIS = 7 * 24 * 3600 * 1000;
    public static final int WEEKS_IN_SEMESTER = 14;

    private DrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavDrawerAdapter drawerAdapter;

    private android.support.v7.app.ActionBar actionBar;

    private MainActivityPresenterImpl presenter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainActivityPresenterImpl(this);
        Log.d(TAG, "create activity_main");

        Utils.setCurrentWeek(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);

        drawerToggle = new DrawerToggle(this, drawerLayout,
                toolbar,
                R.string.title_nav_opened,
                R.string.title_nav_closed);

        if (savedInstanceState != null) {
            Log.d("MAIN ACTIVITY", "create activity_main with saved instance state");
            return; //Must not overlap old fragment
        }

        presenter.initialize();
        presenter.checkForNewTimeStructure();
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
        SearchManager searchManager = (SearchManager) this.getSystemService(SEARCH_SERVICE);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView((menu.findItem(R.id.search_from_menu)));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeNavDrawer();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        ((TextView) findViewById(R.id.toolbar_title)).setText(title);
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

        if (requestCode == REQUEST_CODE_DOWNLOAD) {
            if (resultCode == RESULT_OK) {
                Log.d("Main", "downloaded. " + "Refresh data.");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("Main", "NOT downloaded");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "new intent" + intent.getAction());
        presenter.onNewIntent(intent);
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

    public void initializeNavDrawer() {
        if (drawerLayout == null)
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);

        final ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        if (drawerAdapter == null)
            drawerAdapter = new NavDrawerAdapter(this);

        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(new NavigationItemClickListener(drawerLayout, drawerToggle));

        if (actionBar == null)
            actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public int retrieveLastPosition() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(VP_LAST_POSITION, -1);
    }

    @Override
    public MainActivity getActivity() {
        return this;
    }

    @Override
    public FragmentManager getFragManager() {
        return getSupportFragmentManager();
    }

    @Override
    public boolean onCourseClicked(Course c) {
        return presenter.onCourseClicked(c);
    }

    @Override
    public Context getContext() {
        return this;
    }

}
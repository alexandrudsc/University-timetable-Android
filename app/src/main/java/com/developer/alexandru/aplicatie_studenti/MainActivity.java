package com.developer.alexandru.aplicatie_studenti;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.Toast;
import com.developer.alexandru.aplicatie_studenti.data.DBAdapter;
import com.developer.alexandru.aplicatie_studenti.data.DataLoader;
import com.developer.alexandru.aplicatie_studenti.navigation_drawer.DrawerToggle;
import com.developer.alexandru.aplicatie_studenti.navigation_drawer.NavDrawerAdapter;
import com.developer.alexandru.aplicatie_studenti.navigation_drawer.NavigationItemClickListener;
import com.developer.alexandru.aplicatie_studenti.data.Course;
import com.developer.alexandru.aplicatie_studenti.view_pager.DayFragment;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.net.URL;


public class MainActivity extends ActionBarActivity
        implements TimetableFragment.OnCourseSelected{

    //debug
    private static final boolean D = true;
    private static final String TAG = "MainActivity";

    public static final char REQUEST_CODE_DOWNLOAD = 12;

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
    public static int IS_HOLIDAY = -1;

    //Main fragment
    private TimetableFragment timetableFragment = null;

    private  DrawerToggle drawerToggle;
    private  DrawerLayout drawerLayout;

    //The current item selected in nav drawer
    private int currentPageNavDrawer = -1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentWeek(this);

        setContentView(R.layout.main);

        drawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);

        drawerToggle = new DrawerToggle(this, drawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.title_nav_opened,
                R.string.title_nav_closed);

        Log.d("MAIN ACTIVITY", "create main");

        if(savedInstanceState != null){
            Log.d("MAIN ACTIVITY", "create main with saved instance state");
            //Remember the fragments inside main activity that must refer to this new activity
            DayFragment.onCourseSelected = this;

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

        switch (item.getItemId()){
            case R.id.download_from_menu:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_DOWNLOAD);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeNavDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Result returned by the downloader activity to this

        if(requestCode == REQUEST_CODE_DOWNLOAD){
            if(resultCode == RESULT_OK) {
                Log.d("Main", "downloaded. " + "Refresh data.");
                //Check if two pane layout
                if(timetableFragment == null)
                    timetableFragment = (TimetableFragment)getSupportFragmentManager().findFragmentById(R.id.timetable_fragment);

                if(timetableFragment == null)
                    timetableFragment = (TimetableFragment)getSupportFragmentManager().findFragmentByTag(TIMETABLE_FRAGMENT_TAG);
                try {
                    new DataLoader(this, timetableFragment.getChildFragmentManager(),
                            2, null, null, null).execute((URL) null);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            else
                if(resultCode == RESULT_CANCELED) {
                    Log.d("Main", "NOT downloaded");
                    Toast.makeText(this, "Eroare la descarcare ..,", Toast.LENGTH_SHORT).show();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Bundle bundle = intent.getExtras();
        final Course c;
        String name = null, type = null, info = null;
        String[] typeAndName = null;
        if(intent.getAction().equals(Intent.ACTION_SEARCH)){
            typeAndName  = intent.getStringExtra(SearchManager.QUERY).split(" ");
            try {
                type = typeAndName[1];
                name = typeAndName[0];
            }catch (ArrayIndexOutOfBoundsException e){
            }
        }
        else
            if(intent.getAction().equals(Intent.ACTION_VIEW) && bundle != null){
                info = bundle.getString(SearchManager.EXTRA_DATA_KEY);
                typeAndName = intent.getData().toString().split("/");
                type = typeAndName[0];
                name = typeAndName[1];
            }else
                return;

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        c = dbAdapter.getCourse(name, type);
        dbAdapter.close();
        //Searching has the same result as clicking on that course from anywhere;
        onCourseClicked(c);
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

    /**
     * Calculate the current week of semester and save it.<br>
     * IS CALLED FROM MULTIPLE CONTEXTS: MAIN ACTIVITY, SERVICE FOR APPLICATION WIDGET.
     * @param context context from which is called
     */
    public static void setCurrentWeek(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TIME_ORGANISER_FILE_NAME, MODE_PRIVATE);

        final long startDateSemester = sharedPreferences.getLong(START_DATE, 0);
        final long endDateSemester = sharedPreferences.getLong(END_DATE, 0);

        final String[] holidayStartEnd = sharedPreferences.getString(HOLIDAY + "_" + 0, "0-0").split("-");
        final long startDateHoliday = Long.valueOf(holidayStartEnd[0]);
        final long endDateHoliday = Long.valueOf(holidayStartEnd[1]);

        final long currentTimeInMillis = System.currentTimeMillis();
        int currentWeek ;
        if (D) Log.d(TAG, "now " + currentTimeInMillis + " end " + endDateSemester);
        if(currentTimeInMillis > endDateSemester)
            currentWeek = WEEKS_IN_SEMESTER;
        else
            if(currentTimeInMillis < startDateSemester)
                currentWeek = 1;
            else{
                if(isHoliday(startDateHoliday, endDateHoliday)){
                    if (D) Log.d(TAG, "is holiday");
                    // Show the first week after the holiday
                    currentWeek = (int)((startDateHoliday - startDateSemester) / WEEK_IN_MILLIS) + 1;
                }
                else {
                    final long vacationTime = calculateVacationTime(startDateHoliday, endDateHoliday);
                    if (currentTimeInMillis >= endDateHoliday)
                        currentWeek = (int) ((currentTimeInMillis - startDateSemester - vacationTime) / WEEK_IN_MILLIS) + 1;
                    else
                        currentWeek = (int) ((currentTimeInMillis - startDateSemester) / WEEK_IN_MILLIS) + 1;
                    if ( currentWeek > WEEKS_IN_SEMESTER)
                        currentWeek = WEEKS_IN_SEMESTER;
                }
            }

        if(sharedPreferences.getInt(WEEK_OF_SEMESTER, -1) !=  currentWeek){
            // If the week is changed invalidate the old data set.
            ViewPagerAdapter.listsOfCourses = null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(WEEK_OF_SEMESTER, currentWeek);
            editor.commit();
        }
        if (D) Log.d(TAG, "current week set");
    }

    private static long calculateVacationTime(long startDate, long endDate){
        long freeTime = 0;
        freeTime += durationOfHoliday(startDate , endDate);
        return freeTime;
    }

    private static long durationOfHoliday(long start, long end){
        long time = end - start;
        return time;
    }

    private static boolean isHoliday(long start, long end){
        return System.currentTimeMillis() >= start && System.currentTimeMillis() <= end;
    }

    public void initializeNavDrawer(){
        if(drawerLayout == null)
            drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(drawerToggle);

        final ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new NavDrawerAdapter(this));
        drawerList.setOnItemClickListener(new NavigationItemClickListener(drawerLayout, drawerToggle));

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public int retrieveLastPosition(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(VP_LAST_POSITION, -1);
    }

    @Override
    public Context getContext() {
        return this;
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
package com.developer.alexandru.orarusv;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.developer.alexandru.orarusv.action_bar.MySpinnerAdapter;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.DataLoader;
import com.developer.alexandru.orarusv.view_pager.PagerSlidingTabStrip;
import com.developer.alexandru.orarusv.view_pager.ViewPagerAdapter;

import java.util.Calendar;

/**
 * Created by Alexandru on 7/12/14.
 * The fragment displayed as activity_main page in the activity_main activity.
 * Contains the view pager with 7 fragments corresponding to a whole week
 */
public class TimetableFragment extends Fragment {
    //debug
    private static final boolean D = true;
    private static final String TAG = "TimetableFragment";

    public ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    public PagerSlidingTabStrip pagerSlidingTabStrip;

    private OnCourseSelected onCourseSelected;
    //Used to refresh current item
    private int currentItem;

    private Calendar calendar;

    public static final String DETAILS_FRAGMENT_TAG = "this_is_details_frag";

    /**
     * Implemented by the activity hosting this fragment
     * Main functionality is to determine the event that will happen at click on a course
     * Also contains a reference to the activity_main activity
     */
    public static interface OnCourseSelected{
        public MainActivity getActivity();
        public FragmentManager getFragManager();
        public boolean onCourseClicked(Course c);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onCourseSelected = (OnCourseSelected)activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (D) Log.d(TAG, "create frag");
        // In case the fragment is recreated, but the activity was not destroyed (fragment replacement);
        try {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Săptămâna " + Utils.getCurrentWeek(getActivity()));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "create view");
        View fragmentView = inflater.inflate(R.layout.fragment_timetable, container, false);

        if (calendar == null)
            calendar = Calendar.getInstance();
        // Create here the view pager adapter so I can call getActivity() on a DayFragment anytime during it's lifecycle
        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter((MainActivity)getActivity(), getChildFragmentManager()));
        pagerSlidingTabStrip = (PagerSlidingTabStrip) fragmentView.findViewById(R.id.sliding_tabs);
        pagerSlidingTabStrip.setViewPager(viewPager);
        viewPager.setCurrentItem(calendar.get(Calendar.DAY_OF_WEEK) - 1);
        return fragmentView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //onCourseSelected.getActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        initializeActionBar();
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null)
            currentItem = savedInstanceState.getInt("current_item");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Săptămâna " + Utils.getCurrentWeek(getActivity()));
        //Test if the async task hasn't just finished.If true no need to execute it again.
        //But if list view adapters are null, async task was finished long time ago, so there it's
        //isn't anything stored on RAM.The file must be loaded again.

        /*if(ViewPagerAdapter.listsOfCourses == null || ViewPagerAdapter.isAnyListNull()){
            Log.d("MAIN ON_RESUME", "new loader");
            loadData();
        }
        else{
            Log.d("MAIN ON_RESUME", "old loader");
            setDataFromPreviousLoad();
            Utils.setCurrentWeek(onCourseSelected.getActivity());
        }*/

    }

    @Override
    public void onResume() {
        super.onResume();
        if (D) Log.d(TAG, "resumed");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (D) Log.d(TAG, "on create options");
        switch (item.getItemId()){
            case R.id.download_from_menu:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);

                // Register a receiver for the notification of the downloading status
                /*if (timetableFragment == null)
                        timetableFragment = (TimetableFragment)getSupportFragmentManager().findFragmentByTag(TIMETABLE_FRAGMENT_TAG);
                downloadFinishedReceiver = new DownloadFinishedReceiver(this, timetableFragment.getChildFragmentManager());
                IntentFilter downloadFinishedFilter = new IntentFilter(TimetableDownloaderService.ACTION_DOWNLOAD_FINISHED);
                LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver, downloadFinishedFilter);
                */
                this.startActivityForResult(intent, MainActivity.REQUEST_CODE_DOWNLOAD);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult");
        for (Fragment fr: getChildFragmentManager().getFragments())
            fr.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPause() {
        super.onPause();
        final int lastPosition = viewPager.getCurrentItem();
       /* SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.VP_LAST_POSITION, lastPosition);
        editor.commit();*/
    }


    public void initializeActionBar(){
        // OnCourseSelected is an interface implemented by the activity_main activity in order to communicate with
        // the fragments.
        SharedPreferences prefs = onCourseSelected.getActivity().getSharedPreferences(
                            MainActivity.TIME_ORGANISER_FILE_NAME, MainActivity.MODE_PRIVATE);
        prefs.edit().putInt(MainActivity.PREF_LAST_SELECTED_WEEK, (prefs.getInt(
                MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER) - 1)).commit();

        final android.support.v7.app.ActionBar actionBar = onCourseSelected.getActivity().getSupportActionBar();
        final MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(onCourseSelected.getActivity());

        //actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
        //actionBar.setListNavigationCallbacks(spinnerAdapter, new SpinnerElementSelectedEvent(onCourseSelected.getActivity()));
        //actionBar.setSelectedNavigationItem(prefs.getInt(MainActivity.WEEK_OF_SEMESTER,
        //        MainActivity.WEEKS_IN_SEMESTER) - 1);

    }

    private void loadData(){
            new DataLoader(onCourseSelected, getChildFragmentManager(), currentItem, null, null, null).execute();
    }

    private void setDataFromPreviousLoad(){

        if(viewPager.getAdapter() == null){
            Log.d("TIMETABLE FRAG", "reset activity_main adapter");

            viewPagerAdapter = new ViewPagerAdapter(onCourseSelected, getChildFragmentManager());

            viewPager.setAdapter(viewPagerAdapter);

            pagerSlidingTabStrip.setViewPager(viewPager);
        }else
            viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();

        //final int lastVPPosition = onCourseSelected.getActivity().retrieveLastPosition();
        calendar = Calendar.getInstance();
        viewPager.setCurrentItem(calendar.get(Calendar.DAY_OF_WEEK) - 1);
        if (D) Log.d(TAG, "" + calendar.get(Calendar.DAY_OF_WEEK) );
    }

    /**
     * Check if the week has changed and the user hasn't completely quited the app since then. <br>
     * This is used because the data is loaded in onCreate(), but there is a possibility that the user kept the app opened <br>
     * (at least it's still in memory). Because the data is kept, when the user reopens the app it will see the data from the <br>
     * previous week <br>
     * This is only available if the day is saturday (last day of week)<br>
     */
    private void checkWeekChanged(){
        if (calendar == null)
            calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            Utils.setCurrentWeek(this.getActivity());
    }

}

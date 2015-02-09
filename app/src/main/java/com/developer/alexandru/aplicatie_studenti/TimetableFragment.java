package com.developer.alexandru.aplicatie_studenti;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.alexandru.aplicatie_studenti.action_bar.MySpinnerAdapter;
import com.developer.alexandru.aplicatie_studenti.action_bar.SpinnerElementSelectedEvent;
import com.developer.alexandru.aplicatie_studenti.data.DataLoader;
import com.developer.alexandru.aplicatie_studenti.data.Course;
import com.developer.alexandru.aplicatie_studenti.view_pager.PagerSlidingTabStrip;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Alexandru on 7/12/14.
 */
public class TimetableFragment extends Fragment {
    public ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    public PagerSlidingTabStrip pagerSlidingTabStrip;

    private OnCourseSelected onCourseSelected;
    //Used to refresh current item
    private int currentItem;

    public static final String DETAILS_FRAGMENT_TAG = "this_is_details_frag";

    /**
     * Implemented by the activity hosting this fragment
     * Main functionality is to determine the event that will happen at click on a course
     * Also contains a reference to the main activity
     */
    public static interface OnCourseSelected{
        FragmentManager childFragManager = null;
        public Context getContext();
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
        Log.d("Timetable", "create frag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TIMETABLE FRAG", "create view");

        View fragmentView = inflater.inflate(R.layout.fragment_timetable, container, false);
        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) fragmentView.findViewById(R.id.sliding_tabs);
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
        //Test if the async task hasn't just finished.If true no need to execute it again.
        //But if list view adapters are null, async task was finished long time ago, so there it's
        //isn't anything stored on RAM.The file must be loaded again.

        if(ViewPagerAdapter.listsOfCourses == null || ViewPagerAdapter.isAnyListNull()){
            Log.d("MAIN ON_RESUME", "new loader");
            loadJSONFileAndSetData();
        }
        else{
            Log.d("MAIN ON_RESUME", "old loader");
            setDataFromLoadedFile();
            MainActivity.setCurrentWeek(onCourseSelected.getActivity());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TIMETABLE ", "resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        final int lastPosition = viewPager.getCurrentItem();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.VP_LAST_POSITION, lastPosition);
        editor.commit();
    }


    public void initializeActionBar(){
        // OnCourseSelected is an interface implemented by the main activity in order to comunicate with
        // the fragments.
        SharedPreferences prefs = onCourseSelected.getContext().getSharedPreferences(
                            MainActivity.TIME_ORGANISER_FILE_NAME, MainActivity.MODE_PRIVATE);
        prefs.edit().putInt(MainActivity.PREF_LAST_SELECTED_WEEK, (prefs.getInt(
                MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER) - 1)).commit();

        final android.support.v7.app.ActionBar actionBar = onCourseSelected.getActivity().getSupportActionBar();
        final MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(onCourseSelected.getContext());

        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, new SpinnerElementSelectedEvent(onCourseSelected.getActivity()));
        actionBar.setSelectedNavigationItem(prefs.getInt(MainActivity.WEEK_OF_SEMESTER,
                MainActivity.WEEKS_IN_SEMESTER) - 1);

    }

    private void loadJSONFileAndSetData(){
        URL[] urls = new URL[1];
        try {
            urls[0] = new URL("http://www.developer-alexandru.host56.com/orar_usv_1111b");
            new DataLoader(onCourseSelected, getChildFragmentManager(), currentItem, null, null, null).execute(urls);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setDataFromLoadedFile(){

        if(viewPager.getAdapter() == null){
            Log.d("TIMETABLE FRAG", "reset main adapter");

            viewPagerAdapter = new ViewPagerAdapter(onCourseSelected, getChildFragmentManager());

            viewPager.setAdapter(viewPagerAdapter);

            pagerSlidingTabStrip.setViewPager(viewPager);
        }else
            viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();

        final int lastVPPosition = onCourseSelected.getActivity().retrieveLastPosition();
        if(lastVPPosition == -1){
            Calendar cal = Calendar.getInstance();
            viewPager.setCurrentItem(cal.get(Calendar.DAY_OF_WEEK) - 1);
            Log.d("POST LOADING", ""+cal.get(Calendar.DAY_OF_WEEK) );
        }else
            viewPager.setCurrentItem(lastVPPosition);
    }

    public int retrieveLastPosition(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(MainActivity.VP_LAST_POSITION, -1);
    }

}

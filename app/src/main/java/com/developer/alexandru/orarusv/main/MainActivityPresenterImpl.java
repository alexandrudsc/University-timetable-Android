package com.developer.alexandru.orarusv.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.developer.alexandru.orarusv.CourseFragment;
import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.DBAdapter;
import com.developer.alexandru.orarusv.data.TimetableDownloaderService;

/**
 * Created by alexandru on 10/25/16.
 * The implementation of Presenter for MainActivity
 */
public class MainActivityPresenterImpl implements MainActivityPresenter {

    public static final String TIMETABLE_FRAGMENT_TAG = "this_is_timetable_fragment";

    private MainActivityView view;
    //Main fragment
    private TimetableFragment timetableFragment = null;

    public MainActivityPresenterImpl (MainActivityView view) {
        this.view = view;
    }

    @Override
    public void onNewIntent(Intent intent) {
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
        } else if (action.equals(CourseFragment.actionViewDetails)) {
            // Activity started from app widget
            c = bundle.getParcelable(CourseFragment.EXTRA_COURSE_KEY);
            onCourseClicked(c);
            return;
        } else
            return;

        DBAdapter dbAdapter = new DBAdapter(view.getContext());
        dbAdapter.open();
        c = dbAdapter.getCourse(name, type);
        dbAdapter.close();
        //Searching has the same result as clicking on that course from anywhere;
        onCourseClicked(c);

    }

    @Override
    public boolean onCourseClicked(Course course) {
        FragmentManager fm = view.getSupportFragmentManager();
        CourseFragment courseFragment;
        courseFragment = (CourseFragment) fm.findFragmentById(R.id.searchable_fragment);

        if(courseFragment != null){
            //Two-pane layout
            if(courseFragment.getView() == null){
                courseFragment = new CourseFragment(course, fm);
                fm.beginTransaction().replace(R.id.searchable_container, courseFragment).commit();
            }else
                courseFragment.updateContent(course, fm);
        }else{
            //One pane layout
            courseFragment = new CourseFragment(course, fm);
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            ft.replace(R.id.fragment_container, courseFragment, "replacement_searchable");
            ft.addToBackStack("replacement_searchable");
            ft.commit();
        }

        return true;
    }

    @Override
    public void initialize() {

        final FragmentManager fm = view.getSupportFragmentManager();
        ViewGroup fragmentContainer = (ViewGroup) view.findViewById(R.id.fragment_container);

        if(fragmentContainer != null){
            //One-pane layout
            timetableFragment = (TimetableFragment) fm.findFragmentByTag(TIMETABLE_FRAGMENT_TAG);
            if(timetableFragment == null)
                timetableFragment = new TimetableFragment();
            fm.beginTransaction().replace(R.id.fragment_container, timetableFragment, TIMETABLE_FRAGMENT_TAG).commit();
        } else {
            //Two pane layout

        }

        Intent intent = view.getIntent();
        if (intent == null || intent.getAction() == null)
            return;                                                 // App started normal

        // Check if activity was started from app widget. Must show the course
        if (intent.getAction().equals(CourseFragment.actionViewDetails)) {
            final Bundle bundle = intent.getExtras();
            Course c;
            if (bundle != null){
                c = bundle.getParcelable(CourseFragment.EXTRA_COURSE_KEY);
                onCourseClicked(c);
            }
        }
    }

    @Override
    public void checkForNewTimeStructure() {
        if (view == null)
            return;
        SharedPreferences prefs = view.getContext().getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        boolean appFirstRun = prefs.getBoolean(MainActivity.PREF_APP_FIRST_RUN, true);
        if (!appFirstRun)
            return;
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(MainActivity.PREF_APP_FIRST_RUN, false);
        edit.commit();

        Intent downloadIntent = new Intent(view.getContext(), TimetableDownloaderService.class);
        view.getContext().startService(downloadIntent);
    }
}

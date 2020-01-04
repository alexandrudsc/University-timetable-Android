package com.developer.alexandru.orarusv.main;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.developer.alexandru.orarusv.ChooseTimetableActivity;
import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.app_widget.TimetableWidgetProvider;
import com.developer.alexandru.orarusv.changelog.ChangelogActivity;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.download.DownloadActivity;
import com.developer.alexandru.orarusv.view_pager.PagerSlidingTabStrip;
import com.developer.alexandru.orarusv.view_pager.TimetableViewPagerAdapter;

import java.util.Calendar;

/**
 * Created by Alexandru on 7/12/14. The fragment displayed as activity_main page in the
 * activity_main activity. Contains the view pager with 7 fragments corresponding to a whole week
 */
public class TimetableFragment extends Fragment {
  // debug
  private static final boolean D = true;
  private static final String TAG = "TimetableFragment";

  private ViewPager viewPager;
  private PagerSlidingTabStrip pagerSlidingTabStrip;

  private OnCourseSelected onCourseSelected;
  // Used to refresh current item
  private int currentItem;

  private Calendar calendar;

  public static final String DETAILS_FRAGMENT_TAG = "this_is_details_frag";

  /**
   * Implemented by the activity hosting this fragment Main functionality is to determine the event
   * that will happen at click on a course Also contains a reference to the activity_main activity
   */
  public interface OnCourseSelected {
    MainActivity getActivity();

    FragmentManager getFragManager();

    boolean onCourseClicked(Course c);

    void refreshCourses();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      onCourseSelected = (OnCourseSelected) activity;
    } catch (ClassCastException e) {
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
      ((MainActivity) getActivity())
          .getSupportActionBar()
          .setTitle("Săptămâna " + Utils.getCurrentWeek(getActivity()));
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (D) Log.d(TAG, "create view");
    View fragmentView = inflater.inflate(R.layout.fragment_timetable, container, false);

    if (calendar == null) calendar = Calendar.getInstance();
    // Create here the view pager adapter so I can call getActivity() on a DayFragment anytime
    // during it's lifecycle
    viewPager = fragmentView.findViewById(R.id.view_pager);
    viewPager.setAdapter(
        new TimetableViewPagerAdapter((MainActivity) getActivity(), getChildFragmentManager()));
    viewPager.setOffscreenPageLimit(TimetableViewPagerAdapter.NUM_DAYS);
    pagerSlidingTabStrip = fragmentView.findViewById(R.id.sliding_tabs);
    pagerSlidingTabStrip.setViewPager(viewPager);
    viewPager.setCurrentItem(calendar.get(Calendar.DAY_OF_WEEK) - 1);
    return fragmentView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    // onCourseSelected.getActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    initializeActionBar();
  }

  @Override
  public void onPrepareOptionsMenu(@NonNull Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(R.id.search_from_menu).setVisible(true);
    menu.findItem(R.id.download_from_menu).setVisible(true);
    menu.findItem(R.id.choose_timetable_from_menu).setVisible(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {

    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) currentItem = savedInstanceState.getInt("current_item");
    ((MainActivity) getActivity())
        .getSupportActionBar()
        .setTitle("Săptămâna " + Utils.getCurrentWeek(getActivity()));
  }

  @Override
  public void onResume() {
    super.onResume();
    if (D) Log.d(TAG, "resumed");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (D) Log.d(TAG, "on create options");
    switch (item.getItemId()) {
      case R.id.download_from_menu:
        Intent intent = new Intent(getActivity(), DownloadActivity.class);
        this.startActivityForResult(intent, MainActivity.REQUEST_CODE_DOWNLOAD);
        return true;
      case R.id.choose_timetable_from_menu:
        Intent intentChooseTimetable = new Intent(getActivity(), ChooseTimetableActivity.class);
        this.startActivityForResult(
            intentChooseTimetable, MainActivity.REQUEST_CODE_PICK_TIMETABLE);
        return true;
      case R.id.menu_show_changelog:
        Intent intentShowChangelog = new Intent(getActivity(), ChangelogActivity.class);
        this.startActivity(intentShowChangelog);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (D) Log.d(TAG, "onActivityResult");
    for (Fragment fr : getChildFragmentManager().getFragments())
      if (fr != null) {
        fr.onActivityResult(requestCode, resultCode, data);
      }

    if (requestCode == MainActivity.REQUEST_CODE_PICK_TIMETABLE) {
      // timetable was changed. Update all the external widgets.
      final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
      final ComponentName appWidget =
          new ComponentName(getContext(), TimetableWidgetProvider.class.getName());
      final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void initializeActionBar() {
    // OnCourseSelected is an interface implemented by the activity_main activity in order to
    // communicate with
    // the fragments.
    SharedPreferences prefs =
        onCourseSelected
            .getActivity()
            .getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, MainActivity.MODE_PRIVATE);
    prefs
        .edit()
        .putInt(
            MainActivity.PREF_LAST_SELECTED_WEEK,
            (prefs.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER) - 1))
        .commit();
    ((MainActivityView) getActivity()).enableNavDrawer(true);
  }
}

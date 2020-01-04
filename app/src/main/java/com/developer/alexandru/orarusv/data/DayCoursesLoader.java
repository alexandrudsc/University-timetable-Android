package com.developer.alexandru.orarusv.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;

import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.view_pager.DayFragment;
import com.developer.alexandru.orarusv.view_pager.DayListViewAdapter;

import java.util.ArrayList;

/** Created by alexandru on 8/29/16. */
public class DayCoursesLoader extends AsyncTask<Integer, Void, ArrayList<Course>> {

  // Debug
  public static final String TAG = "DayCoursesLoader";
  public static final boolean D = true;

  private FragmentActivity activity;
  private Context context;
  DayFragment fragment;

  public DayCoursesLoader(FragmentActivity activity, DayFragment fragment) {
    this.activity = activity;
    this.context = activity;
    this.fragment = fragment;
  }

  @Override
  protected ArrayList<Course> doInBackground(Integer... integers) {

    if (integers == null || integers.length != 2) return null;

    int currentDay = integers[0];
    int currentWeek = integers[1];
    DBAdapter dbAdapter = null;
    ArrayList<Course> courses = null;
    SharedPreferences timeOrganiser;

    if (activity != null)
      timeOrganiser =
          activity.getSharedPreferences(
              MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);
    else
      timeOrganiser =
          context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);
    // Get the current week ( this will be updated either by opening the app or without the user
    // intervention by the widget
    // service
    currentWeek =
        timeOrganiser.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
    if (activity != null) {
      try {
        // TimetableViewPagerAdapter.context = activity;

        // Open the connection with the local database

        // Thread created by the fragment hosting the viewpager
        dbAdapter = new DBAdapter(activity);
        dbAdapter.open();
        courses =
            dbAdapter.getCourses(
                currentWeek, currentDay, Utils.getCurrentTimetableId(this.context));
      } catch (ArrayIndexOutOfBoundsException e) {
        e.printStackTrace();
      } catch (SQLiteException e) {
        e.printStackTrace();
      } finally {
        if (dbAdapter != null) dbAdapter.close();
      }
    }

    return courses;
  }

  @Override
  protected void onPostExecute(ArrayList<Course> courses) {
    super.onPostExecute(courses);
    DayListViewAdapter listAdapter = (DayListViewAdapter) fragment.getListAdapter();
    listAdapter.setValues(courses);
    listAdapter.notifyDataSetChanged();
  }
}

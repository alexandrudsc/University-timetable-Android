package com.developer.alexandru.orarusv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekActivity;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.CsvAPI;
import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.main.TimetableFragment;

/**
 * Created by Alexandru on 7/14/14. Details fragment - displays info about a selected class (details
 * and progress) DetailsFragment and ResultsFragment must be seen
 */
public class CourseFragment extends Fragment {
  public static final String TAG = "SEARCHABLE FRAGMENT";
  public static final boolean D = true;

  public static final String EXTRA_COURSE_KEY = "course_to_view";
  public static final String actionViewDetails = "com.alexandru.developer.VIEW_DETAILS";

  private Course course;
  private DetailsFragment detailsFragment;

  // private Button courseButton;
  private FrameLayout courseFragContainer;
  private FragmentManager fm;

  private Bundle detailsFragmentArgs;
  private boolean wasResultTable;

  // Toolbar used at horizontal two-pane layout
  private Toolbar toolbar;

  public CourseFragment() {
    super();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public CourseFragment(Course course, FragmentManager fm) {
    super();
    this.course = course;
    this.fm = fm;
    detailsFragmentArgs = new Bundle();
    detailsFragmentArgs.putParcelable("course", course);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (D)
      if (course == null) Log.d(TAG, "create void view");
      else Log.d(TAG, "create view");

    View fragmentView = inflater.inflate(R.layout.fragment_searchable, container, false);
    FragmentManager fm = getChildFragmentManager();
    TextView courseTitle = fragmentView.findViewById(R.id.course_title);

    if (course == null) {
      if (savedInstanceState != null) {
        if (D) Log.d(TAG, "create with bundle");
        wasResultTable = savedInstanceState.getBoolean("was_results_table");
        course = savedInstanceState.getParcelable("course");
        if (course != null) {
          if (D) Log.d(TAG, course.getFullName() + " " + course.getName());
          if (course.getFullName() == null)
            courseTitle.setText(course.getName() + " " + course.getType());
          else courseTitle.setText(course.getFullName() + " " + course.getType());

          if (wasResultTable) {
            // The results table was displayed
            if (D) Log.d(TAG, "recreating with results table");
            ResultsFragment resultsFragment =
                new ResultsFragment(fm, course.getName(), course.getType(), course.getParity());
            FragmentTransaction tr = fm.beginTransaction();
            tr.replace(
                R.id.course_fragment_container,
                resultsFragment,
                DetailsFragment.REPLACE_DETAILS_WITH_RESULT);
            tr.addToBackStack(DetailsFragment.REPLACE_DETAILS_WITH_RESULT);
            tr.commit();
          } else {
            // The details of course were displayed
            courseFragContainer =
                    fragmentView.findViewById(R.id.course_fragment_container);

            detailsFragmentArgs = new Bundle();
            detailsFragmentArgs.putParcelable("course", course);

            detailsFragment = new DetailsFragment(fm, true);
            detailsFragment.setArguments(detailsFragmentArgs);
            detailsFragment.onCreateView(inflater, courseFragContainer, savedInstanceState);
          }
        }
        return fragmentView;
      }
    }

    courseFragContainer = fragmentView.findViewById(R.id.course_fragment_container);

    detailsFragment = new DetailsFragment(getChildFragmentManager(), true);
    detailsFragment.setArguments(detailsFragmentArgs);
    detailsFragment.onCreateView(inflater, courseFragContainer, savedInstanceState);

    if (course == null) {
      // Search result
      courseTitle.setText("Niciun rezultat");
      fragmentView.findViewById(R.id.course_button).setVisibility(View.INVISIBLE);
    } else {
      if (course.getFullName() == null)
        courseTitle.setText(course.getName() + " " + course.getType());
      else courseTitle.setText(course.getFullName() + " " + course.getType());
    }

    // Find the toolbar (two-pane layout)
    toolbar = fragmentView.findViewById(R.id.toolbar_searchable);
    return fragmentView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    if (D) Log.d("NonCurrentWeek", "create options");
    // ((MainActivity)
    // getActivity()).getSupportActionBar().setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
    if (toolbar == null) // One pane layout
    getActivity().getMenuInflater().inflate(R.menu.searchable_fragment, menu);
    else // Two pane layout
    toolbar.inflateMenu(R.menu.searchable_fragment);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    MenuItemCompat.collapseActionView(menu.findItem(R.id.search_from_menu));
    menu.findItem(R.id.search_from_menu).setVisible(false);
    menu.findItem(R.id.download_from_menu).setVisible(false);
    menu.findItem(R.id.choose_timetable_from_menu).setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add_note:
        Intent editNoteIntent = new Intent(this.getActivity(), NoteActivity.class);
        editNoteIntent.putExtra(NoteActivity.COURSE_NAME_EXTRA, course.getName());
        startActivity(editNoteIntent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (D) Log.d(TAG, "save state");
    wasResultTable =
        getChildFragmentManager().findFragmentByTag(DetailsFragment.REPLACE_DETAILS_WITH_RESULT)
            != null;
    if (D) Log.d(TAG, "was result table: " + wasResultTable);
    outState.putBoolean("was_results_table", wasResultTable);
    if (course != null) outState.putParcelable("course", course);
    else
      try {
        outState.putParcelable("course", detailsFragmentArgs.getParcelable("course"));
      } catch (NullPointerException e) {
        // In two pane layout and no course was selected yet
      }
    super.onSaveInstanceState(outState);
  }

  public void updateContent(Course c, FragmentManager fm) {
    View fragmentView = this.getView();

    TextView courseTitle = fragmentView.findViewById(R.id.course_title);
    if (c.getFullName() == null) courseTitle.setText(c.getName() + " " + c.getType());
    else courseTitle.setText(c.getFullName() + " " + c.getType());

    detailsFragmentArgs = new Bundle();
    detailsFragmentArgs.putParcelable("course", c);
    detailsFragment = new DetailsFragment(getChildFragmentManager(), false);
    detailsFragment.setArguments(detailsFragmentArgs);
    fm.beginTransaction()
        .replace(
            R.id.course_fragment_container, detailsFragment, TimetableFragment.DETAILS_FRAGMENT_TAG)
        .commit();
  }

  /**
   * Obtain an object with the number of skipped and attained classes of a course
   *
   * @param context application context
   * @param name the name of the course
   * @param type the type of the course
   * @param info the info about course(e.g. "saptamanile pare")
   * @return the AbsPres object
   */
  public static SearchableActivity.AbsPres getResults(
      Context context, String name, String type, String info) {
    if (name == null || type == null || info == null) return null;
    SearchableActivity.AbsPres result = new SearchableActivity.AbsPres();
    result.absences = result.presences = 0;
    int i;
    if (info.equals(CsvAPI.EVEN_WEEK)) {
      for (i = 2; i <= MainActivity.WEEKS_IN_SEMESTER; i += 2) {
        boolean wasPresent = false;
        if (context
            .getSharedPreferences(
                NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i, Context.MODE_PRIVATE)
            .getBoolean(name + "_" + type, false)) {
          result.presences++;
          wasPresent = true;
        } else result.absences++;

        result.table.put(i, wasPresent);
      }
    } else if (info.equals(CsvAPI.ODD_WEEK)) {
      for (i = 1; i <= MainActivity.WEEKS_IN_SEMESTER; i += 2) {
        boolean wasPresent = false;
        if (context
            .getSharedPreferences(
                NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i, Context.MODE_PRIVATE)
            .getBoolean(name + "_" + type, false)) {
          result.presences++;
          wasPresent = true;
        } else result.absences++;

        result.table.put(i, wasPresent);
      }
    } else {
      for (i = 1; i <= MainActivity.WEEKS_IN_SEMESTER; i++) {
        boolean wasPresent = false;
        if (context
            .getSharedPreferences(
                NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i, Context.MODE_PRIVATE)
            .getBoolean(name + "_" + type, false)) {
          result.presences++;
          wasPresent = true;
        } else result.absences++;
        result.table.put(i, wasPresent);
      }
    }

    return result;
  }
}

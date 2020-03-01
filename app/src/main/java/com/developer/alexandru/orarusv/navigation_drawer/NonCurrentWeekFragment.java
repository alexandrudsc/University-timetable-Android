package com.developer.alexandru.orarusv.navigation_drawer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.action_bar.ListViewAdapterNonCurWeek;
import com.developer.alexandru.orarusv.main.MainActivity;

/** Created by Alexandru on 8/1/14. */
public class NonCurrentWeekFragment extends Fragment {
  public static final String NAME_OF_WEEK_NUMBER = "week_number";
  public static final String PARTIAL_NAME_BACKUP_FILE = "saptamana_";
  public static final String WEEK_NUMBER = "selected_week";

  private int weekNumber;
  private String backupFileName;
  private MainActivity activity;

  public NonCurrentWeekFragment() {
    super();
  }

  public NonCurrentWeekFragment(MainActivity activity) {
    this.activity = activity;
  }

  @Override
  public void setArguments(Bundle args) {
    super.setArguments(args);

    weekNumber = args.getInt(NAME_OF_WEEK_NUMBER);
    backupFileName = PARTIAL_NAME_BACKUP_FILE + weekNumber;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (savedInstanceState != null) weekNumber = savedInstanceState.getInt(WEEK_NUMBER);
    View fragmentView = inflater.inflate(R.layout.non_current_week_activity, container, false);
    ListView listView = fragmentView.findViewById(android.R.id.list);
    listView.setAdapter(new ListViewAdapterNonCurWeek((MainActivity)getActivity(), backupFileName, weekNumber));
    return fragmentView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    Log.d("NonCurrentWeek", "create options");
    menu.findItem(R.id.search_from_menu).setVisible(false);
    menu.findItem(R.id.download_from_menu).setVisible(false);
    menu.findItem(R.id.choose_timetable_from_menu).setVisible(false);

    ActionBar supportActionBar = ((MainActivity) getActivity()).getSupportActionBar();
    if (supportActionBar == null)
    {
      return;
    }
    supportActionBar.setTitle("Săptămâna " + weekNumber);
    supportActionBar.setDisplayHomeAsUpEnabled(false);
    supportActionBar.setHomeButtonEnabled(false);

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(WEEK_NUMBER, weekNumber);
    Log.d("Non current", "state saved");
  }
}

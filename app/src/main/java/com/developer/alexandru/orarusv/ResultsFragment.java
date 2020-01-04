package com.developer.alexandru.orarusv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekActivity;
import com.developer.alexandru.orarusv.data.CsvAPI;
import com.developer.alexandru.orarusv.main.MainActivity;

import java.util.HashMap;

/**
 * Created by Alexandru on 7/14/14. Fragment displaying a table with the number of classes the user
 * has attained to and the classes the user has skipped Will be contained in CourseFragment
 */
public class ResultsFragment extends Fragment {

  // Debug
  public static final String TAG = "ResultsFragment";
  public static final boolean D = true;

  private AbsPres absPres;
  private Context context;
  public FragmentManager fm;

  private String name;
  private String type;
  private String info;
  private String parity;

  public ResultsFragment() {
    super();
  }

  public ResultsFragment(FragmentManager fm, String name, String type, String info) {
    this.fm = fm;
    this.name = name;
    this.type = type;
    this.info = info;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    context = activity;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      String[] savedData = savedInstanceState.getStringArray("values");
      this.name = savedData[0];
      this.type = savedData[1];
      this.info = savedData[2];
      absPres = getResults(name, type, info);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putStringArray("values", new String[] {name, type, info});
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_results, container, false);
    absPres = getResults(name, type, info);
    if (absPres == null) return null;
    TableLayout tableLayout = fragmentView.findViewById(R.id.table);
    ImageButton exitBtn = fragmentView.findViewById(R.id.close_results_button);

    exitBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            if (fm == null)
              // configuration changes.Get a new instance for the child fragment manager
              fm = getParentFragment().getChildFragmentManager();
            if (D)
              Log.d(
                  TAG,
                  "fragments in backStack: "
                      + fm.getBackStackEntryCount()
                      + " at 0: "
                      + fm.getBackStackEntryAt(0));

            fm.popBackStack(
                DetailsFragment.REPLACE_DETAILS_WITH_RESULT,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
          }
        });
    createTable(absPres, tableLayout);
    return fragmentView;
  }

  private AbsPres getResults(String name, String type, String info) {
    if (name == null || type == null || info == null) return null;
    AbsPres result = new AbsPres();
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

  private TableLayout createTable(AbsPres absPres, TableLayout tableLayout) {

    if (absPres == null) return tableLayout;

    int total = absPres.absences + absPres.presences;

    TableRow.LayoutParams tableRowTitleParams =
        new TableRow.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    tableRowTitleParams.weight = 1;
    tableRowTitleParams.span = 3;

    TableRow.LayoutParams tableRowParams =
        new TableRow.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    tableRowParams.weight = 1;

    TableRow tableRow = new TableRow(context);
    tableRow.setGravity(Gravity.CENTER);
    tableRow.setPadding(1, 1, 1, 1);
    tableRow.setLayoutParams(tableRowTitleParams);

    TextView tv = new TextView(context);
    tv.setGravity(Gravity.CENTER);
    tv.setPadding(2, 2, 2, 2);
    tv.setText("Total: " + total);
    tv.setTextColor(Color.BLACK);
    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

    tableRow.addView(tv);
    tableLayout.addView(tableRow);

    tableRow = new TableRow(context);
    tableRow.setLayoutParams(tableRowParams);
    for (int i = 1; i <= 3; i++) {
      tv = new TextView(context);
      tv.setGravity(Gravity.CENTER);
      tv.setBackgroundColor(Color.WHITE);
      tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
      tv.setPadding(1, 1, 1, 1);
      tv.setBackgroundResource(R.drawable.cell_shape);
      switch (i) {
        case 1:
          break;
        case 2:
          tv.setText(absPres.presences + " prezențe ");
          break;
        case 3:
          tv.setText(absPres.absences + " absențe");
          break;
      }
      tableRow.addView(tv);
    }
    tableLayout.addView(tableRow);

    for (int i = 1; i <= MainActivity.WEEKS_IN_SEMESTER; i++) {

      Boolean wasPresent = absPres.table.get(i);
      if (wasPresent != null) {
        tableRow = new TableRow(context);
        tableRow.setLayoutParams(tableRowParams);
        tableRow.setPadding(1, 1, 1, 1);

        for (int j = 1; j <= 3; j++) {
          tv = new TextView(context);
          tv.setGravity(Gravity.CENTER);
          tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
          tv.setPadding(1, 1, 1, 1);
          tv.setBackgroundResource(R.drawable.cell_shape);
          switch (j) {
            case 1:
              tv.setText("Săptămâna " + i);
              break;
            case 2:
              if (wasPresent) tv.setText("X");
              break;
            case 3:
              if (!wasPresent) tv.setText("X");
              break;
          }
          tableRow.addView(tv);
        }
        tableLayout.addView(tableRow);
      }
    }

    return tableLayout;
  }

  private class AbsPres {
    public int absences;
    public int presences;
    HashMap<Integer, Boolean> table = new HashMap<Integer, Boolean>();
  }
}

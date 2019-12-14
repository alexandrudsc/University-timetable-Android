package com.developer.alexandru.orarusv.action_bar;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.R;

/**
 * Created by Alexandru on 8/1/14.
 */
public class NonCurrentWeekFragment extends Fragment {
    public static final String NAME_OF_WEEK_NUMBER = "week_number";
    public static final String PARTIAL_NAME_BACKUP_FILE = "saptamana_";
    public static final String WEEK_NUMBER ="selected_week";

    int weekNumber;
    String backupFileName;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            weekNumber = savedInstanceState.getInt(WEEK_NUMBER);
        View fragmentView = inflater.inflate(R.layout.non_current_week_activity, container, false);
        ListView listView = (ListView)fragmentView.findViewById(android.R.id.list);
        listView.setAdapter(new ListViewAdapterNonCurWeek(activity, backupFileName, weekNumber));
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("NonCurrentWeek", "create options");
        menu.findItem(R.id.search_from_menu).setVisible(false);
       ((MainActivity)getActivity()).getSupportActionBar().setTitle("Săptămâna " + weekNumber);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(WEEK_NUMBER, weekNumber);
        Log.d("Non current", "state saved");
    }

}

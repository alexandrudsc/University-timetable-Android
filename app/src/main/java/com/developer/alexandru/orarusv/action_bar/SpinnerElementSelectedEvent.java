package com.developer.alexandru.orarusv.action_bar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.R;

/**
 * Created by Alexandru on 6/1/14.
 */
public class SpinnerElementSelectedEvent implements Spinner.OnItemSelectedListener{//ActionBar.OnNavigationListener {
    public static final String
                        ACTION_LAUNCH_EVENT_ACTIVITY = "com.alexandru.developer.action.LAUNCH_NON_CURRENT_WEEK_ACTIVITY";
    public static final String
                        CATEGORY_NON_CURRENT_WEEK = "com.alexandru.developer.category.SECOND_ACTIVITY";
    public static final String REPLACEMENT_NON_CURRENT = "replace_timetable_non_current";

    private Context context;
    private MainActivity activity;
    public SpinnerElementSelectedEvent(MainActivity activity) {
        this.activity = activity;
        this.context = activity;
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemId) {
        onNavigationItemSelected(position, itemId);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
          //TODO: dunno yet
    }


        public boolean onNavigationItemSelected(int position, long itemId) {

        SharedPreferences prefs = activity.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);

        if(position != prefs.getInt(MainActivity.PREF_LAST_SELECTED_WEEK, MainActivity.WEEKS_IN_SEMESTER - 1) ) {
            Intent intent = new Intent(context, NonCurrentWeekActivity.class);
            intent.setAction(ACTION_LAUNCH_EVENT_ACTIVITY);

            intent.putExtra(NonCurrentWeekActivity.NAME_OF_WEEK_NUMBER, position + 1);
            //context.startActivity(intent);
            prefs.edit().putInt(MainActivity.PREF_LAST_SELECTED_WEEK, position).commit();

            Bundle args = new Bundle();
            args.putInt(NonCurrentWeekFragment.NAME_OF_WEEK_NUMBER, position + 1);
            NonCurrentWeekFragment fragment = new NonCurrentWeekFragment(activity);
            fragment.setArguments(args);
            if (activity.findViewById(R.id.fragment_container) != null) {
                //One pane layout
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment, REPLACEMENT_NON_CURRENT);
                ft.addToBackStack(REPLACEMENT_NON_CURRENT);
                ft.commit();
            } else {
                //Two pane layout
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.timetable_container, fragment, REPLACEMENT_NON_CURRENT);
                ft.addToBackStack(REPLACEMENT_NON_CURRENT);
                ft.commit();

            }
        }
        return true;
    }

}

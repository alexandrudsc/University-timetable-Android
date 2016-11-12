package com.developer.alexandru.orarusv.view_pager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.developer.alexandru.orarusv.*;
import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekActivity;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.main.TimetableFragment;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/16/14.
 * Adapter for the list with courses in each DayFragment
 */
public class DayListViewAdapter extends BaseAdapter {

    private ArrayList<Course> values;
    private Context context;

    private SharedPreferences currentWeeksProgress;
    private int currentWeek;
    private String currentWeekFileName;
    private CheckBoxOnChangeListener checkBoxOnChangeListener;
    private TimetableFragment.OnCourseSelected onCourseSelected;

    // Used to display a dialog with a list of possible courses, at LongItemClicked
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public DayListViewAdapter(TimetableFragment.OnCourseSelected onCourseSelected,
                              ArrayList<Course> values) {
        this.onCourseSelected = onCourseSelected;
        this.context = onCourseSelected.getActivity();
        this.values = values;
        if (this.values == null) {
            this.values = new ArrayList<>();
        }

        this.currentWeek = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,
                            Context.MODE_PRIVATE).
                            getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
        this.currentWeekFileName = NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + currentWeek;

        currentWeeksProgress = context.getSharedPreferences(currentWeekFileName,
                                                            Context.MODE_PRIVATE);

        // Builder for the dialog displayed at ItemLongClick
        builder = new AlertDialog.Builder(onCourseSelected.getActivity());

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        if(values == null)
            return 0;
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.eventType = (TextView) convertView.findViewById(R.id.course_description);
            viewHolder.eventCheckBox = (CheckBox) convertView.findViewById(R.id.event_checkbox);
            viewHolder.eventName = (TextView)convertView.findViewById(R.id.course_name);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        if(checkBoxOnChangeListener == null)
            checkBoxOnChangeListener = new CheckBoxOnChangeListener();
        final Course c = values.get(position);
        viewHolder.eventCheckBox.setTag(currentWeekFileName + ";" + c.name+ "_" + c.type);
        viewHolder.eventCheckBox.setOnCheckedChangeListener(checkBoxOnChangeListener);


        viewHolder.eventName.setText (c.name.toUpperCase());
        viewHolder.eventType.setText (c.type + "\n" + c.time + "\n" + c.location);
        boolean currentCourseProgress = getCourseProgress(viewHolder.eventCheckBox.getContext(),
                                                            viewHolder.eventCheckBox.getTag());
        viewHolder.eventCheckBox.setChecked(currentCourseProgress);

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout_course);
        layout.setOnClickListener(new OnCourseClickListener(this.onCourseSelected, c));
        layout.setOnLongClickListener(new OnCourseLongClickListener(this.onCourseSelected, c));

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return (values == null || values.size() == 0);
    }

    public void setValues(ArrayList<Course> values){
        this.values.clear();
        if (values == null) {
            this.notifyDataSetChanged();
            return;
        }
        this.values.addAll(values);
        this.notifyDataSetChanged();
    }

    public ArrayList<Course> getValues(){
        return this.values;
    }

    public static class ViewHolder{
        public TextView eventName, eventType;
        public CheckBox eventCheckBox;
    }

    private boolean getCourseProgress(Context context, Object tag) {
        if (! (tag instanceof String))
            return false;
        final String str = (String)tag;
        String[] fileAndPreference = str.split(";");
        final SharedPreferences currentWeekProgressFile = context.getSharedPreferences(fileAndPreference[0],
                                                        Context.MODE_PRIVATE);
        return currentWeekProgressFile.getBoolean(fileAndPreference[1], false);
    }
}


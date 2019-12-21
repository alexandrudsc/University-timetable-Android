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
        CourseViewHolder courseViewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.course_item_layout, parent, false);
            courseViewHolder = new CourseViewHolder(convertView);
        }
        else {
            courseViewHolder = (CourseViewHolder) convertView.getTag();
        }

        final Course c = values.get(position);
        courseViewHolder.populate(convertView, onCourseSelected, currentWeekFileName, c);

        boolean currentCourseProgress = getCourseProgress(courseViewHolder.eventCheckBox.getContext(),
                                                            courseViewHolder.eventCheckBox.getTag());
        courseViewHolder.setAttendance(currentCourseProgress);

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

    public static class CourseViewHolder {
        public TextView eventName, eventType;
        public CheckBox eventCheckBox;

        /**
         * Used when the course view holder is drawed on the screen only with the weekday name.
         * For example the full week view on a single page also includes days name.
         */
        public CourseViewHolder() {
        }

        /**
         * Sets up the course's elements to point to their graphical equivalents from the screen
         * @param view - the view on the screen which must hold the courses color, the name
         *             and the description together with the checkbox which marks this course
         *             as attended to, or not
         */
        public CourseViewHolder(View view) {
            eventName = view.findViewById(R.id.course_name);
            eventType = view.findViewById(R.id.course_description);
            eventCheckBox = view.findViewById(R.id.event_checkbox);
            view.setTag(this);
        }

        /**
         * Populates the data into the view and also sets the handlers for click and long click
         * actions
         * @param view the graphical view on the screen
         * @param onCourseSelected - the handler for when the visual element is selected
         * @param fileName - the file on the disk which reminds me if this course
         *                  was as attended to, or not
         * @param c - the course structured data
         */
        public void populate(View view, TimetableFragment.OnCourseSelected onCourseSelected,
                             String fileName, Course c){
            eventName.setText(c.getName().toUpperCase());
            eventType.setText(c.getType() + "\n" + c.getTime() + "\n" +
                    c.getLocation());

            eventCheckBox.setTag(fileName + ";" + c.getName() + "_" + c.getType());
            eventCheckBox.setOnCheckedChangeListener(new CheckBoxOnChangeListener());

            View layout = view.findViewById(R.id.layout_course);
            layout.setOnClickListener(new OnCourseClickListener(onCourseSelected, c));
            layout.setOnLongClickListener(new OnCourseLongClickListener(onCourseSelected, c));
        }

        /**
         * Marks this course as attended to or not for a specific day
         * @param attendance - if true, then this course was taken for a specific day
         */
        public void setAttendance(boolean attendance){
            eventCheckBox.setChecked(attendance);
        }
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


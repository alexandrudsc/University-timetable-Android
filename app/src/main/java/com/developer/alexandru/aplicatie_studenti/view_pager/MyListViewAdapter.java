package com.developer.alexandru.aplicatie_studenti.view_pager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.developer.alexandru.aplicatie_studenti.*;
import com.developer.alexandru.aplicatie_studenti.action_bar.NonCurrentWeekActivity;
import com.developer.alexandru.aplicatie_studenti.data.Course;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/16/14.
 */
public class MyListViewAdapter extends BaseAdapter {

    private ArrayList<Course> values;
    private Context context;

    private SharedPreferences currentWeeksProgress;
    private int currentWeek;
    private String currentWeekFileName;
    private TimetableFragment.OnCourseSelected onCourseSelected;

    public MyListViewAdapter(TimetableFragment.OnCourseSelected onCourseSelected,
                             ArrayList<Course> values) {
        this.onCourseSelected = onCourseSelected;
        this.context = onCourseSelected.getContext();
        this.values = values;
        this.currentWeek = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,
                            Context.MODE_PRIVATE).
                            getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
        this.currentWeekFileName = NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + currentWeek;

        currentWeeksProgress = context.getSharedPreferences(currentWeekFileName,
                                                            Context.MODE_PRIVATE);
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
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

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
            viewHolder.eventName = (TextView)convertView.findViewById(R.id.event_name);
            viewHolder.eventType = (TextView)convertView.findViewById(R.id.event_type);
            viewHolder.eventTime = (TextView)convertView.findViewById(R.id.event_time);
            viewHolder.eventLocation = (TextView)convertView.findViewById(R.id.event_location);

            viewHolder.eventCheckBox = (CheckBox)convertView.findViewById(R.id.event_checkbox);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        final Course c = values.get(position);
        viewHolder.eventName.setText(c.name);
        viewHolder.eventType.setText(c.type);
        viewHolder.eventTime.setText(c.time);
        viewHolder.eventLocation.setText(c.location);
        viewHolder.eventCheckBox.setOnCheckedChangeListener(new CheckBoxOnChangeListener(
                                                                                currentWeekFileName,
                                                                                c.name+ "_" +
                                                                                c.type ) );

        boolean currentCourseProgress;
        if(currentWeeksProgress == null)
            currentWeeksProgress = context.getSharedPreferences(currentWeekFileName, Context.MODE_PRIVATE);

        currentCourseProgress = currentWeeksProgress.getBoolean(c.name+ "_" +
                c.type, false);
        if(currentCourseProgress)
            viewHolder.eventCheckBox.setChecked(true);

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.course_in_list_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CLICKED ON", c.fullName);
                onCourseSelected.onCourseClicked(c);
                view.setSelected(true);
            }
        });

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
        this.values.addAll(values);
        this.notifyDataSetChanged();
    }

    public ArrayList<Course> getValues(){
        return this.values;
    }

    public static class ViewHolder{
        public TextView eventName, eventType, eventTime, eventLocation;
        ImageView eventColor;
        CheckBox eventCheckBox;
    }

}


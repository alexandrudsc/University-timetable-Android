package com.developer.alexandru.orarusv.view_pager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.developer.alexandru.orarusv.*;
import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekActivity;
import com.developer.alexandru.orarusv.data.CSVParser;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.DBAdapter;
import com.developer.alexandru.orarusv.data.DialogListAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alexandru on 6/16/14.
 * Adapter for the list with courses in each DayFragment
 */
public class MyListViewAdapter extends BaseAdapter {

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

    public MyListViewAdapter(TimetableFragment.OnCourseSelected onCourseSelected,
                             ArrayList<Course> values) {
        this.onCourseSelected = onCourseSelected;
        this.context = onCourseSelected.getActivity();
        this.values = values;

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
            /*viewHolder.eventType = (TextView)convertView.findViewById(R.id.event_type);
            viewHolder.eventTime = (TextView)convertView.findViewById(R.id.event_time);
            viewHolder.eventLocation = (TextView)convertView.findViewById(R.id.event_location);

            viewHolder.eventCheckBox = (CheckBox)convertView.findViewById(R.id.event_checkbox);
            */
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        if(checkBoxOnChangeListener == null)
            checkBoxOnChangeListener = new CheckBoxOnChangeListener();
        final Course c = values.get(position);
        /*viewHolder.eventName.setText(c.name);
        viewHolder.eventType.setText(c.type);
        viewHolder.eventTime.setText(c.time);
        viewHolder.eventLocation.setText(c.location);*/
        viewHolder.eventCheckBox.setTag(currentWeekFileName + ";" + c.name+ "_" + c.type);
        viewHolder.eventCheckBox.setOnCheckedChangeListener(checkBoxOnChangeListener);


        viewHolder.eventName.setText(c.name.toUpperCase());
        viewHolder.eventType.setText(c.type + "\n" + c.time + "\n" +
                c.location);
        boolean currentCourseProgress = false;
        /*if(currentWeeksProgress == null)
            currentWeeksProgress = context.getSharedPreferences(currentWeekFileName, Context.MODE_PRIVATE);

        currentCourseProgress = currentWeeksProgress.getBoolean(c.name+ "_" +
                c.type, false);
        */
        if(currentCourseProgress)
            viewHolder.eventCheckBox.setChecked(true);

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout_course);//course_in_list_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CLICKED ON", c.fullName);
                onCourseSelected.onCourseClicked(c);
                view.setSelected(true);
            }
        });

        layout.setOnLongClickListener(new ReplaceItem(c));

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
        public TextView eventName, eventType;
        public CheckBox eventCheckBox;
    }

    private class ReplaceItem implements View.OnLongClickListener{

        private Course course;

        public ReplaceItem(Course course) {
            this.course = course;
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("LONG CLICKED ON", course.toString());

            if (!Utils.hasInternetAccess(context)) {
                Utils.toastNoInternetAccess(context);
                return true;
            }

            DBAdapter dbAdapter = new DBAdapter(onCourseSelected.getActivity());
            //dbAdapter.open();


            ArrayList<Course> items = new ArrayList<>();

            DialogListAdapter adapter = new DialogListAdapter(context, R.layout.course_item_layout, items);

            builder.setTitle(R.string.choose)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Event handled by the inner view. See DialogListItem's implementation of getView()
                        }
                    });
            dialog = builder.create();
            adapter.setDialog(dialog);
            dialog.show();

            new Thread(new Run(adapter, course.profID)).start();

            //dbAdapter.replaceCourse(course, newCourse, "6");
            //dbAdapter.close();
            /*try {
                new DataLoader(onCourseSelected,onCourseSelected.getFragManager(),
                        2, null, null, null).execute();
            }catch (NullPointerException e){
                e.printStackTrace();
            }*/
            return true;
        }
    }

    private class Run implements Runnable{

        private DialogListAdapter adapter;
        private String profId;

        public Run(DialogListAdapter adapter, String profId) {
            super();
            this.adapter = adapter;
            this.profId = profId;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                URL url = new URL(DialogListAdapter.PROF_URL + this.profId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStreamReader is = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(is);

                ParallelCoursesParser parser = new ParallelCoursesParser(br);
                parser.parse();
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                conn.disconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private class ParallelCoursesParser extends CSVParser{

            private Course c;

            public ParallelCoursesParser(BufferedReader br) {
                super(br);
            }

            @Override
            public boolean handleData(String[] data) {
                c = new Course();
                c.fullName = data[12];
                c.name = data[13];
                adapter.add(c);
                return true;
            }
        }
    }

}


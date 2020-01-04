package com.developer.alexandru.orarusv.data;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;

import java.util.ArrayList;

/** Created by alexandru on 9/3/16. Adapter for alternative courses displayed as dialog. */
public class AlternativeCoursesListAdapter extends BaseAdapter {

  private Context context;
  private AlertDialog dialog;
  private int courseItemLayout;
  private ArrayList<Course> items;

  public AlternativeCoursesListAdapter(
      Context context, int course_item_layout, ArrayList<Course> items) {
    this.context = context;
    this.courseItemLayout = course_item_layout;
    this.items = items;
  }

  @Override
  public int getCount() {
    if (items == null) {
      return 0;
    }
    return items.size();
  }

  @Override
  public Object getItem(int i) {
    if (items == null) return null;
    return items.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    ViewHolder viewHolder = null;
    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(this.courseItemLayout, viewGroup, false);
      viewHolder = new ViewHolder();
      viewHolder.name = convertView.findViewById(R.id.course_name);
      viewHolder.description = convertView.findViewById(R.id.course_description);
      viewHolder.courseLayout = convertView.findViewById(R.id.course_layout);
      convertView.setTag(viewHolder);
    }

    if (viewHolder == null) {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    final Course course = this.items.get(i);
    viewHolder.name.setText(course.getFullName());
    viewHolder.description.setText(getDescription(course));
    viewHolder.courseLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.d("COURSE_ADAPTER", course.getFullName());
            if (context == null) return;
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            dbAdapter.updateCourseTime(course);
            dbAdapter.close();
            if (dialog != null) dialog.cancel();
          }
        });
    return convertView;
  }

  @NonNull
  private String getDescription(Course course) {
    StringBuilder description = new StringBuilder(course.getLocation() + ", ");
    description.append(
        course.getStartTime()
            + ":00 - "
            + course.getEndTime()
            + ":00"
            + ", "
            + Utils.getDayName(course.getDay()));
    description.append("\n");
    if (CsvAPI.ODD_WEEK.equals(course.getParity())) {
      description.append("Săptămâni impare");
    } else if (CsvAPI.EVEN_WEEK.equals(course.getParity())) {
      description.append("Săptămâni pare");
    }
    return description.toString();
  }

  public void setDialog(AlertDialog dialog) {
    this.dialog = dialog;
  }

  public boolean add(Course c) {
    return this.items.add(c);
  }

  private static class ViewHolder {
    TextView name;
    TextView description;
    LinearLayout courseLayout;
  }
}

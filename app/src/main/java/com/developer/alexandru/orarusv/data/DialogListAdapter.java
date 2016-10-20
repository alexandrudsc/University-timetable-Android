package com.developer.alexandru.orarusv.data;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.developer.alexandru.orarusv.R;

import java.util.ArrayList;

/**
 * Created by alexandru on 9/3/16.
 * Adapter for alternative courses displayed as dialog.
 */
public class DialogListAdapter extends BaseAdapter {


    private Context context;
    private AlertDialog dialog;
    private int courseItemLayout;
    private ArrayList<Course> items;


    public DialogListAdapter(Context context, int course_item_layout, ArrayList<Course> items) {
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
        if (items == null)
            return null;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.courseItemLayout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.course_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.course_description);
            convertView.setTag(viewHolder);
        }

        if (viewHolder == null) {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Course course = this.items.get(i);
        viewHolder.name.setText(course.fullName);

        viewHolder.description.setText(course.startTime + " - " + course.endTime + ", " + ", \n" + "Săptămâni: " + course.parity);
        return convertView;
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
    }
}

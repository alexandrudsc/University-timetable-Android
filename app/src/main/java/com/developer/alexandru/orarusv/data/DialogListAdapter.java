package com.developer.alexandru.orarusv.data;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by alexandru on 9/3/16.
 */
public class DialogListAdapter extends BaseAdapter {


    public static final String PROF_URL = "http://www.usv.ro/orar/vizualizare/data/orarSPG.php?mod=prof&ID=";

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
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public boolean add(Course c) {
        return this.items.add(c);
    }
}

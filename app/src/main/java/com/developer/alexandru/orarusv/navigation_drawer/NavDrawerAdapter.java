package com.developer.alexandru.orarusv.navigation_drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.alexandru.orarusv.R;

/**
 * Created by Alexandru on 7/1/14.
 * The adapter for the navigation drawer (helper for backing up the list with data)
 */
public class NavDrawerAdapter extends BaseAdapter{
    private final int NAV_DRAWER_ELEM_COUNT = 18;
    private String[] titles;
    private Context context;

    //Each title in the nav drawer
    public static final int CURRENT_WEEK = 0;
    public static final int HOLIDAYS = 2;
    public static final int HELP = 3;
    public static final int ABOUT = 4;

    public NavDrawerAdapter(Context context) {
        titles = context.getResources().getStringArray(R.array.drawer_list_elements);
        this.context = context;
    }

    @Override
    public int getCount() {
        return NAV_DRAWER_ELEM_COUNT;
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 && position > NAV_DRAWER_ELEM_COUNT)
            return null;
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_drawer_elem, parent, false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.nav_elem_tv);
        tv.setText(titles[position]);

        return convertView;
    }
}

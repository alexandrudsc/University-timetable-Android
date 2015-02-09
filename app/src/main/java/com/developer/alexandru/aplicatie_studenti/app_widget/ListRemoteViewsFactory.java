package com.developer.alexandru.aplicatie_studenti.app_widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.data.DBAdapter;
import com.developer.alexandru.aplicatie_studenti.data.Course;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alexandru on 5/30/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String TAG = "RemoteViewsFactory";

    public static final int VIEW_DETAILS_CODE = 100;

    private ArrayList<Course> valuesToday;
    private int mWidgetId;
    private Context mContext;
    private DBAdapter dbAdapter;

    public ListRemoteViewsFactory(Context context, Intent intent){
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "data set changed");
        if(dbAdapter == null)
            dbAdapter = new DBAdapter(this.mContext);
        dbAdapter.open();

        this.setValues(dbAdapter.getCourses(14, ViewPagerAdapter.days [Calendar.getInstance().get(Calendar.MINUTE) % 7]));

        dbAdapter.close();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (valuesToday == null)
            return 0;
        return valuesToday.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Construct a remote views object based on the app widget item XML file,
        // and set the text based on the position.This will be a row in the list
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout);
        if(valuesToday.size() > 0) {
            final Course c = valuesToday.get(position);

            remoteViews.setTextViewText(R.id.event_name_widget, c.name + " " +
                    c.type);
            remoteViews.setTextViewText(R.id.event_description_widget, c.time + " " +
                    c.location);

            remoteViews.setOnClickFillInIntent(R.id.widget_list_item, getIntentForFillIn(c));
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void setValues(ArrayList<Course> newValues){
        if(this.valuesToday != null){
            this.valuesToday.clear();
            this.valuesToday.addAll(newValues);
        }else
            this.valuesToday = newValues;
    }

    public boolean hasValues(){
        return valuesToday == null || valuesToday.size() == 0;
    }


    private Intent getIntentForFillIn(Course course) {
        Intent viewDetails = new Intent();
        Bundle args = new Bundle();
        args.putParcelable("course_to_view", course);
        viewDetails.putExtras(args);

        return viewDetails;

    }

}

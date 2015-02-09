package com.developer.alexandru.aplicatie_studenti.app_widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViewsService;
import com.developer.alexandru.aplicatie_studenti.data.DataLoader;

/**
 * Created by Alexandru on 5/30/14.
 */
/*
 Shouldn't check if file is loaded !
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListWidgetService extends RemoteViewsService {
    public static final String TAG = "ListWidgetService";

    private ListRemoteViewsFactory listRemoteViewsFactory;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "Service created");
        if(listRemoteViewsFactory == null)
            listRemoteViewsFactory = new ListRemoteViewsFactory(this.getApplicationContext(), intent);

        String widgetProviderName = intent.getStringExtra(TimetableWidgetProvider.CLASS_NAME);

        Boolean coursesForTomorrow = intent.getBooleanExtra("tomorrow", false);

        //Test if the async task hasn't just finished.If true no need to execute it again.
        //But if list view adapters are null, async task was finished long time ago, so there it's
        //isn't anything stored on RAM
        //if(ViewPagerAdapter.listViewAdapters == null){
            //No data from previous run of app
            Log.d(TAG, "NO LOADING YET");
            //DataLoader.widgetForTomorrow = coursesForTomorrow;
            new DataLoader(null, null, 0, listRemoteViewsFactory, getApplicationContext(),
                                    widgetProviderName).execute();
        //}
        /*else{
            final Calendar calendar = Calendar.getInstance();
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            final ComponentName componentName = new ComponentName(getApplicationContext(), widgetProviderName);
            final int[] widgetsId = appWidgetManager.getAppWidgetIds(componentName);
            if(coursesForTomorrow){
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek != 7)
                    listRemoteViewsFactory.setValues(
                                    ViewPagerAdapter.listViewAdapters[calendar.get(Calendar.DAY_OF_WEEK)].getValues());
                else
                    listRemoteViewsFactory.setValues(
                            ViewPagerAdapter.listViewAdapters[0].getValues());
            }
            else
                listRemoteViewsFactory.setValues(
                        ViewPagerAdapter.listViewAdapters[calendar.get(Calendar.DAY_OF_WEEK) - 1].getValues());
            Log.d(TAG, ViewPagerAdapter.listViewAdapters[calendar.get(Calendar.DAY_OF_WEEK)-1].getValues().toString());

            appWidgetManager.notifyAppWidgetViewDataChanged(widgetsId, R.id.widget_list);
        }*/

        return listRemoteViewsFactory;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyed");
    }
}

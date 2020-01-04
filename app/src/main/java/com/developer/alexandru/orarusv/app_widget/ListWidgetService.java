package com.developer.alexandru.orarusv.app_widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViewsService;
/**
 * Created by Alexandru on 5/30/14. An interface between the adapter of the widget's list view and
 * the data from the sqlite database
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
    if (listRemoteViewsFactory == null)
      listRemoteViewsFactory = new ListRemoteViewsFactory(this.getApplicationContext(), intent);

    String widgetProviderName = intent.getStringExtra(TimetableWidgetProvider.CLASS_NAME);

    Boolean coursesForTomorrow = intent.getBooleanExtra("tomorrow", false);

    Log.d(TAG, "NO LOADING YET");

    return listRemoteViewsFactory;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "destroyed");
  }
}

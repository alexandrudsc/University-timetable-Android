package com.developer.alexandru.aplicatie_studenti.app_widget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.SearchableFragment;
import com.developer.alexandru.aplicatie_studenti.Utils;

import java.util.Calendar;

/**
 * Created by Alexandru on 5/26/14.
 * The provider for all the widgets created by the user.
 *
 * This class is some sort of broadcast receiver, receiving an intent when a widget is created
 * Also, it can receive an intent with CALENDAR_UPDATE_DAY action (at midnight) with the
 * explicit action to update all the widgets.The intent firing is scheduled with an alarm.
 *
 * The updating is made with a remote views service that creates an instance of RemoteViewsFactory
 * (an adapter for the list view)
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TimetableWidgetProvider extends AppWidgetProvider {
    // debug
    public static String CLASS_NAME = "class_name";
    private final String TAG = "TimetableWidgetProvider";
    private final boolean D = true;

    // Update code for refresh (the day has passed)
    public static final String CALENDAR__DAILY_UPDATE = "com.alexandru.developer.aplicatie_studenti.action.UPDATE_CALENDAR";
    // Update because the user has pressed the button on th calendar
    public static final String CALENDAR_CHANGE_DAY = "com.alexandru.developer.aplicatie_studenti.action.CHANGE_DAY";

    private final int START_APP_REQUEST_CODE = 1;
    private final int UPDATE_WIDGET_REQUEST_CODE = 2;
    public static final long DAY_TO_MILLIS = 24 * 3600 * 1000;

    private boolean tomorrow;
    private boolean buttonClicked;

    private RemoteViews remoteViews;

    @Override
    public void onEnabled(Context context) {
        if (D) Log.d(TAG, "Widget enabled");
        super.onEnabled(context);
        scheduleUpdate(context.getApplicationContext());
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntentForUpdate(context));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        // Two situations are considered: there is a daily update (the day has passed)
        // or an update was requested by clicking the button (if that, allow the updating by setting another action to the intent)
        buttonClicked = intent.getAction().equals(CALENDAR_CHANGE_DAY);
        if (buttonClicked)
            intent.setAction(CALENDAR__DAILY_UPDATE);

        if(intent.getAction().equals(CALENDAR__DAILY_UPDATE) ){
            if (D) Log.d(TAG, "onReceive()");
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final ComponentName appWidget = new ComponentName(context, getClass().getName());

            final int widgetsId[] = appWidgetManager.getAppWidgetIds(appWidget);
            tomorrow = intent.getBooleanExtra("tomorrow", false);


            //Update day, week, month in calendar title
            updateDisplayedData(context, widgetsId);

            //Notify the remote adapter that data has changed.Actually call onDataSetChanged in RemoteViewsFactory
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetsId, R.id.widget_list);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //updateDisplayedData(context, appWidgetIds);
        if(remoteViews == null)
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        for(int i = 0; i < appWidgetIds.length; i++){
            if (D) Log.d(TAG, "update" + appWidgetIds[i]);

            int mWidgetId = appWidgetIds[i];

            remoteViews.setOnClickPendingIntent(R.id.calendar_title, getPendingIntentForStartApp(context) );
            //remoteViews.setOnClickPendingIntent(R.id.change_day, getClickPendingIntent(context));
            remoteViews.setPendingIntentTemplate(R.id.widget_list, getPendingIntentForDetails(context) );

            //Intent - starting the service that provides content to the list view
            //within the widget
            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            intent.putExtra(CLASS_NAME, getClass().getName());
            intent.putExtra("tomorrow", tomorrow);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                remoteViews.setRemoteAdapter(mWidgetId, R.id.widget_list, intent);
            else
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    remoteViews.setRemoteAdapter(R.id.widget_list, intent);
            remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

        }

    }

    private String dayToString(Context context, int day){
        String[] days = context.getResources().getStringArray(R.array.days_of_week);
        return days[day-1];
    }

    private String monthToString(Context context, int month){
        String[] months = context.getResources().getStringArray(R.array.months);
        return months[month];
    }

    private void scheduleUpdate(Context context){
        if (D) Log.d(TAG, "Widget created and alarm set!");

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();

        final PendingIntent pendingIntentForUpdate = getPendingIntentForUpdate(context);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60 * 1000, pendingIntentForUpdate);
    }

    private void updateDisplayedData(Context context, final int[] appWidgetIds){
        /*Update the text views in the calendar widget title
                weekOfSemester current week
                weekWord "saptamana" translated
                tomorrow boolean if tomorrow is requested
                buttonClicked boolean if button in widget was clicked
        */
        Utils.setCurrentWeek(context);
        int weekOfSemester = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE).
                getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
        final String weekWord = context.getResources().getString(R.string.widget_week);
        final Calendar calendar = Calendar.getInstance();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (remoteViews == null) {
            onUpdate(context, appWidgetManager, appWidgetIds);
        }

        if (D) Log.d(TAG, "usual update: " + (calendar.get(Calendar.MINUTE) % 7 + 1));
        //Usual update because the day is finished
        for(int i = 0; i < appWidgetIds.length; i++) {
            if (calendar.get(Calendar.MINUTE) % 7 + 1 == 6)
                remoteViews.setTextViewText(R.id.day_of_week, "Now");
            else
                remoteViews.setTextViewText(R.id.day_of_week, "" + dayToString(context, calendar.get(Calendar.MINUTE) % 7 + 1));

            remoteViews.setTextViewText(R.id.day_of_month, monthToString(context, calendar.get(Calendar.MONTH)) + " " +
                    calendar.get(Calendar.DAY_OF_MONTH));
            remoteViews.setTextViewText(R.id.current_week_widget, weekWord + " " + String.valueOf(weekOfSemester));

            //Current day is chosen, so the drawable must indicate forward
            //remoteViews.setImageViewResource(R.id.change_day, R.drawable.ic_action_next_item);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        /*if (tomorrow && buttonClicked){
            //The day after the current one is requested
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int monthOfYear = calendar.get(Calendar.MONTH);

            if (D) Log.d(TAG, "forced update: " + dayOfWeek);

            //If current day is saturday ( =7, end of week), the next day will be sunday and the week will be updated
            //if the the current week is less than WEEKS_IN_SEMESTER
            if(dayOfWeek != 7){
                remoteViews.setTextViewText(R.id.day_of_week, dayToString(context, dayOfWeek + 1 ));
                remoteViews.setTextViewText(R.id.current_week_widget, weekWord + " " +
                        String.valueOf(weekOfSemester));
            }
            else{
                remoteViews.setTextViewText(R.id.day_of_week, dayToString(context, 1));
                if(weekOfSemester < MainActivity.WEEKS_IN_SEMESTER)
                    remoteViews.setTextViewText(R.id.current_week_widget, weekWord + " " +
                            String.valueOf(weekOfSemester + 1));
                else
                    remoteViews.setTextViewText(R.id.current_week_widget,weekWord + " " +
                            String.valueOf(weekOfSemester));
            }

            //If the month is 12 (end of year) start again with month 1
            if(monthOfYear != 12)
                remoteViews.setTextViewText(R.id.day_of_month, monthToString(context, calendar.get(Calendar.MONTH))+ " "+
                        (calendar.get(Calendar.DAY_OF_MONTH) + 1));
            else
                remoteViews.setTextViewText(R.id.day_of_month, monthToString(context, calendar.get(Calendar.MONTH))+ " "+
                        1);
            //Next day was chosen, so change the imageButton drawable ( now indicates back)
            //remoteViews.setImageViewResource(R.id.change_day, R.drawable.ic_action_previous_item);

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }*/
    }


    private PendingIntent getPendingIntentForUpdate(Context context){
        Intent updateIntent = new Intent(context, this.getClass());
        updateIntent.setAction(CALENDAR__DAILY_UPDATE);

        return PendingIntent.getBroadcast(context, UPDATE_WIDGET_REQUEST_CODE,
                updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getClickPendingIntent(Context context){
        Intent tomorrow = new Intent(context, this.getClass());
        tomorrow.setAction(CALENDAR_CHANGE_DAY);
        if(this.tomorrow)
            //Already displaying tomorrow's courses
            tomorrow.putExtra("tomorrow", false);
        else {
            this.tomorrow = true;
            tomorrow.putExtra("tomorrow", true);
        }
        return PendingIntent.getBroadcast(context, UPDATE_WIDGET_REQUEST_CODE,
                tomorrow, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendingIntentForDetails(Context context){
        Intent viewDetails = new Intent(context, MainActivity.class);
        viewDetails.setAction(SearchableFragment.actionViewDetails);
        return PendingIntent.getActivity(context, ListRemoteViewsFactory.VIEW_DETAILS_CODE,
                    viewDetails, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private PendingIntent getPendingIntentForStartApp(Context context){

        final Intent startApplication = new Intent(context, MainActivity.class);
        startApplication.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(context, START_APP_REQUEST_CODE,
                startApplication, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
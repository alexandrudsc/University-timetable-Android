package com.developer.alexandru.orarusv.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.app_widget.TimetableWidgetProvider;
import com.developer.alexandru.orarusv.download.DownloadActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Alexandru on 2/9/2015.
 * A service downloading the data necessary to a specific timetable.
 * It puts the data in a temporary table of the database and if everything it's ok, it replaces the old data with the fresh one.
 */
public class TimetableDownloaderService extends IntentService {

    //Debug
    private static final boolean D = true;
    public static final String TAG = "TIMETABLE_DOWNLOADER";

    public static final String ACTION_DOWNLOAD_FINISHED = "download_finished";

    private NotificationManager notificationManager;
    private static final int DOWNLOAD_NOTIF_CODE = 1;

    // data from starting intent
    public static final String EXTRA_URL = "timetable_url";
    public static final String EXTRA_TIMETABLE_TYPE = "timetable_type";
    public static final String EXTRA_TIMETABLE_ID = "timetable_id";
    public static final String EXTRA_TIMETABLE_NAME = "timetable_name";

    private DBAdapter dbAdapter;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name string used to name the worker thread, important only for debugging.
     */
    public TimetableDownloaderService(String name) {
        super(name);
    }

    public TimetableDownloaderService() {
        super(TAG);
        if(D) Log.d(TAG, "created");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "started");
        showNotification();

        dbAdapter = new DBAdapter(this);
        dbAdapter.open();

        String urlCourses = intent.getStringExtra(EXTRA_URL);
        String timetableName = intent.getStringExtra(EXTRA_TIMETABLE_NAME);
        int timetableID = intent.getIntExtra(EXTRA_TIMETABLE_ID, -1);
        int timetableType = intent.getIntExtra(EXTRA_TIMETABLE_TYPE, Timetable.Type.Student.ordinal());

        try {
            // Get the structure of the current semester and save it
            URL timeStructureURL = new URL(CsvAPI.TIME_URL);
            HttpURLConnection conn = (HttpURLConnection) timeStructureURL.openConnection();
            InputStreamReader is = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // A line in the buffer
            String line;
            String[] strs;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, line);
                strs = line.split("<br />");
                if (strs.length > 1)
                    saveTimeStructure(strs);
            }

            br.close();
            is.close();
            conn.disconnect();

            if (urlCourses == null) {
                sendNotificationDownloaded();
                throw new MalformedURLException("No courses URL.");
            }
            URL url = new URL(urlCourses);
            conn = (HttpURLConnection) url.openConnection();
            is = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(is);

            CoursesParser parser = new CoursesParser(br);
            parser.parse();
            conn.disconnect();
            if (parser.wasSuccessful()) {
                Timetable timetable = Timetable.Creator.create(new String[]{
                        String.valueOf(timetableType),
                        String.valueOf(timetableID),
                        timetableName});
                dbAdapter.deleteTimetableAndCourses(timetable);
                dbAdapter.insertTimetable(timetable);
                Utils.setCurrentTimetable(timetable, getApplicationContext());

                final ArrayList<Course> courses = parser.getCourses();
                for (Course c : courses) {
                    dbAdapter.insertCourse(c, timetableID);
                }
                sendNotificationDownloaded();
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        cancelNotification();
        dbAdapter.close();
    }

    private void sendNotificationDownloaded() {
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(new Intent(DownloadActivity.TIMETABLE_DOWNLOADED));
    }

    private void showNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.refresh_anim)
                .setContentTitle("Descărcare")
                .setContentText("Orar și date adiționale")
                .setProgress(0, 0, true);
        Notification n = builder.build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(DOWNLOAD_NOTIF_CODE, n);
    }

    private void cancelNotification(){
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(DOWNLOAD_NOTIF_CODE);
    }

    private void saveTimeStructure(String[] time){
        boolean afterFirstSemester = false;
        Semester s1 = new Semester(), s2 = new Semester();
        String[] dates;
        for ( int i = 0; i < time.length; i++) {
            dates = time[i].split(";");
            if (dates.length > 1) {
                if ( i == time.length - 2)
                    s2.end = Date.valueOf(dates[0]).getTime() - TimetableWidgetProvider.DAY_TO_MILLIS;
                else {
                    if (dates.length > 2 && dates[2] != null && dates[2].toLowerCase().contains("deschidere")) {
                        s1.start = Date.valueOf(dates[1]).getTime();
                    }
                    if (dates.length > 2 && dates[2] != null && dates[2].toLowerCase().contains("sesiune")) {
                        if (!afterFirstSemester)
                            s1.end = Date.valueOf(dates[0]).getTime() - TimetableWidgetProvider.DAY_TO_MILLIS;
                        else {
                            if (s2.start == 0)
                                s2.start = Date.valueOf(dates[1]).getTime() + TimetableWidgetProvider.DAY_TO_MILLIS;
                            s2.end = Date.valueOf(dates[1]).getTime() + TimetableWidgetProvider.DAY_TO_MILLIS;
                        }
                        afterFirstSemester = true;
                    }
                    if (dates.length > 2 && dates[2] != null && dates[2].toLowerCase().contains("vacanta"))
                        if (afterFirstSemester) {
                            s2.startHoliday = Date.valueOf(dates[0]).getTime();
                            s2.endHoliday = Date.valueOf(dates[1]).getTime();
                        } else {
                            s1.startHoliday = Date.valueOf(dates[0]).getTime();
                            s1.endHoliday = Date.valueOf(dates[1]).getTime();
                        }
                    switch (i) {
                        case 0:
                            if (dates.length > 2 && dates[2] != null && dates[2].toLowerCase().contains("deschidere")) {
                                s1.start = Date.valueOf(dates[1]).getTime();
                            }
                            break;
                        default:
                            if (dates.length > 2 && dates[2] != null && dates[2].toLowerCase().contains("deschidere")) {
                                s1.start = Date.valueOf(dates[1]).getTime();
                            }
                    }
                }
            }

        }
        if (s1.contains(System.currentTimeMillis())) {
            Log.d(TAG, s1.toString());
            Semester.save(getApplicationContext(), s1);
        }
        else {
            Log.d(TAG, s2.toString());
            Semester.save(getApplicationContext(), s2);
        }
    }

    private class CoursesParser extends CSVParser {

        private ArrayList<Course> courses;

        public CoursesParser(BufferedReader br) {
            super(br);
            courses = new ArrayList<>();
        }

        @Override
        public boolean handleData(String[] data) {
            Course c = CourseBuilder.build(data);
            if (c == null)
                return false;

            Log.d(TAG, c.toString());
//            dbAdapter.insertTmpCourse(c);
            courses.add(c);
            return true;
        }

        public ArrayList<Course> getCourses() {
            return this.courses;
        }
    }

    private static class Semester{
        long start, end;
        long startHoliday, endHoliday;

        @Override
        public String toString() {
            Date d = new Date(start);

            String str =  "Start: " + d.toString();

            d = new Date(end);
            str += ", end: " + d.toString();

            d = new Date(startHoliday);
            str += " with holiday " + d.toString();

            d = new Date(endHoliday);
            str += "-" + d.toString();

            return str;
        }

        public boolean contains(long dateMilis) {
            return Utils.isDateBetween(start, end, dateMilis);
        }

        public static boolean save(Context context, Semester semester){
            SharedPreferences preferences = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putLong(MainActivity.START_DATE, semester.start);
            editor.putLong(MainActivity.END_DATE, semester.end);
            int numberOfHolidays = 1;
            editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);
            for (int i = 0; i < numberOfHolidays; i++)
                editor.putString(MainActivity.HOLIDAY + "_" + i, semester.startHoliday + "-" + semester.endHoliday);
            editor.commit();
            return true;
        }
    }

}

package com.developer.alexandru.aplicatie_studenti.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.Utils;
import com.developer.alexandru.aplicatie_studenti.app_widget.TimetableWidgetProvider;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;

/**
 * Created by Alexandru on 2/9/2015.
 * A service downloading the data necessary to a specific timetable.
 * It puts the data in a temporary table of the database and if everything it's ok, it replaces the old data with the fresh one.
 */
public class TimetableDownloaderService extends IntentService {

    //Debug
    private static final boolean D = true;
    public static final String TAG = "timetable_downloader";

    public static final String EXTRA_URL = "timetable_url";
    public static final String TIME_URL = "http://www.usv.ro/orar/vizualizare/data/zoneinterzise.php";
    public static final String PROFS_URL = "http://www.usv.ro/orar/vizualizare/data/cadre.php";

    public static final String ACTION_DOWNLOAD_FINISHED = "download_finished";

    // Downloaded CSV data: elements indexes on a line
    public static final int PROF_ID = 3;
    public static final int PROF_LAST_NAME = 4;
    public static final int PROF_FIRST_NAME = 5;
    public static final int RANK = 6;
    public static final int HAS_PHD= 7;
    public static final int OTHER_TITLES = 8;
    public static final int BUILDING = 10;
    public static final int ROOM = 11;
    public static final int ROOM_SHORT_NAME = 12;
    public static final int COURSE_FULL_NAME = 13;
    public static final int COURSE_NAME = 14;
    public static final int DAY = 15;
    public static final int START = 16;
    public static final int STOP = 17;
    public static final int PARITY = 18;
    public static final int INFO = 19;
    public static final int TYPE = 21;

    private NotificationManager notificationManager;
    private static final int DOWNLOAD_NOTIF_CODE = 1;

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

        String address = intent.getStringExtra(EXTRA_URL);
        String adr;

        adr = address;

        try {

            // Get the structure of the current semester and save it
            URL timeStructureURL = new URL(TIME_URL);
            HttpURLConnection conn = (HttpURLConnection) timeStructureURL.openConnection();
            InputStreamReader is = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(is);

            // A line in the buffer
            String line;
            String[] strs;
            while ((line = br.readLine()) != null) {
                strs = line.split("<br />");
                if (strs.length > 1)
                    saveTimeStructure(strs);
            }

            br.close();
            is.close();
            conn.disconnect();

                /*URL profsURL = new URL(PROFS_URL);
                conn = (HttpURLConnection) profsURL.openConnection();
                is = new InputStreamReader(conn.getInputStream());
                br = new BufferedReader(is);

                String filePath = getApplicationContext().getFilesDir().getPath() + "/profs.txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                while ((line = br.readLine()) != null)
                writer.write(line + "\n");
                writer.close();

                br.close();
                is.close();
                conn.disconnect();*/

            URL url = new URL(adr);
            conn = (HttpURLConnection) url.openConnection();
            is = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(is);

            // Create the temporary table to keep the new courses until all network operations are done.
            // This is used in case of connectivity issues
            dbAdapter.createTMPCoursesTable();
            CoursesParser parser = new CoursesParser(br);
            parser.parse();
            conn.disconnect();

            dbAdapter.replaceOldCourses();
//            ViewPagerAdapter.listsOfCourses = null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            //
        } catch (IOException e) {
            e.printStackTrace();
        }

        cancelNotification();
        dbAdapter.close();
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
                    if (dates[2].toLowerCase().contains("sesiune")) {
                        if (!afterFirstSemester)
                            s1.end = Date.valueOf(dates[0]).getTime() - TimetableWidgetProvider.DAY_TO_MILLIS;
                        else {
                            if (s2.start == 0)
                                s2.start = Date.valueOf(dates[1]).getTime() + TimetableWidgetProvider.DAY_TO_MILLIS;
                            s2.end = Date.valueOf(dates[1]).getTime() + TimetableWidgetProvider.DAY_TO_MILLIS;
                        }
                        afterFirstSemester = true;
                    }
                    if (dates[2].toLowerCase().contains("vacanta"))
                        if (afterFirstSemester) {
                            s2.startHoliday = Date.valueOf(dates[0]).getTime();
                            s2.endHoliday = Date.valueOf(dates[1]).getTime();
                        } else {
                            s1.startHoliday = Date.valueOf(dates[0]).getTime();
                            s1.endHoliday = Date.valueOf(dates[1]).getTime();
                        }
                    switch (i) {
                        case 0:
                            s1.start = Date.valueOf(dates[1]).getTime();
                            break;
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

        public CoursesParser(BufferedReader br) {
            super(br);
        }

        @Override
        public boolean handleData(String[] data) {
            final int startTime = Integer.valueOf(data[START]) / 60;
            final int endTime = Integer.valueOf(data[STOP]) / 60 + startTime;

            Course c = new Course(data[COURSE_NAME], data[COURSE_FULL_NAME], data[TYPE],
                    data[ROOM_SHORT_NAME], data[BUILDING] + " " + data[ROOM],
                    startTime + ":00 - " + endTime + ":00",
                    data[RANK] + " " + data[HAS_PHD] + data[OTHER_TITLES] + " " +
                    data[PROF_FIRST_NAME] + " " + data[PROF_LAST_NAME], data[PROF_ID],
                    data[PARITY], data[INFO]);

            c.startTime = startTime;
            c.endTime = endTime;

            Log.d(TAG, c.toString());
            dbAdapter.insertTmpCourse(c, data[DAY]);
            return true;
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

        public boolean contains(long dateMilis){
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

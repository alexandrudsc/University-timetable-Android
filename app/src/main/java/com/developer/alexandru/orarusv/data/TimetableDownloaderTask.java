package com.developer.alexandru.orarusv.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.developer.alexandru.orarusv.SettingsActivity;
import com.developer.alexandru.orarusv.MainActivity;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.app_widget.TimetableWidgetProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;

/**
 * Created by Alexandru on 7/28/14.
 * Downloads data from the specified URL.The usual case is querying an online database.
 * The work is done off the activity_main thread with an AsyncTask
 * NOT ACTIVE NOW!!
 */
@Deprecated
public class TimetableDownloaderTask extends AsyncTask <String, Void, Void> {

    //Debug
    public static final String TAG = "DownloaderTask";
    public static final boolean D = true;

    private Context context;
    private SettingsActivity activity;
    private DBAdapter dbAdapter;

    private boolean success = true;

    public static final String TIME_URL = "http://www.usv.ro/orar/vizualizare/data/zoneinterzise.php";

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

    public TimetableDownloaderTask(SettingsActivity activity) {
        this.activity = activity;
        context = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.showDialog();
    }

    @Override
    protected Void doInBackground(String... args) {

        Log.d(TAG, "started");

        dbAdapter = new DBAdapter(context);
        dbAdapter.open();
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

            URL timetableUrl = new URL(args[0]);
            conn = (HttpURLConnection) timetableUrl.openConnection();
            is = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(is);

            // Create the temporary table to keep the new courses until all network operations are done.
            // This is used in case of connectivity issues
            dbAdapter.createTMPCoursesTable();
            CoursesParser parser = new CoursesParser(br);
            parser.parse();
            conn.disconnect();
            if (parser.wasSuccessful()) {
                dbAdapter.replaceOldCourses();
//                ViewPagerAdapter.listsOfCourses = null;
            } else {
                success = false;
                dbAdapter.deleteTMPCourses();
            }
        }catch (IOException e){
            e.printStackTrace();
            success = false;
            dbAdapter.deleteTMPCourses();
        }

        dbAdapter.close();
        /*SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences timeOrganiser = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,
                Context.MODE_PRIVATE);
        final int currentWeek = timeOrganiser.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
        try {
            URL url = urls[0];
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //ViewPagerAdapter.context = context;
            //Create the local database and open a connection
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            dbAdapter.deleteCourses();
            dbAdapter.create();
            StringBuilder stringBuilder = new StringBuilder();
            JSONObject jsonObject;
            String line;

            JSONArray day = null;
            ArrayList<Course> courses;
            //Build the string from the JSON response
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            jsonObject = new JSONObject(stringBuilder.toString());
            jsonArray = jsonObject.getJSONArray(ViewPagerAdapter.NAME_OF_DAYS);
            Log.d("DOWNLOADER", stringBuilder.toString());
            //Save start and end date.Save holidays' dates.

            if (timeOrganiser.getLong(MainActivity.START_DATE, 0) == 0) {
                SharedPreferences.Editor editor = timeOrganiser.edit();
                JSONObject jsonTimeOrganiser = jsonObject.getJSONObject(ViewPagerAdapter.NAME_OF_SEMESTER_ORG);

                //Update the start and the finish date
                editor.putLong(MainActivity.START_DATE,
                        Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_START_DATE).toString()));
                editor.putLong(MainActivity.END_DATE,
                        Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_END_DATE).toString()));

                //Update the holidays
                JSONArray jsonHolidays = jsonTimeOrganiser.getJSONArray(ViewPagerAdapter.NAME_OF_HOLIDAYS);
                JSONObject holiday;
                int numberOfHolidays = jsonHolidays.length();
                editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);
                for (int i = 0; i < numberOfHolidays; i++) {
                    holiday = jsonHolidays.getJSONObject(i);
                    editor.putString(MainActivity.HOLIDAY + "_" + i,
                            holiday.get(ViewPagerAdapter.NAME_OF_START_DATE).toString() + "-"
                                    + holiday.get(ViewPagerAdapter.NAME_OF_END_DATE).toString());
                }
                editor.commit();

            }

            ViewPagerAdapter.listsOfCourses = new ArrayList[ViewPagerAdapter.NUM_DAYS];

            for (int i = 0; i < ViewPagerAdapter.NUM_DAYS; i++) {
                try {
                    day = jsonArray.getJSONObject(i).getJSONArray(ViewPagerAdapter.days[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (day != null) {
                    //Parse the day object from the JSON response into an array
                    //courses = ViewPagerAdapter.getCoursesFromJSONArray(day);
                    courses = null;
                    //Add the courses into the database and also select the ones for the current wwek
                    ArrayList<Course> coursesForToday = new ArrayList<Course>();
                    for (int j = 0; j < courses.size(); j++) {
                        Course c = courses.get(j);
                        //Add the course to the database
                        dbAdapter.insertCourse(c, ViewPagerAdapter.days[i]);
                        //If is in the current week select it for immediate displaying
                        if (DataLoader.isCourseInWeek(currentWeek, c))
                            coursesForToday.add(c);
                    }
                    //Add the data for the current wee to a static array
                    ViewPagerAdapter.listsOfCourses[i] = coursesForToday;
                    if (D) Log.d(TAG, day.toString());
                } else if (D) Log.d(TAG, ViewPagerAdapter.days[i] + " null");
            }
            success = true;
            dbAdapter.close();

            preferences.edit().putBoolean(MainActivity.FIRST_RUN, false).commit();

            // The list view adapter for every day of current week uses the current week value.
            //Must be set now.Also the remote views adapter uses it.
            Utils.setCurrentWeek(context);

            connection.disconnect();
        } catch (IOException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (success)
            activity.setResult(Activity.RESULT_OK);
        else
            activity.setResult(Activity.RESULT_CANCELED);
        activity.cancelDialog();
    }

    private void saveTimeStructure(String[] time) {
        boolean afterFirstSemester = false;
        Semester s1 = new Semester(), s2 = new Semester();
        String[] dates;
        for (int i = 0; i < time.length; i++) {
            if (isCancelled())
                break;
            dates = time[i].split(";");
            if (dates.length > 1) {
                if (i == time.length - 2)
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
                            s2.holidayName = dates[2];
                        } else {
                            s1.startHoliday = Date.valueOf(dates[0]).getTime();
                            s1.endHoliday = Date.valueOf(dates[1]).getTime();
                            s1.holidayName = dates[2];
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
            Semester.save(context, s1);
        } else {
            Log.d(TAG, s2.toString());
            Semester.save(context, s2);
        }
    }

    private class CoursesParser extends CSVParser {

        public CoursesParser(BufferedReader br) {
            super(br);
        }

        @Override
        public boolean handleData(String[] data) {
            if (isCancelled())
                return false;
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

    private static class Semester {
        long start, end;
        long startHoliday, endHoliday;
        String holidayName;

        @Override
        public String toString() {
            Date d = new Date(start);

            String str = "Start: " + d.toString();

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

        public static boolean save(Context context, Semester semester) {
            SharedPreferences preferences = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putLong(MainActivity.START_DATE, semester.start);
            editor.putLong(MainActivity.END_DATE, semester.end);
            int numberOfHolidays = 1;
            editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);
            for (int i = 0; i < numberOfHolidays; i++)
                editor.putString(MainActivity.HOLIDAY + "_" + i, semester.startHoliday + "-" + semester.endHoliday + "-" + semester.holidayName);
            editor.commit();
            return true;
        }
    }
}
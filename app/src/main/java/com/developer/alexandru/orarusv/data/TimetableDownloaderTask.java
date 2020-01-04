package com.developer.alexandru.orarusv.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.developer.alexandru.orarusv.SettingsActivity;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.app_widget.TimetableWidgetProvider;
import com.developer.alexandru.orarusv.main.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;

/**
 * Created by Alexandru on 7/28/14. Downloads data from the specified URL.The usual case is querying
 * an online database. The work is done off the activity_main thread with an AsyncTask NOT ACTIVE
 * NOW!!
 */
@Deprecated
public class TimetableDownloaderTask extends AsyncTask<String, Void, Void> {

  // Debug
  public static final String TAG = "DownloaderTask";
  public static final boolean D = true;

  private Context context;
  private SettingsActivity activity;
  private DBAdapter dbAdapter;

  private boolean success = true;

  public static final String TIME_URL = "http://www.usv.ro/orar/vizualizare/data/zoneinterzise.php";

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
        if (strs.length > 1) saveTimeStructure(strs);
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
      //            dbAdapter.createTMPCoursesTable();
      CoursesParser parser = new CoursesParser(br);
      parser.parse();
      conn.disconnect();
      if (parser.wasSuccessful()) {
        //                dbAdapter.replaceOldCourses();
        //                TimetableViewPagerAdapter.listsOfCourses = null;
      } else {
        success = false;
        //                dbAdapter.deleteTMPCourses();
      }
    } catch (IOException e) {
      e.printStackTrace();
      success = false;
      //            dbAdapter.deleteTMPCourses();
    }

    dbAdapter.close();
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if (success) activity.setResult(Activity.RESULT_OK);
    else activity.setResult(Activity.RESULT_CANCELED);
    activity.cancelDialog();
  }

  private void saveTimeStructure(String[] time) {
    boolean afterFirstSemester = false;
    Semester s1 = new Semester(), s2 = new Semester();
    String[] dates;
    for (int i = 0; i < time.length; i++) {
      if (isCancelled()) break;
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
      if (isCancelled()) return false;
      Course c = CourseBuilder.build(data);
      if (c == null) return false;

      Log.d(TAG, c.toString());
      //            dbAdapter.insertTmpCourse(c);
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
      SharedPreferences preferences =
          context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = preferences.edit();

      editor.putLong(MainActivity.START_DATE, semester.start);
      editor.putLong(MainActivity.END_DATE, semester.end);
      int numberOfHolidays = 1;
      editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);
      for (int i = 0; i < numberOfHolidays; i++)
        editor.putString(
            MainActivity.HOLIDAY + "_" + i,
            semester.startHoliday + "-" + semester.endHoliday + "-" + semester.holidayName);
      editor.commit();
      return true;
    }
  }
}

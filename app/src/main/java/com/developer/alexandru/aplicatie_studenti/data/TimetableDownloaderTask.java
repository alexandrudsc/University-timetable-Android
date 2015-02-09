package com.developer.alexandru.aplicatie_studenti.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.developer.alexandru.aplicatie_studenti.SettingsActivity;
import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alexandru on 7/28/14.
 * Downloads data from the specified URL.The usual case is querying an online database.
 * The work is done off the main thread with an AsyncTask
 * NOT ACTIVE NOW!!
 */
public class TimetableDownloaderTask extends AsyncTask <URL, Void, Void> {

    //Debug
    public static final String TAG = "DownloaderTask";
    public static final boolean D = true;

    private Context context;

    private JSONArray jsonArray;

    private boolean success = false;

    private SettingsActivity activity;
    public TimetableDownloaderTask(SettingsActivity activity) {
        this.activity = activity;
        context = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.showDialog();
    }

    @Override
    protected Void doInBackground(URL... urls) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences timeOrganiser = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,
                Context.MODE_PRIVATE);
        final int currentWeek = timeOrganiser.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
        try{
            URL url = urls[0];
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            ViewPagerAdapter.context = context;
            //Create the local database and open a connection
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            dbAdapter.deleteCourses();
            dbAdapter.create();
            StringBuilder stringBuilder = new StringBuilder();
            JSONObject jsonObject;
            String line;

            JSONArray day = null;
            ArrayList<Course >courses ;
            //Build the string from the JSON response
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            inputStream.close();
            jsonObject = new JSONObject(stringBuilder.toString());
            jsonArray = jsonObject.getJSONArray(ViewPagerAdapter.NAME_OF_DAYS);
            Log.d("DOWNLOADER", stringBuilder.toString());
            //Save start and end date.Save holidays' dates.

            if(timeOrganiser.getLong(MainActivity.START_DATE, 0) == 0){
                SharedPreferences.Editor editor = timeOrganiser.edit();
                JSONObject jsonTimeOrganiser = jsonObject.getJSONObject(ViewPagerAdapter.NAME_OF_SEMESTER_ORG);

                //Update the start and the finish date
                editor.putLong(MainActivity.START_DATE,
                        Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_START_DATE).toString()) );
                editor.putLong(MainActivity.END_DATE,
                        Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_END_DATE).toString()) );

                //Update the holidays
                JSONArray jsonHolidays = jsonTimeOrganiser.getJSONArray(ViewPagerAdapter.NAME_OF_HOLIDAYS);
                JSONObject holiday;
                int numberOfHolidays = jsonHolidays.length();
                editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);
                for(int i = 0; i<numberOfHolidays; i++){
                    holiday = jsonHolidays.getJSONObject(i);
                    editor.putString(MainActivity.HOLIDAY + "_" + i,
                            holiday.get(ViewPagerAdapter.NAME_OF_START_DATE).toString() + "-"
                                    + holiday.get(ViewPagerAdapter.NAME_OF_END_DATE).toString());
                }
                editor.commit();

            }

            ViewPagerAdapter.listsOfCourses = new ArrayList[ViewPagerAdapter.NUM_DAYS];

            for(int i = 0; i < ViewPagerAdapter.NUM_DAYS; i++){
                    try {
                        day = jsonArray.getJSONObject(i).getJSONArray(ViewPagerAdapter.days[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(day != null){
                        //Parse the day object from the JSON response into an array
                        courses = ViewPagerAdapter.getCoursesFromJSONArray(day);

                        //Add the courses into the database and also select the ones for the current wwek
                        ArrayList<Course> coursesForToday = new ArrayList<Course>();
                        for(int j = 0; j < courses.size(); j++){
                            Course c = courses.get(j);
                            //Add the course to the database
                            dbAdapter.insertCourse(c, ViewPagerAdapter.days[i]);
                            //If is in the current week select it for immediate displaying
                            if(DataLoader.isCourseInWeek(currentWeek, c))
                                coursesForToday.add(c);
                        }
                        //Add the data for the current wee to a static array
                        ViewPagerAdapter.listsOfCourses[i] = coursesForToday;
                        if(D) Log.d(TAG, day.toString());
                    }else
                        if(D) Log.d(TAG, ViewPagerAdapter.days[i]+ " null");
            }
            success = true;
            dbAdapter.close();

            preferences.edit().putBoolean(MainActivity.FIRST_RUN, false).commit();

            // The list view adapter for every day of current week uses the current week value.
            //Must be set now.Also the remote views adapter uses it.
            MainActivity.setCurrentWeek(context);

            connection.disconnect();
        }catch (IOException e){

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(success)
            activity.setResult(Activity.RESULT_OK);
        else
            activity.setResult(Activity.RESULT_CANCELED);
        activity.cancelDialog();
    }
}
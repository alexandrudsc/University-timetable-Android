package com.developer.alexandru.orarusv;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.developer.alexandru.orarusv.main.MainActivity;

/**
 * Created by Alexandru on 2/10/2015.
 * Class with static methods providing utilities like: calculating the current week of the semester avoiding the holidays,
 * displaying toast messages, checking internet connection.
 */
public class Utils {

    private static String TAG = "UtilsClass";
    private static boolean D = true;

    private static long startTime;

    private static final String NO_INTERNET_ACCESS = "Eroare! Verificați conexiunea la internet.";

    private Utils(){
        // cannot be instantiated or extended
    }

    public static void toastNoInternetAccess(Context context){
        Toast toast = Toast.makeText(context, NO_INTERNET_ACCESS, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static boolean hasInternetAccess(Context c){
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }

    /**
     * Check if date is between lower and higher dates
     * @param lowMilis the lowest date in miliseconds
     * @param highMilis the highest date in miliseconds
     * @param dateMilis the date in miliseconds
     * @return true if @param dateMilis belongs to the interval, false otherwise
     */
    public static boolean isDateBetween (long lowMilis, long highMilis, long dateMilis){
        return dateMilis >= lowMilis && dateMilis <= highMilis;
    }

    /**
     * Calculate the current week of semester and save it into preferences file if needed.<br>
     * Avoid the holidays time. <br>
     * IT IS CALLED FROM MULTIPLE CONTEXTS: MAIN ACTIVITY, SERVICE FOR APPLICATION WIDGET.
     * @param context context from which is called
     */
    public static void setCurrentWeek(Context context) {
        if (context == null)
            return;

        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);

        final long startDateSemester = sharedPreferences.getLong(MainActivity.START_DATE, 0);
        final long endDateSemester = sharedPreferences.getLong(MainActivity.END_DATE, 0);

        final String[] holidayStartEnd = sharedPreferences.getString(MainActivity.HOLIDAY + "_" + 0, "0-0").split("-");
        final long startDateHoliday = Long.valueOf(holidayStartEnd[0]);
        final long endDateHoliday = Long.valueOf(holidayStartEnd[1]);

        final long currentTimeInMillis = System.currentTimeMillis();
        int currentWeek ;
        if (D) Log.d(TAG, "now " + currentTimeInMillis + " end " + endDateSemester);
        if(currentTimeInMillis > endDateSemester)
            currentWeek = MainActivity.WEEKS_IN_SEMESTER;
        else
        if(currentTimeInMillis < startDateSemester)
            currentWeek = 1;
        else{
            if(isHoliday(startDateHoliday, endDateHoliday)){
                if (D) Log.d(TAG, "is holiday");
                // Show the first week after the holiday
                currentWeek = (int)((startDateHoliday - startDateSemester) / MainActivity.WEEK_IN_MILLIS) + 1;
            }
            else {
                final long vacationTime = calculateVacationTime(startDateHoliday, endDateHoliday);
                if (currentTimeInMillis >= endDateHoliday)
                    currentWeek = (int) ((currentTimeInMillis - startDateSemester - vacationTime) / MainActivity.WEEK_IN_MILLIS) + 1;
                else
                    currentWeek = (int) ((currentTimeInMillis - startDateSemester) / MainActivity.WEEK_IN_MILLIS) + 1;
                if ( currentWeek > MainActivity.WEEKS_IN_SEMESTER)
                    currentWeek = MainActivity.WEEKS_IN_SEMESTER;
            }
        }

        if(sharedPreferences.getInt(MainActivity.WEEK_OF_SEMESTER, -1) !=  currentWeek){
            // If the week is changed invalidate the old data set.
//            TimetableViewPagerAdapter.listsOfCourses = null;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MainActivity.WEEK_OF_SEMESTER, currentWeek);
            editor.commit();
        }
        if (D) Log.d(TAG, "current week set");
    }

    private static long calculateVacationTime(long startDate, long endDate){
        long freeTime = 0;
        freeTime += durationOfHoliday(startDate , endDate);
        return freeTime;
    }

    private static long durationOfHoliday(long start, long end){
        long time = end - start;
        return time;
    }

    private static boolean isHoliday(long start, long end){
        return System.currentTimeMillis() >= start && System.currentTimeMillis() <= end;
    }

    public static int getCurrentWeek(Context context){
        return context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE).getInt(
                MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
    }

    public static void startClock(){
        startTime = System.currentTimeMillis();
    }
    public static long stopClock(){
        return System.currentTimeMillis() - startTime;
    }
}

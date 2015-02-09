package com.developer.alexandru.aplicatie_studenti.view_pager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.TimetableFragment;
import com.developer.alexandru.aplicatie_studenti.data.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/13/14.
 * Adapter providing fragments for each day
 * Contains a static array @listsOfCourses which holds for as long as possible the data for the current week
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    //Debug
    private final String TAG = "ViewPagerAdapter";

    public static final int NUM_DAYS = 7;
    private String[] daysFullName;

    public static Context context;

    //Array storing references to the fragments.Used to avoid unnecessary recreation of the fragments within the view pager
    private DayFragment[] fragments;

    private TimetableFragment.OnCourseSelected onCourseSelected;

    /**
     * Static array which holds for as long as possible the data for the current week.
     * Used to avoid unnecessary queries to the database.
     */
    public static ArrayList<Course>[] listsOfCourses ;

    public static final String[] days={"duminica", "luni", "marti", "miercuri", "joi", "vineri", "sambata"};

    //Key names for the values stored in the preferences file
    public static final String NAME_OF_DAYS = "zile";
    public static final String NAME_OF_COURSE = "nume";
    public static final String FULL_NAME = "nume_complet";
    public static final String TYPE_OF_COURSE = "tip";
    public static final String INFO_ABOUT_REPETITION = "info";
    public static final String INFO_ABOUT_LOCATION = "locatie";
    public static final String INFO_ABOUT_TIME = "interval_orar";
    public static final String PROF = "cadru_didactic";
    public static final String COURSES_IN_EVEN_WEEK = "in saptamanile pare";
    public static final String COURSES_IN_ODD_WEEK = "in saptamanile impare";

    public static final String NAME_OF_SEMESTER_ORG = "organizare_semestru";
    public static final String NAME_OF_START_DATE = "start";
    public static final String NAME_OF_END_DATE = "stop";
    public static final String NAME_OF_HOLIDAYS = "vacante";

    /**
     * Constructor for the view pager adapter
     * @param onCourseSelected the interface implemented by the activity hosting the fragment with the the view pager
     * @param childFragManager the fragment manager within the fragment hosting the view pager
     */
    public ViewPagerAdapter(TimetableFragment.OnCourseSelected onCourseSelected, FragmentManager childFragManager){
        super(childFragManager);
        if(fragments == null)
            fragments = new DayFragment[NUM_DAYS];
        this.onCourseSelected = onCourseSelected;
        ViewPagerAdapter.context = onCourseSelected.getContext();
        if(daysFullName == null)
            daysFullName = context.getResources().getStringArray(R.array.days_of_week_full_name);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(daysFullName == null)
            daysFullName = context.getResources().getStringArray(R.array.days_of_week_full_name);
        return daysFullName[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if(daysFullName == null)
            daysFullName = context.getResources().getStringArray(R.array.days_of_week_full_name);

        if(fragments[position] != null){
            Log.d("FRAGMENT ADAPTER", "fragment already created");
            return fragments[position];
        }
        DayFragment dayFragment = DayFragment.createFragment(onCourseSelected, daysFullName[position], position);
        fragments[position] = dayFragment;
        return dayFragment;
    }

    @Override
    public int getCount() {
        return NUM_DAYS;
    }

    /**
     * Parse a jsonArray formatted day into an array list of Courses object
     * @param jsonArray day object from JSON response
     * @return ArrayList<Course> object
     */
    public static ArrayList<Course> getCoursesFromJSONArray(JSONArray jsonArray){
        //The json array contains the courses from an entire day.
        //This method return these.
        JSONObject courseJSON;
        String name, fullName, type, info, location, time, prof;
        int  i = 0;
        int n = jsonArray.length();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,
                                                                            Context.MODE_PRIVATE);
        int weekOfSemester = sharedPreferences.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
        ArrayList<Course> courses = new ArrayList<Course>();

        try {
            while (i<n){
                courseJSON = jsonArray.getJSONObject(i);
                name = courseJSON.getString(NAME_OF_COURSE);
                fullName = courseJSON.getString(FULL_NAME);
                type = courseJSON.getString(TYPE_OF_COURSE);
                info = courseJSON.getString(INFO_ABOUT_REPETITION);
                time = courseJSON.getString(INFO_ABOUT_TIME);
                location = courseJSON.getString(INFO_ABOUT_LOCATION);
                prof = courseJSON.getString(PROF);

                courses.add(new Course(name, fullName, type, location, time, prof, info));

                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        /*if(fragments != null) {
            for (int i = 0; i < fragments.length; i++)
                ((MyListViewAdapter) fragments[i].getListView().getAdapter()).notifyDataSetChanged();
        }*/
    }

    public static boolean isAnyListNull(){
        if(ViewPagerAdapter.listsOfCourses == null)
            return true;
        for(int i = 0;i < NUM_DAYS; i++)
            if(listsOfCourses[i] == null)
                return true;
        return false;
    }
}

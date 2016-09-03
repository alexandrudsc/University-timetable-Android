package com.developer.alexandru.aplicatie_studenti.view_pager;

import android.support.v4.app.*;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.TimetableFragment;
import com.developer.alexandru.aplicatie_studenti.Utils;
import com.developer.alexandru.aplicatie_studenti.data.Course;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/13/14.
 * Adapter providing fragments for each day
 * Contains a static array @listsOfCourses which holds for as long as possible the data for the current week (BAD IDEA)
 * NOT SURE IF I SHOULD USE FragmentStatePagerAdapter or a simple FragmentPagerAdapter
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    //Debug
    private final String TAG = "ViewPagerAdapter";

    public static final int NUM_DAYS = 7;
    private String[] daysFullName;

    private TimetableFragment.OnCourseSelected onCourseSelected;

    /**
     * Static array which holds for as long as possible the data for the current week.
     * Used to avoid unnecessary queries to the database.
     */
//    public static ArrayList<Course>[] listsOfCourses ;

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
    public static final String COURSES_IN_EVEN_WEEK = "p";
    public static final String COURSES_IN_ODD_WEEK = "i";

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
        this.onCourseSelected = onCourseSelected;
        if(daysFullName == null)
            daysFullName = onCourseSelected.getActivity().getResources().getStringArray(R.array.days_of_week_full_name);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(daysFullName == null)
            daysFullName = onCourseSelected.getActivity().getResources().getStringArray(R.array.days_of_week_full_name);
        return daysFullName[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if(daysFullName == null)
            daysFullName = onCourseSelected.getActivity().getResources().getStringArray(R.array.days_of_week_full_name);
        final int week = Utils.getCurrentWeek(onCourseSelected.getActivity());
        DayFragment dayFragment = DayFragment.createFragment(daysFullName[position], week, position);

        return dayFragment;
    }

    @Override
    public int getCount() {
        return NUM_DAYS;
    }
}

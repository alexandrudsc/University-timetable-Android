package com.developer.alexandru.aplicatie_studenti.navigation_drawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.developer.alexandru.aplicatie_studenti.AboutFragment;
import com.developer.alexandru.aplicatie_studenti.HolidaysFragment;
import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.TimetableFragment;
import com.developer.alexandru.aplicatie_studenti.action_bar.NonCurrentWeekFragment;

/**
 * Created by Alexandru on 9/14/2014.
 */
public class DrawerToggle extends ActionBarDrawerToggle {

    //Debug
    private static final boolean D = true;
    private static final String TAG = "DrawerToggle";

    //The page displayed
    private int currentPage = 0;
    //Page to be opened
    private int selectedPage;

    private DrawerLayout drawer;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private Activity activity;

    private Fragment timetableFragment;
    private Fragment aboutFragment;
    private Fragment holidaysFragment;
    private Fragment helpFragment;
    private Fragment examsFragment;
    private Fragment nonCurrentWeekFragment;

    public DrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.drawer = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        this.activity = activity;
        this.fragmentManager = ((MainActivity)activity).getSupportFragmentManager();
        timetableFragment = fragmentManager.findFragmentByTag(MainActivity.TIMETABLE_FRAGMENT_TAG);

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if (currentPage == selectedPage)
            return; //Item clicked in navigation drawer is already selected
        setCurrentPage(selectedPage);
        switch (selectedPage) {
            case NavDrawerAdapter.CURRENT_WEEK:
                timetableFragment = new TimetableFragment();
                currentPage = NavDrawerAdapter.CURRENT_WEEK;
                replaceFragment(timetableFragment);
                break;
            case NavDrawerAdapter.ABOUT:
                aboutFragment = new AboutFragment();
                currentPage = NavDrawerAdapter.ABOUT;
                replaceFragment(aboutFragment);
                break;
            case NavDrawerAdapter.EXAMS:
                examsFragment = new HolidaysFragment();
                currentPage = NavDrawerAdapter.EXAMS;
                replaceFragment(examsFragment);
                break;
            case NavDrawerAdapter.HELP:
                helpFragment = new HolidaysFragment();
                currentPage = NavDrawerAdapter.HELP;
                replaceFragment(helpFragment);
                break;
            case NavDrawerAdapter.HOLIDAYS:
                holidaysFragment = new HolidaysFragment();
                currentPage = NavDrawerAdapter.HOLIDAYS;
                replaceFragment(holidaysFragment);
                break;
            default:
                Bundle args = new Bundle();
                args.putInt(NonCurrentWeekFragment.NAME_OF_WEEK_NUMBER, selectedPage - 4);
                nonCurrentWeekFragment = new NonCurrentWeekFragment((MainActivity) activity);
                nonCurrentWeekFragment.setArguments(args);
                replaceFragment(nonCurrentWeekFragment);

                if (D) Log.d(TAG, "" + (selectedPage - 4));
        }
    }

    public void setSelectedPage(int selectedPage){
        this.selectedPage = selectedPage;
    }

    public void setCurrentPage(int currentPage){
        this.currentPage = currentPage;
    }

    public int getCurrentPage(){
        return this.currentPage;
    }

    private void replaceFragment(Fragment fr){

        if(fragmentManager != null )
            this.transaction = fragmentManager.beginTransaction();
        else
            return;

        if(activity.findViewById(R.id.fragment_container) != null)
            //One pane layout
            transaction.replace(R.id.fragment_container, fr).commit();

        else
            //Two pane layout
            transaction.replace(R.id.timetable_container, fr).commit();
    }



}

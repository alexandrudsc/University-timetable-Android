package com.developer.alexandru.orarusv.navigation_drawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.developer.alexandru.orarusv.AboutFragment;
import com.developer.alexandru.orarusv.HolidaysFragment;
import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekFragment;
import com.developer.alexandru.orarusv.main.MainActivity;
import com.developer.alexandru.orarusv.main.MainActivityPresenterImpl;
import com.developer.alexandru.orarusv.main.TimetableFragment;
import com.developer.alexandru.orarusv.splash_screen.TutorialActivity;

/** Created by Alexandru on 9/14/2014. */
public class DrawerToggle extends ActionBarDrawerToggle {

  // Debug
  private static final boolean D = true;
  private static final String TAG = "DrawerToggle";

  // The page displayed
  private int currentPage = 0;
  // Page to be opened
  private int selectedPage;

  private DrawerLayout drawer;
  private FragmentTransaction transaction;
  private FragmentManager fragmentManager;
  private Activity activity;

  public Fragment timetableFragment;
  public Fragment aboutFragment;
  public Fragment holidaysFragment;
  public Fragment helpFragment;
  public Fragment examsFragment;
  public Fragment nonCurrentWeekFragment;

  public DrawerToggle(
      Activity activity,
      DrawerLayout drawerLayout,
      Toolbar toolbar,
      int openDrawerContentDescRes,
      int closeDrawerContentDescRes) {
    super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    this.drawer = activity.findViewById(R.id.drawer_layout);
    this.activity = activity;
    this.fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
    timetableFragment =
        fragmentManager.findFragmentByTag(MainActivityPresenterImpl.TIMETABLE_FRAGMENT_TAG);
  }

  @Override
  public void onDrawerOpened(View drawerView) {
    super.onDrawerOpened(drawerView);
  }

  @Override
  public void onDrawerClosed(View drawerView) {
    super.onDrawerClosed(drawerView);
    drawerClosed();
  }

  public void drawerClosed() {
    if (currentPage == selectedPage)
      return; // Item clicked in navigation drawer is already selected
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
      case NavDrawerAdapter.HELP:
        if (this.activity != null) {
          Intent var2 = new Intent(this.activity, TutorialActivity.class);
          this.activity.startActivity(var2);
          this.setSelectedPage(0);
          this.setCurrentPage(0);
          return;
        }
        break;
      case NavDrawerAdapter.HOLIDAYS:
        holidaysFragment = new HolidaysFragment();
        currentPage = NavDrawerAdapter.HOLIDAYS;
        replaceFragment(holidaysFragment);
        break;
      default:
        Bundle args = new Bundle();
        args.putInt(
            NonCurrentWeekFragment.NAME_OF_WEEK_NUMBER, selectedPage - NavDrawerAdapter.ABOUT);
        nonCurrentWeekFragment = new NonCurrentWeekFragment((MainActivity) activity);
        nonCurrentWeekFragment.setArguments(args);
        replaceFragment(nonCurrentWeekFragment);

        if (D) Log.d(TAG, "" + (selectedPage - NavDrawerAdapter.ABOUT));
    }
  }

  public void setSelectedPage(int selectedPage) {
    this.selectedPage = selectedPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public int getCurrentPage() {
    return this.currentPage;
  }

  protected void replaceFragment(Fragment fr) {

    if (fragmentManager != null) this.transaction = fragmentManager.beginTransaction();
    else return;
    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    if (activity.findViewById(R.id.fragment_container) != null)
      // One pane layout
      transaction.replace(R.id.fragment_container, fr).addToBackStack(TAG).commit();
    else
      // Two pane layout
      transaction.replace(R.id.timetable_container, fr).addToBackStack(TAG).commit();
  }
}

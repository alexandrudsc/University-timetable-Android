package com.developer.alexandru.orarusv.view_pager;

import androidx.fragment.app.FragmentManager;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.main.TimetableFragment;

/**
 * Created by Alexandru on 6/13/14. Adapter providing fragments for each day NOT SURE IF I SHOULD
 * USE FragmentStatePagerAdapter or a simple FragmentPagerAdapter
 */
public class TimetableViewPagerAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {

  // Debug
  private final String TAG = "TimetableViewPagerAdapter";

  public static final int NUM_DAYS = 7;
  private String[] daysFullName;

  private TimetableFragment.OnCourseSelected onCourseSelected;

  /**
   * Constructor for the view pager adapter
   *
   * @param onCourseSelected the interface implemented by the activity hosting the fragment with the
   *     the view pager
   * @param childFragManager the fragment manager within the fragment hosting the view pager
   */
  public TimetableViewPagerAdapter(
      TimetableFragment.OnCourseSelected onCourseSelected, FragmentManager childFragManager) {
    super(childFragManager);
    this.onCourseSelected = onCourseSelected;
    if (daysFullName == null)
      daysFullName =
          onCourseSelected
              .getActivity()
              .getResources()
              .getStringArray(R.array.days_of_week_full_name);
  }

  @Override
  public CharSequence getPageTitle(int position) {
    if (daysFullName == null)
      daysFullName =
          onCourseSelected
              .getActivity()
              .getResources()
              .getStringArray(R.array.days_of_week_full_name);
    return daysFullName[position];
  }

  @Override
  public int getItemPosition(Object object) {
    return POSITION_NONE;
  }

  @Override
  public androidx.fragment.app.Fragment getItem(int position) {
    if (daysFullName == null)
      daysFullName =
          onCourseSelected
              .getActivity()
              .getResources()
              .getStringArray(R.array.days_of_week_full_name);
    final int week = Utils.getCurrentWeek(onCourseSelected.getActivity());
    DayFragment dayFragment = DayFragment.createFragment(daysFullName[position], week, position);

    return dayFragment;
  }

  @Override
  public int getCount() {
    return NUM_DAYS;
  }
}

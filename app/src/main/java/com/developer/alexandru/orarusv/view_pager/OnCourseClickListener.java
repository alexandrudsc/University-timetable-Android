package com.developer.alexandru.orarusv.view_pager;

import android.util.Log;
import android.view.View;

import com.developer.alexandru.orarusv.main.TimetableFragment;
import com.developer.alexandru.orarusv.data.Course;

/**
 * Created by alexandru on 9/16/16.
 */
public class OnCourseClickListener implements View.OnClickListener {

    private TimetableFragment.OnCourseSelected onCourseSelected;
    private Course c;

    public OnCourseClickListener(TimetableFragment.OnCourseSelected onCourseSelected, Course c) {
        this.onCourseSelected = onCourseSelected;
        this.c = c;
    }

    @Override
    public void onClick(View view) {
        Log.d("CLICKED ON", c.getFullName());
        onCourseSelected.onCourseClicked(c);
        view.setSelected(true);
    }
}

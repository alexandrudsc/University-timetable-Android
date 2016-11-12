package com.developer.alexandru.orarusv.main;

import android.content.Intent;

import com.developer.alexandru.orarusv.data.Course;

/**
 * Created by alexandru on 10/25/16.
 * Presenter for MainActivity
 */
public interface MainActivityPresenter {
    void initialize();
    void checkForNewTimeStructure();
    void onNewIntent(Intent intent);
    boolean onCourseClicked(Course course);
}

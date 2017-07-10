package com.developer.alexandru.orarusv.view_pager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.main.TimetableFragment.OnCourseSelected;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.AlternativeCoursesListAdapter;
import com.developer.alexandru.orarusv.data.Course;

import java.util.ArrayList;

/**
 * Created by alexandru on 9/15/16.
 * Event listener for LongClick on a course  from list (MainActivity, NonCurrentWeek)
 */
public class OnCourseLongClickListener implements View.OnLongClickListener {
    private Course course;
    private OnCourseSelected courseSelectedCallback;

    public OnCourseLongClickListener(OnCourseSelected courseSelectedCallback, Course course) {
        this.courseSelectedCallback = courseSelectedCallback;
        this.course = course;
    }

    public boolean onLongClick(View var1) {
        Log.d("LONG CLICKED ON", this.course.toString());
        if (!Utils.hasInternetAccess(courseSelectedCallback.getActivity())) {
            Utils.toastNoInternetAccess(courseSelectedCallback.getActivity());
            return true;
        }
        ArrayList<Course> items = new ArrayList<>();

        AlternativeCoursesListAdapter adapter = new AlternativeCoursesListAdapter(courseSelectedCallback.getActivity(), R.layout.simple_course_layout, items);
        // Builder for the dialog displayed at ItemLongClick
        AlertDialog.Builder builder = new AlertDialog.Builder(courseSelectedCallback.getActivity());
        builder.setTitle(R.string.choose)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Event handled by the inner view. See DialogListItem's implementation of getView()
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    courseSelectedCallback.getActivity().refreshCourses();
                }
        });
        AlertDialog dialog = builder.create();
        adapter.setDialog(dialog);
        dialog.show();

        AlternativeCoursesLoader alternativeCoursesLoader = new AlternativeCoursesLoader(adapter, this.course);
        alternativeCoursesLoader.execute();
        return false;
    }
}

package com.developer.alexandru.orarusv.view_pager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.TimetableFragment.OnCourseSelected;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.DialogListAdapter;

import java.util.ArrayList;

/**
 * Created by alexandru on 9/15/16.
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

        DialogListAdapter adapter = new DialogListAdapter(courseSelectedCallback.getActivity(), R.layout.course_item_layout, items);
        // Builder for the dialog displayed at ItemLongClick
        AlertDialog.Builder builder = new AlertDialog.Builder(courseSelectedCallback.getActivity());
        builder.setTitle(R.string.choose)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Event handled by the inner view. See DialogListItem's implementation of getView()
                    }
                });
        AlertDialog dialog = builder.create();
        adapter.setDialog(dialog);
        dialog.show();

        ParallelCoursesLoader parallelCoursesLoader = new ParallelCoursesLoader(this.courseSelectedCallback.getActivity(), adapter, course.profID);
        parallelCoursesLoader.execute();
        return false;
    }
}

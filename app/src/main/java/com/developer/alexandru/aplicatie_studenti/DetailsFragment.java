package com.developer.alexandru.aplicatie_studenti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.developer.alexandru.aplicatie_studenti.data.Course;
import com.developer.alexandru.aplicatie_studenti.view_pager.DayFragment;

/**
 * Created by Alexandru on 7/14/14.
 */
public class DetailsFragment extends Fragment {

    //Debug
    public static final String TAG = "DETAILS";
    public static final boolean D = true;

    public static final String REPLACE_DETAILS_WITH_RESULT = "result_replacement";

    private Course course;
    private Context context;
    private boolean attachFrToContainer;
    private TextView courseTime = null, courseProf, courseLocation;

    private FragmentManager fm;

    public DetailsFragment() {
        super();
    }

    public DetailsFragment(FragmentManager fm, boolean attachFrToContainer) {
        super();
        this.fm = fm;
        this.attachFrToContainer = attachFrToContainer;
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if(args != null){
            course = args.getParcelable("course");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            this.course = savedInstanceState.getParcelable("saved_course");

    }

    @Override
    public void onResume() {
        super.onResume();
        if(D) Log.d(TAG, "resumed");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(D)  Log.d(TAG, "create view");
        View fragmentView = inflater.inflate(R.layout.fragment_details, container, attachFrToContainer);
        if(D) Log.d(TAG, ""+course);
        if(course == null)
            return inflater.inflate(R.layout.widget_empty_view, container, attachFrToContainer);

        Button courseButton = (Button)fragmentView.findViewById(R.id.course_button);
        courseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fm == null)
                    //Configuration changes.Interface describing activity will be certainly updated
                    fm = DayFragment.onCourseSelected.getActivity().getFragManager();
                final FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.anim_fragments, 0);
                ResultsFragment fr = new ResultsFragment(fm, course.name, course.type, course.info);
                ft.replace(R.id.course_fragment_container, fr);
                ft.addToBackStack(REPLACE_DETAILS_WITH_RESULT);
                ft.commit();
            }
        });
        try{
            courseProf = (TextView) fragmentView.findViewById(R.id.course_prof);
            courseTime = (TextView) fragmentView.findViewById(R.id.course_time);
            courseLocation = (TextView) fragmentView.findViewById(R.id.course_location);

            courseProf.setText(course.prof);
            courseTime.setText(course.time);
            courseLocation.setText(course.location);
        }
        catch (NullPointerException e ){
        }
        return fragmentView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("saved_course", course);
    }

}

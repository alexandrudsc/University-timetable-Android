package com.developer.alexandru.aplicatie_studenti.view_pager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.alexandru.aplicatie_studenti.SettingsActivity;
import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.TimetableFragment;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/13/14.
 */
public class DayFragment extends ListFragment {
    //Debug
    public static final String TAG = "ListFragment";
    public static final boolean D = true;

    private String title;
    private int position;
    public static TimetableFragment.OnCourseSelected onCourseSelected;

    public static DayFragment createFragment(TimetableFragment.OnCourseSelected onCourseSelected,
                                             String title, int position){
        DayFragment.onCourseSelected = onCourseSelected;

        DayFragment dayFragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        dayFragment.setArguments(args);

        return dayFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            title = args.getString("title");
            position = args.getInt("position");
            if(D) Log.d(TAG, "Fragment " + title + " created");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_day, container, false);
        frag.findViewById(android.R.id.empty).setOnClickListener(new DownloadOnClickListener());

        //checked for saved instance (mainly in case of screen rotation)
        if(savedInstanceState != null){
            if(D) Log.d(TAG, "RESTORE");

            int position = savedInstanceState.getInt("position");
            if(ViewPagerAdapter.listsOfCourses == null)
                ViewPagerAdapter.listsOfCourses = new ArrayList[ViewPagerAdapter.NUM_DAYS];
            try{
                if(ViewPagerAdapter.listsOfCourses[position] == null){
                    ViewPagerAdapter.listsOfCourses[position] = savedInstanceState.getParcelableArrayList("values");
                    if(ViewPagerAdapter.listsOfCourses[position] == null)
                        if(D) Log.d("DAY FRAG", "nothing changed");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if(D) Log.d("FRAGMENT", "adapter from saved state" + title);
            if(this.getListAdapter() == null){
                this.setListAdapter(new MyListViewAdapter(onCourseSelected, ViewPagerAdapter.listsOfCourses[position]));

                if(D) Log.d("FRAGMENT", "adapter set " + title);
                //this.getListView().setItemsCanFocus(false);
            }else {
                if(D) Log.d("DAY FRAGMENT RESUMED", "old adapter");
            }
        }

        return frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        try {
            this.setListAdapter(new MyListViewAdapter(onCourseSelected, ViewPagerAdapter.listsOfCourses[position]));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(D) Log.d(TAG, title + "  resumed");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
            outState.putInt("position", position);
            if(ViewPagerAdapter.listsOfCourses[position] != null)
                outState.putParcelableArrayList("values", ViewPagerAdapter.listsOfCourses[position]);
            else
                outState.putParcelableArrayList("values", ((MyListViewAdapter)getListAdapter()).getValues());

        super.onSaveInstanceState(outState);
        if(D) Log.d(TAG, "SAVE");
    }

    private class DownloadOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(onCourseSelected.getContext(), SettingsActivity.class);
            onCourseSelected.getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_DOWNLOAD);
        }
    }
}

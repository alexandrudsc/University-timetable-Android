package com.developer.alexandru.aplicatie_studenti.view_pager;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.developer.alexandru.aplicatie_studenti.data.Course;

import java.util.ArrayList;

/**
 * Created by Alexandru on 8/7/14.
 */
public class CustomViewPager extends ViewPager {

    private int positionToSaveState = -1;
    private static CustomViewPager viewPager;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        if(positionToSaveState != -1){
            DayFragment fragment = (DayFragment)((ViewPagerAdapter)getAdapter()).getItem(positionToSaveState);
            State state = new State(((MyListViewAdapter)fragment.getListAdapter()).getValues());
            return state;
        }
        return null;
    }

    public void setPositionSaveState(int position){
        this.positionToSaveState = position;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        State oldState;
        try{
            oldState = (State)state;
            DayFragment fragment = (DayFragment)((ViewPagerAdapter)getAdapter()).getItem(positionToSaveState);
            MyListViewAdapter adapter = ((MyListViewAdapter)fragment.getListAdapter());
            adapter.getValues().clear();
            adapter.getValues().addAll(oldState.valuesCurrent);
            adapter.notifyDataSetChanged();
        }catch (ClassCastException e ){
            e.printStackTrace();
        }

    }

    private static class State implements Parcelable{

        private ArrayList<Course> valuesCurrent;

        public State(ArrayList<Course> values) {
            valuesCurrent = values;
        }

        public State(Parcel in){
            valuesCurrent = in.readArrayList(getClass().getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeParcelableArray((Course[])valuesCurrent.toArray(), 0);
        }

        public static final Creator <State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel parcel) {
                return null;
            }

            @Override
            public State[] newArray(int position) {
                return new State[position];
            }
        };

    }

}

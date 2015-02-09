package com.developer.alexandru.aplicatie_studenti;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.developer.alexandru.aplicatie_studenti.view_pager.MyListViewAdapter;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

/**
 * Created by Alexandru on 6/30/14.
 */
public class CheckBoxOnChangeListener implements CheckBox.OnCheckedChangeListener {

    private String weekProgressFileName;
    private String courseNameAndType;
    public CheckBoxOnChangeListener(String weekProgressFileName, String courseNameAndType) {
        this.weekProgressFileName = weekProgressFileName;
        this.courseNameAndType = courseNameAndType;
    }

    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean value) {
        final MyListViewAdapter.ViewHolder tag = (MyListViewAdapter.ViewHolder)checkBox.getTag();

        final SharedPreferences currentWeekProgressFile = checkBox.getContext().getSharedPreferences(weekProgressFileName,
                                                                                                Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = currentWeekProgressFile.edit();
        editor.putBoolean(courseNameAndType, value);
        editor.commit();

    }
}

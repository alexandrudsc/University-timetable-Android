package com.developer.alexandru.aplicatie_studenti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.developer.alexandru.aplicatie_studenti.view_pager.MyListViewAdapter;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

/**
 * Created by Alexandru on 6/30/14.
 * Listener for checkbox change listener.A presence or absence can be marked here (save a bool value in a prefs file)
 */
public class CheckBoxOnChangeListener implements CheckBox.OnCheckedChangeListener {

    public CheckBoxOnChangeListener(){

    }

    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean value) {
        final String tag = (String)checkBox.getTag();
        String[] fileAndPreference = tag.split(";");

        final SharedPreferences currentWeekProgressFile = checkBox.getContext().getSharedPreferences(fileAndPreference[0],
                                                                                                Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = currentWeekProgressFile.edit();
        editor.putBoolean(fileAndPreference[1], value);
        if (Build.VERSION.SDK_INT < 9)
            editor.commit();
        else
            editor.apply();
    }

}

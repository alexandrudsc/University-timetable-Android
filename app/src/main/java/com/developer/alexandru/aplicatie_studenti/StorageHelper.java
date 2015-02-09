package com.developer.alexandru.aplicatie_studenti;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alexandru on 6/29/14.
 */
public class StorageHelper {

    public static void saveCurrentWeekProgress(Context context, int week){
        SharedPreferences currentWeekProgress = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences weekFile = context.getSharedPreferences(String.valueOf(week), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = weekFile.edit();

        for (Map.Entry<String, ?> entry : currentWeekProgress.getAll().entrySet()){

            Boolean value = (Boolean)entry.getValue();
            String key = entry.getKey();

            editor.putBoolean(key, value);
        }
        editor.commit();
    }

}

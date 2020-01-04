package com.developer.alexandru.orarusv;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/** Created by Alexandru on 6/29/14. */
public class StorageHelper {

  public static void saveCurrentWeekProgress(Context context, int week) {
    SharedPreferences currentWeekProgress = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences weekFile =
        context.getSharedPreferences(String.valueOf(week), Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = weekFile.edit();

    for (Map.Entry<String, ?> entry : currentWeekProgress.getAll().entrySet()) {

      Boolean value = (Boolean) entry.getValue();
      String key = entry.getKey();

      editor.putBoolean(key, value);
    }
    editor.commit();
  }
}

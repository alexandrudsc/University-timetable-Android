package com.developer.alexandru.orarusv;

import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;

/** Created by alexandru on 9/17/16. Event listener for Spinner Group on SettingsActivity */
@Deprecated
public class SettingsGroupSelected implements AdapterView.OnItemSelectedListener {

  private String studiesLevel;
  private SharedPreferences prefs;

  public SettingsGroupSelected(SharedPreferences prefs) {
    this.prefs = prefs;
    studiesLevel = prefs.getString(SettingsActivity.LEVEL_PREF, SettingsActivity.LICENCE);
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    Elem group = (Elem) parent.getAdapter().getItem(position);
    switch (parent.getId()) {
      case R.id.spinner_group_undergraduate:
        if (studiesLevel.equals(SettingsActivity.LICENCE)) saveSelectedGroup(group);
        break;
      case R.id.spinner_group_master:
        if (studiesLevel.equals(SettingsActivity.MASTERS)) saveSelectedGroup(group);
        break;
      case R.id.spinner_group_phd:
        if (studiesLevel.equals(SettingsActivity.PHD)) saveSelectedGroup(group);
        break;
    }
  }

  public void onNothingSelected(AdapterView var1) {}

  // Save selected group to preferences
  public void saveSelectedGroup(Elem group) {
    if (prefs == null) return;
    if (group == null) return;
    SharedPreferences.Editor editor = prefs.edit();
    editor.putInt(SettingsActivity.GROUP_ID_PREF, group.id);
    editor.putString(SettingsActivity.GROUP_NAME_PREF, group.name);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) editor.apply();
    else editor.commit();
  }
}

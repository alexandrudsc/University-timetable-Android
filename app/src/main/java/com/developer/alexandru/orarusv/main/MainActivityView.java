package com.developer.alexandru.orarusv.main;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentManager;

/** Created by alexandru on 10/25/16. The View for MainActivity */
public interface MainActivityView {
  FragmentManager getSupportFragmentManager();

  Context getContext();

  <T extends View> T findViewById(int id);

  Intent getIntent();

  /**
   * Allows navigation between fragments and activities using the navigation drawer
   *
   * @param enable - if true, navigation drawer can be used. If false, navigation drawer is closed
   *     and locked
   */
  void enableNavDrawer(boolean enable);
}

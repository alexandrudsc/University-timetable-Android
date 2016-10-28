package com.developer.alexandru.orarusv.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Created by alexandru on 10/25/16.
 * The View for MainActivity
 */
public interface MainActivityView {
    FragmentManager getSupportFragmentManager();
    Context getContext();
    View findViewById(int id);
    Intent getIntent();
}

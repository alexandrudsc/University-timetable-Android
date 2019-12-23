package com.developer.alexandru.orarusv;

import android.view.View;

/**
 * Created by alexandru on 9/12/16.
 * @deprecated replaced by TutorialActivity
 */
public class HelpNavigationListener implements View.OnClickListener {

    private HelpActivity activity;

    public HelpNavigationListener(HelpActivity activity) {
        this.activity = activity;
    }

    public void onClick(View var1) {
        if (this.activity != null) {
            this.activity.finish();
        }
    }
}

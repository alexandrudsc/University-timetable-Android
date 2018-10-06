package com.developer.alexandru.orarusv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.toolbar_help);
        this.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_previous_item);
        toolbar.setNavigationOnClickListener(new HelpNavigationListener(this));
    }
}

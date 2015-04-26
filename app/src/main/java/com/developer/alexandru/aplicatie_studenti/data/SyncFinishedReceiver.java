package com.developer.alexandru.aplicatie_studenti.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Spinner;

import com.developer.alexandru.aplicatie_studenti.SettingsActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Alexandru on 12/26/2014.
 * Broadcast receiver for the event announced by the Synchronizer when the data for faculties and groups was fetched from the web
 */
public class SyncFinishedReceiver extends BroadcastReceiver {

    // DEBUG
    private final boolean D = true;
    private final String TAG = "SyncFinishedReceiver";

    private Spinner groupsUndergraduates, groupsMasters, groupsPHD;

    private WeakReference<SettingsActivity> downloaderActivityWeakReference;
    public SyncFinishedReceiver(SettingsActivity activity, Spinner groupsUndergraduates, Spinner groupsMasters, Spinner groupsPHD) {
        this.groupsUndergraduates = groupsUndergraduates;
        this.groupsMasters = groupsMasters;
        this.groupsPHD = groupsPHD;
        this.downloaderActivityWeakReference = new WeakReference<SettingsActivity>(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Synchronizer.ACTION_SYNC_FINISHED)){
            if (downloaderActivityWeakReference != null){
                SettingsActivity activity =  downloaderActivityWeakReference.get();
                if (activity != null) {
                    activity.resetFacultiesSpinnerData();
                    Log.d(TAG, "faculties reseted");
                }
                //this.abortBroadcast();
            }
        }
    }

}

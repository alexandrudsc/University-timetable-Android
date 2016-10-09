package com.developer.alexandru.orarusv.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.developer.alexandru.orarusv.MainActivity;
import com.developer.alexandru.orarusv.download.DownloadActivity;

import java.lang.ref.WeakReference;

/**
 * Created by alexandru on 10/9/16.
 * Local broadcast receiver for notifications from TimetableDownloaderService
 */
public class TimetableDownloadFinished extends BroadcastReceiver {

    private WeakReference<DownloadActivity> activityWeakReference;

    public TimetableDownloadFinished(DownloadActivity activity) {
        super();
        this.activityWeakReference = new WeakReference<DownloadActivity>(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.activityWeakReference != null) {
            final DownloadActivity activity = this.activityWeakReference.get();
            if (activity != null) {
                activity.downloadFinished();
            }
        }
    }
}

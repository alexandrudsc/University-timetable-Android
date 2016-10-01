package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.SettingsActivity;
import com.developer.alexandru.orarusv.data.TimetableDownloaderService;

/**
 * Created by alexandru on 9/26/16.
 */
public class DownloadActivityPresenterImpl implements DownloadActivityPresenter {

    private JavascriptInterface javascriptInterface;
    DownloadActivityView view;

    public DownloadActivityPresenterImpl(DownloadActivityView view) {
        javascriptInterface = new JavascriptInterface();
        view.setJavascriptInterface(javascriptInterface);
        view = view;
    }

    @Override
    public void downloadButtonClicked(Context context, WebView webView) {
        webView.loadUrl("javascript:(" +
                "function getData() {" +
                "   var e = document.getElementById('grupaS');" +
                "   Android.groupChanged(parseInt( e.options[e.selectedIndex].value) )" +
                "}()" +
                ")");
        String url = SettingsActivity.PARTIAL_TIMETABLE_URL + javascriptInterface.getGroupId();
        Intent intent = new Intent(context, TimetableDownloaderService.class);
        intent.putExtra(TimetableDownloaderService.EXTRA_URL, url);
        context.startService(intent);
    }

    public static class JavascriptInterface {

        private int groupId;

        @android.webkit.JavascriptInterface
        public void groupChanged(int groupID) {
            Log.d("JAVASCRIPT INTERFACE", groupID + "");
            this.groupId = groupID;
        }

        public int getGroupId(){
            return groupId;
        }
    }

}

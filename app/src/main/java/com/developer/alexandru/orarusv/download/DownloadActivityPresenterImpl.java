package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;

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
    }

    @Override
    public void downloadButtonClicked(Context context) {
        String url = SettingsActivity.PARTIAL_TIMETABLE_URL + javascriptInterface.getGroupId();
        Intent intent = new Intent(context, TimetableDownloaderService.class);
        intent.putExtra(TimetableDownloaderService.EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    public void webViewPageFinished(WebView view) {

            view.loadUrl("javascript:\n" +
                    "(\n" +
                    "function selectChanged() {\n" +
                    "   alert('Hello'); \n" +
                    "   Android.groupChanged(document.getElementById('grupaS').innerText);" +
                    "};\n" +
                    "document.getElementById('grupaS').onchange=function() {selectChanged()};\n" +
                    ")");
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

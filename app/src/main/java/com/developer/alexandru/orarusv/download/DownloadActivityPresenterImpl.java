package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.CsvAPI;
import com.developer.alexandru.orarusv.data.TimetableDownloaderService;

/**
 * Created by alexandru on 9/26/16.
 * Impl for presenter of DownloadActivity
 */
public class DownloadActivityPresenterImpl implements DownloadActivityPresenter {

    // Debug
    static final String TAG = "JAVASCRIPT INTERFACE";
    private static final boolean DEBUG = true;

    private JavascriptInterface javascriptInterface;
    DownloadActivityView view;

    public DownloadActivityPresenterImpl(DownloadActivityView view) {
        this.view = view;
        initialize();
    }

    @Override
    public void initialize() {
        javascriptInterface = new JavascriptInterface();
        view.setJavascriptInterface(javascriptInterface);
    }

    @Override
    public void downloadButtonClicked() {
        if (view == null)
            return;
        WebView webView = view.getWebView();
        if (webView == null)
            return;
        final Context context = view.getContext();
        if (!Utils.hasInternetAccess(context)) {
            view.connectAgain();
            return;
        }

        String url = javascriptInterface.getUrl();
        if (DEBUG) Log.d(TAG, url + "");

        Intent intent = new Intent(context, TimetableDownloaderService.class);
        intent.putExtra(CsvAPI.EXTRA_URL, url);
        view.showProgressDialog();
        context.startService(intent);
    }

    @Override
    public void webViewPageLoaded(String url) {
        if (DEBUG) Log.d(TAG, url);

        StringBuilder javascriptToInject = new StringBuilder("javascript:(" +
                "function getData() {");

        if (url.contains("grupa")) {
            view.setDownloadBtnVisible(true);
            view.setDownloadBtnText(view.getContext().getResources().getString(R.string.download_timetable_group));
            javascriptToInject.append(
                    "    var ID = location.search.split('ID=')[1].split('&')[0]; " +
                    "    Android.urlChanged(parseInt(ID), 0); ");
        }
        else if (url.contains("prof")) {
            view.setDownloadBtnVisible(true);
            view.setDownloadBtnText(view.getContext().getResources().getString(R.string.download_timetable_prof));
            javascriptToInject.append(
                    "    var ID = location.search.split('ID=')[1].split('&')[0]; " +
                    "    Android.urlChanged(parseInt(ID), 1); ");
        }
        else {
            view.setDownloadBtnVisible(false);
            javascriptToInject.append(
                    "    Android.urlChanged(-1, -1) ");
        }

        javascriptToInject.append("}()" +
                ")");
        view.getWebView().loadUrl(javascriptToInject.toString());
    }

    @Override
    public void tryConnectionAgain() {
        view.connectAgain();
    }

    @Override
    public void webViewReceiverErr(int errorCode) {
        // for API < 23 WebViewClient.ERROR_CONNECT == -2
        if (WebViewClient.ERROR_CONNECT == errorCode || errorCode == -2) {
            view.connectAgain();
        }
    }

    @Override
    public void downloadFinished() {
        view.hideProgressDialog();
    }

    public static class JavascriptInterface {

        private int groupId = -1;

        private String url = null;

        @android.webkit.JavascriptInterface
        public void groupChanged(int groupID) {
            Log.d(TAG, groupID + "");
            this.groupId = groupID;
        }

        @android.webkit.JavascriptInterface
        public void urlChanged(int timetableId, int timetableMode) {
            if (timetableMode == CsvAPI.TIMETABLE_GROUP) {
                this.url = CsvAPI.PARTIAL_GROUP_TIMETABLE_URL + timetableId;
            }
            else if (timetableMode == CsvAPI.TIMETABLE_PROF) {
                this.url = CsvAPI.PARTIAL_PROF_TIMETABLE_URL+ timetableId;
            } else {
                this.url = null;
            }
        }

        public int getGroupId(){
            return groupId;
        }

        private String getUrl() {
            return url;
        }
    }
}

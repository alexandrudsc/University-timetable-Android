package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.developer.alexandru.orarusv.SettingsActivity;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.TimetableDownloaderService;

/**
 * Created by alexandru on 9/26/16.
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
        if (!Utils.hasInternetAccess(view.getContext())) {
            view.connectAgain();
            return;
        }
        view.getWebView().loadUrl("javascript:(" +
                "function getData() {" +
                "   var e = document.getElementById('grupaS');" +
                "   Android.groupChanged(parseInt( e.options[e.selectedIndex].value) )" +
                "}()" +
                ")");

        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(webView.getUrl());
        final String id = sanitizer.getValue("ID");
        Integer groupID;
        if (id == null) {
            groupID = javascriptInterface.getGroupId();
        }
        else {
            groupID = Integer.parseInt(id);
        }
        if (groupID == -1) {
            return;
        }
        String url = SettingsActivity.PARTIAL_TIMETABLE_URL + groupID;
        if (DEBUG) Log.d(TAG, groupID + "");

        final Context context = view.getContext();
        Intent intent = new Intent(context, TimetableDownloaderService.class);
        intent.putExtra(TimetableDownloaderService.EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    public void webViewFinishedLoading() {
        view.getWebView().loadUrl("javascript:(" +
                "function getData() {" +
                "   var e = document.getElementById('grupaS');" +
                "   Android.groupChanged(parseInt( e.options[e.selectedIndex].value) )" +
                "}()" +
                ")");
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

        @android.webkit.JavascriptInterface
        public void groupChanged(int groupID) {
            Log.d(TAG, groupID + "");
            this.groupId = groupID;
        }

        public int getGroupId(){
            return groupId;
        }
    }
}

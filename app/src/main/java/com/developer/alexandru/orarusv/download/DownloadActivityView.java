package com.developer.alexandru.orarusv.download;


import android.content.Context;
import android.webkit.WebView;

/**
 * Created by alexandru on 9/26/16.
 */
public interface DownloadActivityView {
    void setJavascriptInterface(DownloadActivityPresenterImpl.JavascriptInterface javascriptInterface);
    WebView getWebView();
    Context getContext();
    void connectAgain();
    void hideProgressDialog();
}
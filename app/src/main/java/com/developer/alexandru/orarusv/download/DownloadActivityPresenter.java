package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.webkit.WebResourceError;
import android.webkit.WebView;

/**
 * Created by alexandru on 9/26/16.
 */
public interface DownloadActivityPresenter {
    void initialize();
    void downloadButtonClicked();
    void webViewFinishedLoading();
    void tryConnectionAgain();
    void webViewReceiverErr(int errorCode);
    void downloadFinished();
}

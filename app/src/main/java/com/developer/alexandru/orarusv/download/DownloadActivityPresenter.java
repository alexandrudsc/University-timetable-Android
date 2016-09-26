package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by alexandru on 9/26/16.
 */
public interface DownloadActivityPresenter {
    void downloadButtonClicked(Context context);
    void webViewPageFinished(WebView webView);
}

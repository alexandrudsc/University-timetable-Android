package com.developer.alexandru.orarusv.download;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.TimetableDownloadFinished;

public class DownloadActivity extends Activity implements DownloadActivityView, View.OnClickListener {

    public static final String TIMETABLE_DOWNLOADED = "TIMETABLE_DOWNLOADED";

    private WebView webView;
    private Button downloadBtn;
    private Dialog progressDialog;

    private TimetableDownloadFinished timetableDownloadedReceiver;

    private DownloadActivityPresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new DownloadActivityPresenterImpl(this);

        if (Utils.hasInternetAccess(this)) {
            setContentView(R.layout.activity_download);
            createWithConnection();
        }
        else {
            setContentView(R.layout.activity_download_not_connected);
            createWithoutConnection();
        }

        registerReceiverTimetableDownloaded();
    }

    private void registerReceiverTimetableDownloaded() {
        final LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
        timetableDownloadedReceiver = new TimetableDownloadFinished(this);
        final IntentFilter filter = new IntentFilter(TIMETABLE_DOWNLOADED);
        instance.registerReceiver(timetableDownloadedReceiver, filter);
    }

    private void createWithoutConnection() {
        Button tryAgainBtn = (Button)findViewById(R.id.buttonTryAgain);
        tryAgainBtn.setOnClickListener(this);
    }

    private void createWithConnection() {
        webView = (WebView) findViewById(R.id.webview);
        downloadBtn = (Button) findViewById(R.id.timetable_download_btn);
        presenter.initialize();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new OrarUSVWebViewClient());
        webView.loadUrl("http://www.usv.ro/orar/mobilorar/vizualizare/orarUp1.php");

        downloadBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timetableDownloadedReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(timetableDownloadedReceiver);
        }
    }

    @Override
    public void setJavascriptInterface(DownloadActivityPresenterImpl.JavascriptInterface javascriptInterface) {
        if (webView != null) {
            webView.addJavascriptInterface(javascriptInterface, "Android");
        }
    }

    @Override
    public WebView getWebView() {
        return this.webView;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void connectAgain() {
        if (Utils.hasInternetAccess(this)) {
            this.setContentView(R.layout.activity_download);
            createWithConnection();
            presenter.initialize();
        } else {
            this.setContentView(R.layout.activity_download_not_connected);
            createWithoutConnection();
        }
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new Dialog(this);
        progressDialog.setTitle(R.string.downloading);
        progressDialog.setContentView(R.layout.loading);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        this.setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void setDownloadBtnVisible(boolean visible) {
        View downloadBtn = findViewById(R.id.timetable_download_btn);
        if (downloadBtn != null) {
            downloadBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void setDownloadBtnText(String text) {
        Button downloadBtn = (Button) findViewById(R.id.timetable_download_btn);
        if (downloadBtn != null) {
            downloadBtn.setText(text);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.timetable_download_btn:
                presenter.downloadButtonClicked();
                break;
            case R.id.buttonTryAgain:
                presenter.tryConnectionAgain();
                break;
        }
    }

    public void downloadFinished() {
        presenter.downloadFinished();
    }

    private class OrarUSVWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            presenter.webViewPageLoaded(url);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            presenter.webViewReceiverErr(error.getErrorCode());
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            presenter.webViewReceiverErr(errorCode);
        }
    }
}

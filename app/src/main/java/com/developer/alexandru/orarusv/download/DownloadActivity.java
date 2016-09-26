package com.developer.alexandru.orarusv.download;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.developer.alexandru.orarusv.R;

public class DownloadActivity extends Activity implements DownloadActivityView {

    private WebView webView;
    private DownloadActivityPresenterImpl presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        webView = (WebView) findViewById(R.id.webview);

        presenter = new DownloadActivityPresenterImpl(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new OrarUSVWebViewClient());

        webView.loadUrl("http://www.usv.ro/orar/mobilorar/vizualizare/orarUp1.php");;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        findViewById(R.id.timetable_download_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                presenter.downloadButtonClicked(DownloadActivity.this);
            }
        });
    }

    @Override
    public void setJavascriptInterface(DownloadActivityPresenterImpl.JavascriptInterface javascriptInterface) {
        webView.addJavascriptInterface(javascriptInterface, "Android");
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
            presenter.webViewPageFinished(view);

        }
    }
}

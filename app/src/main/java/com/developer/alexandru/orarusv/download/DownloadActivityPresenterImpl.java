package com.developer.alexandru.orarusv.download;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.Utils;
import com.developer.alexandru.orarusv.data.CsvAPI;
import com.developer.alexandru.orarusv.data.Timetable;
import com.developer.alexandru.orarusv.data.TimetableDownloaderService;

/** Created by alexandru on 9/26/16. Impl for presenter of DownloadActivity */
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
    if (view == null) return;
    WebView webView = view.getWebView();
    if (webView == null) return;
    final Context context = view.getContext();
    if (!Utils.hasInternetAccess(context)) {
      view.connectAgain();
      return;
    }

    final String url = javascriptInterface.getUrl();
    final int timetableId = javascriptInterface.getTimetableId();
    final int timetableType = javascriptInterface.getTimetableType();
    final String timetableName = javascriptInterface.getTimetableName();

    Intent intent = new Intent(context, TimetableDownloaderService.class);
    intent.putExtra(TimetableDownloaderService.EXTRA_URL, url);
    intent.putExtra(TimetableDownloaderService.EXTRA_TIMETABLE_TYPE, timetableType);
    intent.putExtra(TimetableDownloaderService.EXTRA_TIMETABLE_ID, timetableId);
    intent.putExtra(TimetableDownloaderService.EXTRA_TIMETABLE_NAME, timetableName);
    view.showProgressDialog();
    context.startService(intent);
  }

  @Override
  public void webViewPageLoaded(String url) {
    if (DEBUG) Log.d(TAG, url);

    StringBuilder javascriptToInject = new StringBuilder("javascript:(" + "function getData() {");

    if (url.contains("grupa")) {
      view.setDownloadBtnVisible(true);
      view.setDownloadBtnText(
          view.getContext().getResources().getString(R.string.download_timetable_group));
      javascriptToInject.append(
          "    var ID = location.search.split('ID=')[1].split('&')[0]; "
              + "    Android.urlChanged(parseInt(ID), 0);"
              + "    Android.setTimetableName(document.title); ");
    } else if (url.contains("prof")) {
      view.setDownloadBtnVisible(true);
      view.setDownloadBtnText(
          view.getContext().getResources().getString(R.string.download_timetable_prof));
      javascriptToInject.append(
          "    var ID = location.search.split('ID=')[1].split('&')[0]; "
              + "    Android.urlChanged(parseInt(ID), 1); "
              + "    Android.setTimetableName(document.title); ");
    } else {
      view.setDownloadBtnVisible(false);
      javascriptToInject.append("    Android.urlChanged(-1, -1) ");
    }

    javascriptToInject.append("}()" + ")");

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

    private int timetableId = -1;
    private Timetable.Type timetableType = Timetable.Type.Student;
    private String url = null;
    private String timetableName = null;

    public void setTimetableId(int timetableId) {
      if (DEBUG) Log.d(TAG, timetableId + "");
      this.timetableId = timetableId;
    }

    public int getTimetableId() {
      return timetableId;
    }

    public int getTimetableType() {
      return timetableType.ordinal();
    }

    @android.webkit.JavascriptInterface
    public void setTimetableName(String timetableName) {
      if (DEBUG) Log.d(TAG, timetableName);
      this.timetableName = timetableName;
    }

    public String getTimetableName() {
      return timetableName;
    }

    @android.webkit.JavascriptInterface
    public void urlChanged(int timetableId, int timetableMode) {
      setTimetableId(timetableId);
      if (timetableMode == CsvAPI.TIMETABLE_GROUP) {
        this.url = CsvAPI.PARTIAL_GROUP_TIMETABLE_URL + timetableId;
        timetableType = Timetable.Type.Student;
      } else if (timetableMode == CsvAPI.TIMETABLE_PROF) {
        this.url = CsvAPI.PARTIAL_PROF_TIMETABLE_URL + timetableId;
        timetableType = Timetable.Type.Professor;
      } else {
        this.url = null;
      }
    }

    private String getUrl() {
      return url;
    }
  }
}

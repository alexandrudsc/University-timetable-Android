package com.developer.alexandru.orarusv.download;


/**
 * Created by alexandru on 9/26/16.
 * Presenter for activity for downloading timetable
 */
public interface DownloadActivityPresenter {
    void initialize();
    void downloadButtonClicked();
    void webViewFinishedLoading();
    void tryConnectionAgain();
    void webViewReceiverErr(int errorCode);
    void downloadFinished();
}

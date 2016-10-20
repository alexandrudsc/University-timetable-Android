package com.developer.alexandru.orarusv.view_pager;

import android.os.AsyncTask;

import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.CsvAPI;
import com.developer.alexandru.orarusv.data.DialogListAdapter;
import com.developer.alexandru.orarusv.data.ParallelCoursesParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexandru on 9/16/16.
 * Async task downloader for alternative courses (same course, same prof, different timimg)
 */
public class ParallelCoursesLoader extends AsyncTask<Void, Void, Void> {

    private Course courseToReplace;
    private String profId;
    private DialogListAdapter adapter;

    public ParallelCoursesLoader(DialogListAdapter adapter, Course courseToReplace) {
        this.adapter = adapter;
        this.courseToReplace = courseToReplace;
        this.profId = courseToReplace.profID;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(CsvAPI.PROF_URL + this.profId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStreamReader is = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(is);

            ParallelCoursesParser parser = new ParallelCoursesParser(this.adapter, br, courseToReplace);
            parser.parse();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        adapter.notifyDataSetChanged();
    }
}

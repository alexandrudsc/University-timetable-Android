package com.developer.alexandru.orarusv.view_pager;

import android.os.AsyncTask;

import com.developer.alexandru.orarusv.data.AlternativeCoursesListAdapter;
import com.developer.alexandru.orarusv.data.AlternativeCoursesParser;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.CsvAPI;

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
public class AlternativeCoursesLoader extends AsyncTask<Void, Void, Void> {

    private Course courseToReplace;
    private String profId;
    private AlternativeCoursesListAdapter adapter;

    public AlternativeCoursesLoader(AlternativeCoursesListAdapter adapter, Course courseToReplace) {
        this.adapter = adapter;
        this.courseToReplace = courseToReplace;
        this.profId = courseToReplace.getProfID();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(CsvAPI.PARTIAL_PROF_TIMETABLE_URL + this.profId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStreamReader is = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(is);

            AlternativeCoursesParser parser = new AlternativeCoursesParser(this.adapter, br, courseToReplace);
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

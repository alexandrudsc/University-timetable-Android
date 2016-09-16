package com.developer.alexandru.orarusv.view_pager;

import android.app.Activity;
import android.os.AsyncTask;

import com.developer.alexandru.orarusv.data.CSVParser;
import com.developer.alexandru.orarusv.data.Course;
import com.developer.alexandru.orarusv.data.DialogListAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexandru on 9/16/16.
 */
public class ParallelCoursesLoader extends AsyncTask<Void, Void, Void> {

    private String profId;
    private Activity activity;
    private DialogListAdapter adapter;

    public ParallelCoursesLoader(Activity activity, DialogListAdapter adapter, String profId) {
        this.profId = profId;
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(1000);
            URL url = new URL(DialogListAdapter.PROF_URL + this.profId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStreamReader is = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(is);

            ParallelCoursesParser parser = new ParallelCoursesParser(br);
            parser.parse();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            conn.disconnect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class ParallelCoursesParser extends CSVParser {

        private Course c;

        public ParallelCoursesParser(BufferedReader br) {
            super(br);
        }

        @Override
        public boolean handleData(String[] data) {
            c = new Course();
            c.fullName = data[12];
            c.name = data[13];
            adapter.add(c);
            return true;
        }
    }
}

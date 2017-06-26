package com.developer.alexandru.orarusv;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.developer.alexandru.orarusv.data.DBAdapter;
import com.developer.alexandru.orarusv.data.Timetable;

import java.util.ArrayList;


public class ChooseTimetableActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayAdapter<Timetable> adapter;

    private int currentTimetableId;
    private int currentTimetableType;
    private String currentTimetableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_timetable);
        listView = (ListView)findViewById(R.id.timetables_list);
        listView.setOnItemClickListener(this);

        getCurrentTimetableDetails();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        TimetablesLoader timetablesLoader = new TimetablesLoader(adapter);
        timetablesLoader.execute(this);
    }

    private void getCurrentTimetableDetails() {
        Timetable currentTimetable = Utils.getCurrentTimetable(this);
        if (currentTimetable == null)
            currentTimetable = Timetable.Creator.create(new String[]{"", "", ""});
        this.currentTimetableId = currentTimetable.getId();
        this.currentTimetableType = currentTimetable.getType().ordinal();
        this.currentTimetableName = currentTimetable.getName();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Timetable timetable = adapter.getItem(i);
        if (timetable == null)
            return;
        Log.d("ChoooseTimetable", timetable.toString());
        if (this.currentTimetableId != timetable.getId()) {
            // TODO reset all progress because timetable changed?
            Utils.setCurrentTimetable(timetable, this);
            this.setResult(RESULT_OK);
        }
        this.finish();
    }

    private static class TimetablesLoader extends AsyncTask<Context, Void,  ArrayList<Timetable>>{

        private ArrayAdapter<Timetable> listAdapter;

        public TimetablesLoader(ArrayAdapter<Timetable> listAdapter) {
            this.listAdapter = listAdapter;
        }

        @Override
        protected  ArrayList<Timetable> doInBackground(Context... contexts) {
            DBAdapter adapter = new DBAdapter(contexts[0]);
            adapter.open();
            ArrayList<Timetable> list = adapter.getAllTimetables();
            adapter.close();
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Timetable> timetables) {
            for (Timetable timetable: timetables) {
                listAdapter.add(timetable);
            }
            listAdapter.notifyDataSetChanged();
        }
    }
}

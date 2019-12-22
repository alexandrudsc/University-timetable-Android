package com.developer.alexandru.orarusv;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.developer.alexandru.orarusv.data.DBAdapter;
import com.developer.alexandru.orarusv.data.Timetable;

import java.util.ArrayList;


public class ChooseTimetableActivity extends Activity implements AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {


    private ListView listView;
    private ArrayAdapter<Timetable> adapter;

    private int currentTimetableId;
    private int currentTimetableType;
    private String currentTimetableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_timetable);
        listView = findViewById(R.id.timetables_list);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        getCurrentTimetableDetails();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        TimetablesLoader timetablesLoader = new TimetablesLoader(adapter);
        timetablesLoader.execute(this);
    }

    private void getCurrentTimetableDetails() {
        Timetable currentTimetable = Utils.getCurrentTimetable(this);
        if (currentTimetable == null)
            currentTimetable = Timetable.Creator.INSTANCE.create(new String[]{"", "", ""});
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Timetable timetable = adapter.getItem(position);
        if(currentTimetableId == timetable.getId())
        {
            new AlertDialog.Builder(this)
                    .setTitle("Ștergere orar")
                    .setMessage("Orarul in uz nu poate fi șters")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.cancel())
                    .show();
            return true;
        }
        new AlertDialog.Builder(this)
                .setTitle("Ștergere orar?")
                .setMessage("")
                .setPositiveButton("Da", (dialog, which) -> {
                    TimetablesDeleter deleteTask = new TimetablesDeleter(adapter, timetable);
                    deleteTask.execute(ChooseTimetableActivity.this);
                })
                .setNegativeButton("Nu", (dialog, which) ->
                        dialog.cancel())
                .show();
        return true;
    }

    private static class TimetablesLoader extends AsyncTask<Context, Void,  ArrayList<Timetable>>{

        private ArrayAdapter<Timetable> listAdapter;

        TimetablesLoader(ArrayAdapter<Timetable> listAdapter) {
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

    /**
     * Background thread for deleting a timetable and all its courses
     */
    private static class TimetablesDeleter extends AsyncTask<Context, Void, Timetable>{

        private final Timetable timetable;
        private ArrayAdapter<Timetable> listAdapter;

        TimetablesDeleter(ArrayAdapter<Timetable> listAdapter, final Timetable timetable) {
            this.timetable = timetable;
            this.listAdapter = listAdapter;
        }


        @Override
        protected Timetable doInBackground(Context... contexts) {
            DBAdapter adapter = new DBAdapter(contexts[0]);
            adapter.open();
            adapter.deleteTimetable(timetable);
            adapter.close();
            return timetable;
        }

        @Override
        protected void onPostExecute(Timetable timetable) {
            Log.d("Deleted timetable", timetable.toString());
            listAdapter.remove(timetable);
            listAdapter.notifyDataSetChanged();
        }
    }
}

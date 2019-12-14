package com.developer.alexandru.orarusv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.developer.alexandru.orarusv.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexandru on 1/4/2015.
 */
public class HolidaysFragment extends ListFragment {
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE);
        int noHolidays = prefs.getInt(MainActivity.NUMBER_OF_HOLIDAYS, 0);
        ArrayList<Event> list = new ArrayList<>();
        for (int i = 0; i < noHolidays; i++) {
            Event e = Event.create(prefs.getString(MainActivity.HOLIDAY + "_" + i, "0-0"));
            if (e.name == null)
                e.name = MainActivity.HOLIDAY;
            list.add(e);
        }
        setListAdapter(new HolidaysListAdapter(getActivity(), R.id.name, R.layout.event_layout, list));

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //getListView().setOnItemLongClickListener(new LongClickListener());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_holidays, container, false);
    }

    private class LongClickListener implements AdapterView.OnItemLongClickListener
    {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            String name = "Vacanța de Crăciun";
            TextView tvDuration = (TextView)view.findViewById(R.id.duration);
            TextView tvName = (TextView)view.findViewById(R.id.name);
            prefs.edit().putString(MainActivity.HOLIDAY + "_" + position, prefs.getString(MainActivity.HOLIDAY+ "_" + position, "0-0-") +
                                                                            "-" + name).commit();
            tvName.setText(name);
            return true;
        }
    }

    private class HolidaysListAdapter extends ArrayAdapter<Event>
    {
        public HolidaysListAdapter(Context context, int resource, int textViewResourceId, List<Event> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.event_layout, parent, false);
            }
            Event ev = getItem(position);

            ((TextView)convertView.findViewById(R.id.name)).setText(ev.name);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            date.setTime(ev.start);
            String startDate, endDate;
            startDate = simpleDateFormat.format(date);
            date.setTime(ev.end);
            endDate = simpleDateFormat.format(date);
            ((TextView) convertView.findViewById(R.id.duration)).setText(startDate + " - " +endDate);

            return convertView;
        }
    }

    public static class Event
    {
        String name;
        long start, end;
        static Event create(String duration) {
            String[] splitted = duration.split("-");
            Event e = new Event();
            try {
                e.start = Long.valueOf(splitted[0]);
                e.end = Long.valueOf(splitted[1]);
                e.name = splitted[2];
            }catch (ArrayIndexOutOfBoundsException ex){

            }
            return e;
        }
    }

}

package com.developer.alexandru.aplicatie_studenti.action_bar;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.developer.alexandru.aplicatie_studenti.R;
import com.developer.alexandru.aplicatie_studenti.view_pager.MyListViewAdapter;

/**
 * Created by Alexandru on 6/1/14.
 */
public class MySpinnerAdapter implements SpinnerAdapter{

    private String[] values;

    public MySpinnerAdapter(Context context) {
        values = context.getResources().getStringArray(R.array.spinner_elements);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)parent.getRootView().getContext().
                                                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);


             convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);
             viewHolder = new ViewHolder();
             viewHolder.textView = (TextView)convertView.findViewById(R.id.spinner_elem_tv);

             convertView.setTag(viewHolder);

        }else
            viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.textView.setText(values[position]);
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)parent.getRootView().getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)convertView.findViewById(R.id.spinner_elem_tv);

            convertView.setTag(viewHolder);

        }else
            viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.textView.setText(values[position]);
        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    static class ViewHolder{
        TextView textView;
    }

}

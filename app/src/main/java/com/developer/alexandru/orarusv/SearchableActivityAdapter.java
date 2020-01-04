package com.developer.alexandru.orarusv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/** Created by Alexandru on 7/9/14. */
@Deprecated
public class SearchableActivityAdapter extends BaseAdapter {
  private ArrayList<String> values;
  private Context context;

  public SearchableActivityAdapter(ArrayList<String> vals, Context context) {
    this.values = vals;
    this.context = context;
  }

  @Override
  public int getCount() {
    return values.size();
  }

  @Override
  public Object getItem(int position) {
    return values.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    if (convertView == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.textView = convertView.findViewById(R.id.spinner_elem_tv);
      convertView.setTag(viewHolder);
    } else viewHolder = (ViewHolder) convertView.getTag();

    viewHolder.textView.setText(values.get(position));

    return convertView;
  }

  private class ViewHolder {
    TextView textView;
  }
}

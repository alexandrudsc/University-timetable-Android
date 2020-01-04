package com.developer.alexandru.orarusv.view_pager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.alexandru.orarusv.R;
import com.developer.alexandru.orarusv.data.Exam;

import java.util.List;

/** Adapter for exams fragment */
public class MyExamRecyclerViewAdapter
    extends RecyclerView.Adapter<MyExamRecyclerViewAdapter.ViewHolder> {

  private final List<Exam> mValues;
  private final OnExamFragmentInteractionListener mListener;

  public MyExamRecyclerViewAdapter(List<Exam> items, OnExamFragmentInteractionListener listener) {
    mValues = items;
    mListener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_exam, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.examTextView.setText(mValues.get(position).getName());

    holder.examTextView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (null != mListener) {
              mListener.onExamClicked(holder.mItem);
            }
          }
        });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView examTextView = null;
    public Exam mItem;

    public ViewHolder(View view) {
      super(view);
      examTextView = view.findViewById(R.id.examTextView);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + examTextView.getText() + "'";
    }
  }

  public interface OnExamFragmentInteractionListener {
    void onExamClicked(Exam exam);

    void onExamAdd();
  }
}

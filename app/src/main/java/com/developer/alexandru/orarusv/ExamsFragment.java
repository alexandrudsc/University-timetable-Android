package com.developer.alexandru.orarusv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.alexandru.orarusv.data.Exam;
import com.developer.alexandru.orarusv.view_pager.MyExamRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A fragment representing a list of exams
 */
@Deprecated
public class ExamsFragment extends Fragment implements MyExamRecyclerViewAdapter.OnExamFragmentInteractionListener {

    private String examsFilename;
    private MyExamRecyclerViewAdapter.OnExamFragmentInteractionListener mListener;
    private final static String FILENAME = "EXAMS_FILENAME";

    public ExamsFragment() {
    }

    public static ExamsFragment newInstance(String examsFilename) {
        ExamsFragment fragment = new ExamsFragment();
        Bundle args = new Bundle();
        args.putString(FILENAME, examsFilename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.examsFilename = getArguments().getString(FILENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(android.R.layout.simple_list_item_1, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            ArrayList<Exam> exams = getExamsFromPrefs(context);
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(new MyExamRecyclerViewAdapter(exams, mListener));
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onExamClicked(Exam exam) {
        deleteExam(exam);
    }

    @Override
    public void onExamAdd() {
        addExam();
    }

    private ArrayList<Exam> getExamsFromPrefs(Context context) {
        if (examsFilename == null)
            return null;
        ArrayList<Exam> exams = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(examsFilename, Context.MODE_PRIVATE);
        int exam_no = prefs.getInt("exam_no", 0);
        for (int i = 0; i < exam_no; i++) {
            String examName = prefs.getString("exam_" + i, null);
            if (examName != null){
                Exam exam = new Exam();
                exam.setName(examName);
                exams.add(exam);
            }
        }
        return exams;
    }

    private void deleteExam(Exam exam) {

    }

    private void addExam(){

    }
}

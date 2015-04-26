package com.developer.alexandru.aplicatie_studenti;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import com.developer.alexandru.aplicatie_studenti.data.Course;
import com.developer.alexandru.aplicatie_studenti.data.DBAdapter;

import java.util.HashMap;

/**
 * Created by Alexandru on 7/4/14.
 * NOT USED!
 */
public class SearchableActivity extends FragmentActivity {//extends ListActivity {


    private Course course = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        String name = null, type = null, info = null;
        String[] typeAndName;
        if(intent.getAction().equals(Intent.ACTION_SEARCH)){
            typeAndName  = intent.getStringExtra(SearchManager.QUERY).split(" ");
            try {
                type = typeAndName[0];
                name = typeAndName[1];
            }catch (ArrayIndexOutOfBoundsException e){
            }
        }
        else
            if(intent.getAction().equals(Intent.ACTION_VIEW) && bundle != null){
                info = bundle.getString(SearchManager.EXTRA_DATA_KEY);
                typeAndName = intent.getData().toString().split("/");
                name = typeAndName[1];
                type = typeAndName[0];

            }else
                if(intent.getAction().equals(SearchableFragment.actionViewDetails)){
                    course = intent.getExtras().getParcelable(SearchableFragment.EXTRA_COURSE_KEY);
                    Toast.makeText(this, course.name + "_" + course.type, Toast.LENGTH_LONG).show();
                }

        if(course == null){
            Toast.makeText(this, name + "_" + type, Toast.LENGTH_LONG).show();
            AbsPres absPres = SearchableFragment.getResults(this, name, type, info);
            DBAdapter dbAdapter = new DBAdapter(this);
            dbAdapter.open();
            course = dbAdapter.getCourse(name, type);
        }
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(new SearchableFragment(course, getSupportFragmentManager()).
                onCreateView((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE), linearLayout, null));

        setContentView(linearLayout);

        /*//this.setListAdapter(createAdapter(absPres));
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        TableLayout tableLayout = createTable(absPres);
        scrollView.addView(tableLayout);
        setContentView(scrollView);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchable_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_done:
                this.finish();
        }
        return true;
    }



    private TableLayout createTable(AbsPres absPres){

        int total = absPres.absences + absPres.presences;
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                                                                   TableLayout.LayoutParams.MATCH_PARENT);
        tableLayoutParams.setMargins(1, 1, 1, 1);

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setLayoutParams(tableLayoutParams);
        if(absPres == null)
            return tableLayout;

        TableRow.LayoutParams tableRowTitleParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tableRowTitleParams.weight = 1;
        tableRowTitleParams.span = 3;

        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tableRowParams.weight = 1;

        TableRow tableRow = new TableRow(this);
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setPadding(1, 1, 1, 1);
        tableRow.setLayoutParams(tableRowTitleParams);

        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(2, 2, 2, 2);
        tv.setText("Total: " + total);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        tableRow.addView( tv);
        tableLayout.addView(tableRow);

        tableRow = new TableRow(this);
        tableRow.setLayoutParams(tableRowParams);
        for(int i = 1; i <= 3; i++){
            tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setPadding(1, 1, 1, 1);
            tv.setBackgroundResource(R.drawable.cell_shape);
            switch (i){
                case 1:
                    break;
                case 2:
                    tv.setText(absPres.presences + " prezențe ");
                    break;
                case 3:
                    tv.setText(absPres.absences + " absențe");
                    break;
            }
            tableRow.addView(tv);
        }
        tableLayout.addView(tableRow);

        for(int i = 1; i <= MainActivity.WEEKS_IN_SEMESTER; i++){

            Boolean wasPresent = absPres.table.get(i);
            if(wasPresent != null){
                tableRow = new TableRow(this);
                tableRow.setLayoutParams(tableRowParams);
                tableRow.setPadding(1, 1, 1, 1);

                for(int j = 1; j <=3; j++){
                    tv = new TextView(this);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    tv.setPadding(1, 1, 1, 1);
                    tv.setBackgroundResource(R.drawable.cell_shape);
                    switch (j){
                          case 1:
                               tv.setText("Săptămâna "+ i);
                               break;
                          case 2:
                               if( wasPresent )
                                    tv.setText("X");
                               break;
                          case 3:
                                if(!wasPresent )
                                    tv.setText("X");
                                break;
                            }
                    tableRow.addView(tv);
                    }
                tableLayout.addView(tableRow);
            }
        }

        return tableLayout;
    }

    public static class AbsPres{
        public int absences;
        public int presences;
        HashMap<Integer, Boolean> table = new HashMap<Integer, Boolean>();
    }

}

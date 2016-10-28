[1mdiff --git a/.idea/misc.xml b/.idea/misc.xml[m
[1mindex 5d19981..fbb6828 100644[m
[1m--- a/.idea/misc.xml[m
[1m+++ b/.idea/misc.xml[m
[36m@@ -37,7 +37,7 @@[m
     <ConfirmationsSetting value="0" id="Add" />[m
     <ConfirmationsSetting value="0" id="Remove" />[m
   </component>[m
[31m-  <component name="ProjectRootManager" version="2" languageLevel="JDK_1_7" default="true" assert-keyword="true" jdk-15="true" project-jdk-name="1.8" project-jdk-type="JavaSDK">[m
[32m+[m[32m  <component name="ProjectRootManager" version="2" languageLevel="JDK_1_8" default="true" assert-keyword="true" jdk-15="true" project-jdk-name="1.8" project-jdk-type="JavaSDK">[m
     <output url="file://$PROJECT_DIR$/build/classes" />[m
   </component>[m
   <component name="ProjectType">[m
[1mdiff --git a/app/app.iml b/app/app.iml[m
[1mindex 0db0732..d5e9878 100644[m
[1m--- a/app/app.iml[m
[1m+++ b/app/app.iml[m
[36m@@ -64,14 +64,6 @@[m
       <sourceFolder url="file://$MODULE_DIR$/src/main/jni" isTestSource="false" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/main/rs" isTestSource="false" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/main/shaders" isTestSource="false" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/res" type="java-test-resource" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/resources" type="java-test-resource" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/assets" type="java-test-resource" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/aidl" isTestSource="true" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/java" isTestSource="true" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/jni" isTestSource="true" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/rs" isTestSource="true" />[m
[31m-      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/shaders" isTestSource="true" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/test/res" type="java-test-resource" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/test/resources" type="java-test-resource" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/test/assets" type="java-test-resource" />[m
[36m@@ -80,6 +72,14 @@[m
       <sourceFolder url="file://$MODULE_DIR$/src/test/jni" isTestSource="true" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/test/rs" isTestSource="true" />[m
       <sourceFolder url="file://$MODULE_DIR$/src/test/shaders" isTestSource="true" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/res" type="java-test-resource" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/resources" type="java-test-resource" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/assets" type="java-test-resource" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/aidl" isTestSource="true" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/java" isTestSource="true" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/jni" isTestSource="true" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/rs" isTestSource="true" />[m
[32m+[m[32m      <sourceFolder url="file://$MODULE_DIR$/src/androidTest/shaders" isTestSource="true" />[m
       <excludeFolder url="file://$MODULE_DIR$/build/intermediates/assets" />[m
       <excludeFolder url="file://$MODULE_DIR$/build/intermediates/blame" />[m
       <excludeFolder url="file://$MODULE_DIR$/build/intermediates/builds" />[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/MainActivity.java b/app/src/main/java/com/developer/alexandru/orarusv/MainActivity.java[m
[1mindex 7e772cd..351ce32 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/MainActivity.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/MainActivity.java[m
[36m@@ -339,6 +339,4 @@[m [mpublic class MainActivity extends ActionBarActivity[m
 [m
         return true;[m
     }[m
[31m-[m
[31m-[m
 }[m
\ No newline at end of file[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/ResultsFragment.java b/app/src/main/java/com/developer/alexandru/orarusv/ResultsFragment.java[m
[1mindex aa5b1e9..19efc5a 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/ResultsFragment.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/ResultsFragment.java[m
[36m@@ -14,7 +14,7 @@[m [mimport android.view.View;[m
 import android.view.ViewGroup;[m
 import android.widget.*;[m
 import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekActivity;[m
[31m-import com.developer.alexandru.orarusv.view_pager.ViewPagerAdapter;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.view_pager.TimetableViewPagerAdapter;[m
 [m
 import java.util.HashMap;[m
 [m
[36m@@ -107,7 +107,7 @@[m [mpublic class ResultsFragment extends Fragment {[m
         AbsPres result= new AbsPres();[m
         result.absences = result.presences = 0;[m
         int i;[m
[31m-        if(info.equals(ViewPagerAdapter.COURSES_IN_EVEN_WEEK)){[m
[32m+[m[32m        if(info.equals(TimetableViewPagerAdapter.COURSES_IN_EVEN_WEEK)){[m
             for(i = 2; i <= MainActivity.WEEKS_IN_SEMESTER; i+=2){[m
                 boolean wasPresent = false;[m
                 if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,[m
[36m@@ -121,7 +121,7 @@[m [mpublic class ResultsFragment extends Fragment {[m
 [m
                 result.table.put(i, wasPresent);[m
             }[m
[31m-        }else if(info.equals(ViewPagerAdapter.COURSES_IN_ODD_WEEK)){[m
[32m+[m[32m        }else if(info.equals(TimetableViewPagerAdapter.COURSES_IN_ODD_WEEK)){[m
             for(i = 1; i <= MainActivity.WEEKS_IN_SEMESTER; i+=2){[m
                 boolean wasPresent = false;[m
                 if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/SearchableFragment.java b/app/src/main/java/com/developer/alexandru/orarusv/SearchableFragment.java[m
[1mindex 8e3c22b..1496263 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/SearchableFragment.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/SearchableFragment.java[m
[36m@@ -7,7 +7,6 @@[m [mimport android.support.v4.app.Fragment;[m
 import android.support.v4.app.FragmentManager;[m
 import android.support.v4.app.FragmentTransaction;[m
 import android.support.v4.view.MenuItemCompat;[m
[31m-import android.support.v7.app.ActionBarActivity;[m
 import android.support.v7.widget.Toolbar;[m
 import android.util.Log;[m
 import android.view.LayoutInflater;[m
[36m@@ -20,7 +19,7 @@[m [mimport android.widget.FrameLayout;[m
 import android.widget.TextView;[m
 import com.developer.alexandru.orarusv.action_bar.NonCurrentWeekActivity;[m
 import com.developer.alexandru.orarusv.data.Course;[m
[31m-import com.developer.alexandru.orarusv.view_pager.ViewPagerAdapter;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.view_pager.TimetableViewPagerAdapter;[m
 [m
 /**[m
  * Created by Alexandru on 7/14/14.[m
[36m@@ -217,7 +216,7 @@[m [mpublic class SearchableFragment extends  Fragment{[m
         SearchableActivity.AbsPres result= new SearchableActivity.AbsPres();[m
         result.absences = result.presences = 0;[m
         int i;[m
[31m-        if(info.equals(ViewPagerAdapter.COURSES_IN_EVEN_WEEK)){[m
[32m+[m[32m        if(info.equals(TimetableViewPagerAdapter.COURSES_IN_EVEN_WEEK)){[m
             for(i = 2; i <= MainActivity.WEEKS_IN_SEMESTER; i+=2){[m
                 boolean wasPresent = false;[m
                 if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,[m
[36m@@ -230,7 +229,7 @@[m [mpublic class SearchableFragment extends  Fragment{[m
 [m
                 result.table.put(i, wasPresent);[m
             }[m
[31m-        } else if(info.equals(ViewPagerAdapter.COURSES_IN_ODD_WEEK)){[m
[32m+[m[32m        } else if(info.equals(TimetableViewPagerAdapter.COURSES_IN_ODD_WEEK)){[m
             for(i = 1; i <= MainActivity.WEEKS_IN_SEMESTER; i+=2){[m
                 boolean wasPresent = false;[m
                 if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/TimetableFragment.java b/app/src/main/java/com/developer/alexandru/orarusv/TimetableFragment.java[m
[1mindex e747419..97ee4a3 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/TimetableFragment.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/TimetableFragment.java[m
[36m@@ -15,12 +15,9 @@[m [mimport android.view.MenuItem;[m
 import android.view.View;[m
 import android.view.ViewGroup;[m
 [m
[31m-import com.developer.alexandru.orarusv.action_bar.MySpinnerAdapter;[m
 import com.developer.alexandru.orarusv.data.Course;[m
[31m-import com.developer.alexandru.orarusv.data.DataLoader;[m
[31m-import com.developer.alexandru.orarusv.view_pager.DayFragment;[m
 import com.developer.alexandru.orarusv.view_pager.PagerSlidingTabStrip;[m
[31m-import com.developer.alexandru.orarusv.view_pager.ViewPagerAdapter;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.view_pager.TimetableViewPagerAdapter;[m
 [m
 import java.util.Calendar;[m
 [m
[36m@@ -37,7 +34,7 @@[m [mpublic class TimetableFragment extends Fragment {[m
     private static final String TAG = "TimetableFragment";[m
 [m
     public ViewPager viewPager;[m
[31m-    private ViewPagerAdapter viewPagerAdapter;[m
[32m+[m[32m    private TimetableViewPagerAdapter timetableViewPagerAdapter;[m
     public PagerSlidingTabStrip pagerSlidingTabStrip;[m
 [m
     private OnCourseSelected onCourseSelected;[m
[36m@@ -91,7 +88,7 @@[m [mpublic class TimetableFragment extends Fragment {[m
             calendar = Calendar.getInstance();[m
         // Create here the view pager adapter so I can call getActivity() on a DayFragment anytime during it's lifecycle[m
         viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);[m
[31m-        viewPager.setAdapter(new ViewPagerAdapter((MainActivity)getActivity(), getChildFragmentManager()));[m
[32m+[m[32m        viewPager.setAdapter(new TimetableViewPagerAdapter((MainActivity)getActivity(), getChildFragmentManager()));[m
         pagerSlidingTabStrip = (PagerSlidingTabStrip) fragmentView.findViewById(R.id.sliding_tabs);[m
         pagerSlidingTabStrip.setViewPager(viewPager);[m
         viewPager.setCurrentItem(calendar.get(Calendar.DAY_OF_WEEK) - 1);[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/Utils.java b/app/src/main/java/com/developer/alexandru/orarusv/Utils.java[m
[1mindex fab0be1..a6b4877 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/Utils.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/Utils.java[m
[36m@@ -96,7 +96,7 @@[m [mpublic class Utils {[m
 [m
         if(sharedPreferences.getInt(MainActivity.WEEK_OF_SEMESTER, -1) !=  currentWeek){[m
             // If the week is changed invalidate the old data set.[m
[31m-//            ViewPagerAdapter.listsOfCourses = null;[m
[32m+[m[32m//            TimetableViewPagerAdapter.listsOfCourses = null;[m
             SharedPreferences.Editor editor = sharedPreferences.edit();[m
             editor.putInt(MainActivity.WEEK_OF_SEMESTER, currentWeek);[m
             editor.commit();[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/action_bar/ListViewAdapterNonCurWeek.java b/app/src/main/java/com/developer/alexandru/orarusv/action_bar/ListViewAdapterNonCurWeek.java[m
[1mindex dc6bd19..20fec76 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/action_bar/ListViewAdapterNonCurWeek.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/action_bar/ListViewAdapterNonCurWeek.java[m
[36m@@ -17,7 +17,7 @@[m [mimport com.developer.alexandru.orarusv.main.MainActivity;[m
 import com.developer.alexandru.orarusv.R;[m
 import com.developer.alexandru.orarusv.data.DBAdapter;[m
 import com.developer.alexandru.orarusv.data.Course;[m
[31m-import com.developer.alexandru.orarusv.view_pager.MyListViewAdapter;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.view_pager.DayListViewAdapter;[m
 [m
 import java.util.ArrayList;[m
 [m
[36m@@ -81,17 +81,17 @@[m [mpublic class ListViewAdapterNonCurWeek extends BaseAdapter {[m
 [m
     @Override[m
     public View getView(int position, View convertView, ViewGroup parent) {[m
[31m-        MyListViewAdapter.ViewHolder viewHolder;[m
[32m+[m[32m        DayListViewAdapter.ViewHolder viewHolder;[m
         if(convertView == null){[m
             LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);[m
             if(isTitleAt(position)){[m
                 convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);[m
[31m-                viewHolder = new MyListViewAdapter.ViewHolder();[m
[32m+[m[32m                viewHolder = new DayListViewAdapter.ViewHolder();[m
                 viewHolder.eventName = (TextView) convertView.findViewById(R.id.spinner_elem_tv);[m
                 convertView.setTag(viewHolder);[m
             }else{[m
                 convertView = inflater.inflate(R.layout.course_item_layout, parent, false);[m
[31m-                viewHolder = new MyListViewAdapter.ViewHolder();[m
[32m+[m[32m                viewHolder = new DayListViewAdapter.ViewHolder();[m
                 viewHolder.eventName = (TextView)convertView.findViewById(R.id.course_name);[m
                 viewHolder.eventType = (TextView)convertView.findViewById(R.id.course_description);[m
 [m
[36m@@ -99,7 +99,7 @@[m [mpublic class ListViewAdapterNonCurWeek extends BaseAdapter {[m
                 convertView.setTag(viewHolder);[m
             }[m
         }else[m
[31m-            viewHolder = (MyListViewAdapter.ViewHolder) convertView.getTag();[m
[32m+[m[32m            viewHolder = (DayListViewAdapter.ViewHolder) convertView.getTag();[m
 [m
         if(isTitleAt(position)){[m
             viewHolder.eventName.setText(values.get(position).name);[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/data/DBAdapter.java b/app/src/main/java/com/developer/alexandru/orarusv/data/DBAdapter.java[m
[1mindex e09a948..d90f180 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/data/DBAdapter.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/data/DBAdapter.java[m
[36m@@ -8,7 +8,7 @@[m [mimport android.database.sqlite.SQLiteException;[m
 import android.database.sqlite.SQLiteOpenHelper;[m
 import android.util.Log;[m
 [m
[31m-import com.developer.alexandru.orarusv.view_pager.ViewPagerAdapter;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.view_pager.TimetableViewPagerAdapter;[m
 [m
 import java.util.ArrayList;[m
 [m
[36m@@ -282,9 +282,9 @@[m [mpublic class DBAdapter {[m
         selectionArgs[0] = String.valueOf(day);[m
         selectionArgs[2] = "-";[m
         if (week % 2 == 0)[m
[31m-            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_EVEN_WEEK;[m
[32m+[m[32m            selectionArgs[1] = TimetableViewPagerAdapter.COURSES_IN_EVEN_WEEK;[m
         else[m
[31m-            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_ODD_WEEK;[m
[32m+[m[32m            selectionArgs[1] = TimetableViewPagerAdapter.COURSES_IN_ODD_WEEK;[m
         selectionArgs[3] = "10 sapt.+1h";[m
         Cursor cursor = null;[m
         cursor = database.query(SQLStmtHelper.COURSES_TABLE,[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/data/DayCoursesLoader.java b/app/src/main/java/com/developer/alexandru/orarusv/data/DayCoursesLoader.java[m
[1mindex 876cdb1..d7855e9 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/data/DayCoursesLoader.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/data/DayCoursesLoader.java[m
[36m@@ -9,7 +9,7 @@[m [mimport android.support.v4.app.FragmentActivity;[m
 [m
 import com.developer.alexandru.orarusv.main.MainActivity;[m
 import com.developer.alexandru.orarusv.view_pager.DayFragment;[m
[31m-import com.developer.alexandru.orarusv.view_pager.MyListViewAdapter;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.view_pager.DayListViewAdapter;[m
 [m
 import java.util.ArrayList;[m
 [m
[36m@@ -55,7 +55,7 @@[m [mpublic class DayCoursesLoader extends AsyncTask<Integer, Void, ArrayList<Course>[m
         currentWeek = timeOrganiser.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);[m
         if(activity != null){[m
             try {[m
[31m-                //ViewPagerAdapter.context = activity;[m
[32m+[m[32m                //TimetableViewPagerAdapter.context = activity;[m
 [m
                 //Open the connection with the local database[m
 [m
[36m@@ -79,7 +79,7 @@[m [mpublic class DayCoursesLoader extends AsyncTask<Integer, Void, ArrayList<Course>[m
     @Override[m
     protected void onPostExecute(ArrayList<Course> courses) {[m
         super.onPostExecute(courses);[m
[31m-        MyListViewAdapter listAdapter = (MyListViewAdapter) fragment.getListAdapter();[m
[32m+[m[32m        DayListViewAdapter listAdapter = (DayListViewAdapter) fragment.getListAdapter();[m
         listAdapter.setValues(courses);[m
         listAdapter.notifyDataSetChanged();[m
     }[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/data/TimetableDownloaderTask.java b/app/src/main/java/com/developer/alexandru/orarusv/data/TimetableDownloaderTask.java[m
[1mindex d4047e1..b6eb697 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/data/TimetableDownloaderTask.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/data/TimetableDownloaderTask.java[m
[36m@@ -90,7 +90,7 @@[m [mpublic class TimetableDownloaderTask extends AsyncTask <String, Void, Void> {[m
             conn.disconnect();[m
             if (parser.wasSuccessful()) {[m
                 dbAdapter.replaceOldCourses();[m
[31m-//                ViewPagerAdapter.listsOfCourses = null;[m
[32m+[m[32m//                TimetableViewPagerAdapter.listsOfCourses = null;[m
             } else {[m
                 success = false;[m
                 dbAdapter.deleteTMPCourses();[m
[36m@@ -102,111 +102,6 @@[m [mpublic class TimetableDownloaderTask extends AsyncTask <String, Void, Void> {[m
         }[m
 [m
         dbAdapter.close();[m
[31m-        /*SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,[m
[31m-                Context.MODE_PRIVATE);[m
[31m-        SharedPreferences timeOrganiser = context.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,[m
[31m-                Context.MODE_PRIVATE);[m
[31m-        final int currentWeek = timeOrganiser.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);[m
[31m-        try {[m
[31m-            URL url = urls[0];[m
[31m-            HttpURLConnection connection = (HttpURLConnection) url.openConnection();[m
[31m-            connection.connect();[m
[31m-[m
[31m-            InputStream inputStream = connection.getInputStream();[m
[31m-[m
[31m-[m
[31m-            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));[m
[31m-[m
[31m-            //ViewPagerAdapter.context = context;[m
[31m-            //Create the local database and open a connection[m
[31m-            DBAdapter dbAdapter = new DBAdapter(context);[m
[31m-            dbAdapter.open();[m
[31m-            dbAdapter.deleteCourses();[m
[31m-            dbAdapter.create();[m
[31m-            StringBuilder stringBuilder = new StringBuilder();[m
[31m-            JSONObject jsonObject;[m
[31m-            String line;[m
[31m-[m
[31m-            JSONArray day = null;[m
[31m-            ArrayList<Course> courses;[m
[31m-            //Build the string from the JSON response[m
[31m-            while ((line = bufferedReader.readLine()) != null) {[m
[31m-                stringBuilder.append(line);[m
[31m-            }[m
[31m-            inputStream.close();[m
[31m-            jsonObject = new JSONObject(stringBuilder.toString());[m
[31m-            jsonArray = jsonObject.getJSONArray(ViewPagerAdapter.NAME_OF_DAYS);[m
[31m-            Log.d("DOWNLOADER", stringBuilder.toString());[m
[31m-            //Save start and end date.Save holidays' dates.[m
[31m-[m
[31m-            if (timeOrganiser.getLong(MainActivity.START_DATE, 0) == 0) {[m
[31m-                SharedPreferences.Editor editor = timeOrganiser.edit();[m
[31m-                JSONObject jsonTimeOrganiser = jsonObject.getJSONObject(ViewPagerAdapter.NAME_OF_SEMESTER_ORG);[m
[31m-[m
[31m-                //Update the start and the finish date[m
[31m-                editor.putLong(MainActivity.START_DATE,[m
[31m-                        Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_START_DATE).toString()));[m
[31m-                editor.putLong(MainActivity.END_DATE,[m
[31m-                        Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_END_DATE).toString()));[m
[31m-[m
[31m-                //Update the holidays[m
[31m-                JSONArray jsonHolidays = jsonTimeOrganiser.getJSONArray(ViewPagerAdapter.NAME_OF_HOLIDAYS);[m
[31m-                JSONObject holiday;[m
[31m-                int numberOfHolidays = jsonHolidays.length();[m
[31m-                editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);[m
[31m-                for (int i = 0; i < numberOfHolidays; i++) {[m
[31m-                    holiday = jsonHolidays.getJSONObject(i);[m
[31m-                    editor.putString(MainActivity.HOLIDAY + "_" + i,[m
[31m-                            holiday.get(ViewPagerAdapter.NAME_OF_START_DATE).toString() + "-"[m
[31m-                                    + holiday.get(ViewPagerAdapter.NAME_OF_END_DATE).toString());[m
[31m-                }[m
[31m-                editor.commit();[m
[31m-[m
[31m-            }[m
[31m-[m
[31m-            ViewPagerAdapter.listsOfCourses = new ArrayList[ViewPagerAdapter.NUM_DAYS];[m
[31m-[m
[31m-            for (int i = 0; i < ViewPagerAdapter.NUM_DAYS; i++) {[m
[31m-                try {[m
[31m-                    day = jsonArray.getJSONObject(i).getJSONArray(ViewPagerAdapter.days[i]);[m
[31m-                } catch (JSONException e) {[m
[31m-                    e.printStackTrace();[m
[31m-                }[m
[31m-[m
[31m-                if (day != null) {[m
[31m-                    //Parse the day object from the JSON response into an array[m
[31m-                    //courses = ViewPagerAdapter.getCoursesFromJSONArray(day);[m
[31m-                    courses = null;[m
[31m-                    //Add the courses into the database and also select the ones for the current wwek[m
[31m-                    ArrayList<Course> coursesForToday = new ArrayList<Course>();[m
[31m-                    for (int j = 0; j < courses.size(); j++) {[m
[31m-                        Course c = courses.get(j);[m
[31m-                        //Add the course to the database[m
[31m-                        dbAdapter.insertCourse(c, ViewPagerAdapter.days[i]);[m
[31m-                        //If is in the current week select it for immediate displaying[m
[31m-                        if (DataLoader.isCourseInWeek(currentWeek, c))[m
[31m-                            coursesForToday.add(c);[m
[31m-                    }[m
[31m-                    //Add the data for the current wee to a static array[m
[31m-                    ViewPagerAdapter.listsOfCourses[i] = coursesForToday;[m
[31m-                    if (D) Log.d(TAG, day.toString());[m
[31m-                } else if (D) Log.d(TAG, ViewPagerAdapter.days[i] + " null");[m
[31m-            }[m
[31m-            success = true;[m
[31m-            dbAdapter.close();[m
[31m-[m
[31m-            preferences.edit().putBoolean(MainActivity.FIRST_RUN, false).commit();[m
[31m-[m
[31m-            // The list view adapter for every day of current week uses the current week value.[m
[31m-            //Must be set now.Also the remote views adapter uses it.[m
[31m-            Utils.setCurrentWeek(context);[m
[31m-[m
[31m-            connection.disconnect();[m
[31m-        } catch (IOException e) {[m
[31m-[m
[31m-        } catch (JSONException e) {[m
[31m-            e.printStackTrace();[m
[31m-        }*/[m
         return null;[m
     }[m
 [m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/view_pager/DayFragment.java b/app/src/main/java/com/developer/alexandru/orarusv/view_pager/DayFragment.java[m
[1mindex fc0029c..f331ee4 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/view_pager/DayFragment.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/view_pager/DayFragment.java[m
[36m@@ -34,7 +34,7 @@[m [mpublic class DayFragment extends ListFragment {[m
     //public static TimetableFragment.OnCourseSelected onCourseSelected;[m
     private WeakReference<DayCoursesLoader> taskLoaderReference;[m
     private ArrayList<Course> list;[m
[31m-    private MyListViewAdapter adapter;[m
[32m+[m[32m    private DayListViewAdapter adapter;[m
     DayCoursesLoader dayCoursesLoader;[m
 [m
 [m
[36m@@ -76,7 +76,7 @@[m [mpublic class DayFragment extends ListFragment {[m
             week = args.getInt("week");[m
             if (list == null)[m
                 list = new ArrayList<>();[m
[31m-            adapter = new MyListViewAdapter((MainActivity)getActivity(), list);[m
[32m+[m[32m            adapter = new DayListViewAdapter((MainActivity)getActivity(), list);[m
             setListAdapter(adapter);[m
             taskLoaderReference = new WeakReference<>(new DayCoursesLoader(getActivity(), this));   // getActivity() will be not null[m
             //taskLoaderReference.get().execute(day, 8);                                       // see TimetableFragment[m
[36m@@ -125,9 +125,9 @@[m [mpublic class DayFragment extends ListFragment {[m
                 if (D) Log.d(TAG, "onCreateView " + "day " + day + " from previous");[m
                 list = savedInstanceState.getParcelableArrayList("courses");[m
                 if (adapter == null)[m
[31m-                    adapter = ((MyListViewAdapter)getListAdapter());[m
[32m+[m[32m                    adapter = ((DayListViewAdapter)getListAdapter());[m
                 if (adapter == null) {[m
[31m-                    adapter = new MyListViewAdapter((MainActivity) getActivity(), list);[m
[32m+[m[32m                    adapter = new DayListViewAdapter((MainActivity) getActivity(), list);[m
                     setListAdapter(adapter);[m
                 } else {[m
                     if (D) Log.d(TAG, "size " + list.size() );[m
[36m@@ -182,7 +182,7 @@[m [mpublic class DayFragment extends ListFragment {[m
         outState.setClassLoader(Course.class.getClassLoader());[m
         outState.putInt("day", day);[m
         outState.putInt("week", week);[m
[31m-        outState.putParcelableArrayList ("courses", ((MyListViewAdapter) getListAdapter()).getValues());[m
[32m+[m[32m        outState.putParcelableArrayList ("courses", ((DayListViewAdapter) getListAdapter()).getValues());[m
         super.onSaveInstanceState(outState);[m
         if (D) Log.d(TAG, "SAVE");[m
     }[m
[1mdiff --git a/app/src/main/java/com/developer/alexandru/orarusv/view_pager/OnCourseLongClickListener.java b/app/src/main/java/com/developer/alexandru/orarusv/view_pager/OnCourseLongClickListener.java[m
[1mindex 0a8f28b..a139a73 100644[m
[1m--- a/app/src/main/java/com/developer/alexandru/orarusv/view_pager/OnCourseLongClickListener.java[m
[1m+++ b/app/src/main/java/com/developer/alexandru/orarusv/view_pager/OnCourseLongClickListener.java[m
[36m@@ -8,8 +8,8 @@[m [mimport android.view.View;[m
 import com.developer.alexandru.orarusv.R;[m
 import com.developer.alexandru.orarusv.main.TimetableFragment.OnCourseSelected;[m
 import com.developer.alexandru.orarusv.Utils;[m
[32m+[m[32mimport com.developer.alexandru.orarusv.data.AlternativeCoursesListAdapter;[m
 import com.developer.alexandru.orarusv.data.Course;[m
[31m-import com.developer.alexandru.orarusv.data.DialogListAdapter;[m
 [m
 import java.util.ArrayList;[m
 [m
[36m@@ -34,7 +34,7 @@[m [mpublic class OnCourseLongClickListener implements View.OnLongClickListener {[m
         }[m
         ArrayList<Course> items = new ArrayList<>();[m
 [m
[31m-        DialogListAdapter adapter = new DialogListAdapter(courseSelectedCallback.getActivity(), R.layout.simple_course_layout, items);[m
[32m+[m[32m        AlternativeCoursesListAdapter adapter = new AlternativeCoursesListAdapter(courseSelectedCallback.getActivity(), R.layout.simple_course_layout, items);[m
         // Builder for the dialog displayed at ItemLongClick[m
         AlertDialog.Builder builder = new AlertDialog.Builder(courseSelectedCallback.getActivity());[m
         builder.setTitle(R.string.choose)[m
[36m@@ -48,8 +48,8 @@[m [mpublic class OnCourseLongClickListener implements View.OnLongClickListener {[m
         adapter.setDialog(dialog);[m
         dialog.show();[m
 [m
[31m-        ParallelCoursesLoader parallelCoursesLoader = new ParallelCoursesLoader(adapter, this.course);[m
[31m-        parallelCoursesLoader.execute();[m
[32m+[m[32m        AlternativeCoursesLoader alternativeCoursesLoader = new AlternativeCoursesLoader(adapter, this.course);[m
[32m+[m[32m        alternativeCoursesLoader.execute();[m
         return false;[m
     }[m
 }[m

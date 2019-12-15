package com.developer.alexandru.orarusv.data;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.developer.alexandru.orarusv.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Alexandru on 12/23/2014.
 * Fetches the faculties from the URL and their IDs storing in a local database
 */
@Deprecated
public class Synchronizer extends IntentService {

    //Debug
    private static final boolean D = true;
    public static final String TAG = "Synchronizer";

    public static final String ACTION_SYNC_FINISHED =  "synchronization finished";

    private final String URL_FACULTIES = CsvAPI.USV_ENDPOINT + "vizualizare/intro1.php";
    private final String URL_GROUPS = CsvAPI.USV_ENDPOINT + "vizualizare/data/subgrupe.php";
    private final String URL_FACULTIES2 = CsvAPI.USV_ENDPOINT + "vizualizare/data/facultati.php";
    private final String PARTIAL_URL_FACULTY =
            CsvAPI.USV_ENDPOINT + "vizualizare/orarUp2.php?facultateID=";

    private DBAdapter dbAdapter;

    private NotificationManager notificationManager;
    private static final int REFRESH_NOTIF_CODE = 1;

    private Intent syncFinished;

    String generalName = "";                        //Some faculties have a general prefixed name (like the "Pedagogic department")

    public Synchronizer() {
        super("Synchronizer");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (D) Log.d(TAG, "start");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        syncFinished = new Intent();
        syncFinished.setAction(ACTION_SYNC_FINISHED);

        //Form a GET request for the time organization
        HttpGet request = new HttpGet(URL_FACULTIES2);
        //Default HTTP client
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(request);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            showNotification();


            dbAdapter = new DBAdapter(this);
            dbAdapter.open();
            //dbAdapter.deleteFaculties();
            //dbAdapter.create();

            FacultiesParser facultiesParser = new FacultiesParser(reader);
            facultiesParser.parse();

            request = new HttpGet(URL_GROUPS);
            response = client.execute(request);
            in = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(in));
            GroupsParser groupsParser = new GroupsParser(reader);
            groupsParser.parse();

            cancelNotification();
            dbAdapter.close();
            LocalBroadcastManager.getInstance(this).sendBroadcast(syncFinished);
        } catch (IOException e) {
            cancelNotification();
            e.printStackTrace();
        }
    }

    private class FacultiesParser extends CSVParser{
        public FacultiesParser(BufferedReader br) {
            super(br);
        }

        @Override
        public boolean handleData(String[] data) {
            //Log.d(TAG, data[0] + " " + data[1] + " " + data[2]);
            addFacultyToDatabase(data);
            return true;
        }
    }

    private class GroupsParser extends CSVParser{
        public GroupsParser(BufferedReader br) {
            super(br);
        }

        @Override
        public boolean handleData(String[] data) {
            //Log.d(TAG, data[0] + " " + data[1] + " " + data[2]);
            addGroupToDatabase(data);
            return true;
        }
    }

    void addFacultyToDatabase(String[] data){
    }

    void addGroupToDatabase(String[] data){
        String name;
        if (data.length > 6)
            name = data[3] + " an " + data[4] + "__" + data[5] + data[6];
        else
            name = data[3] + " an " + data[4] + "__" + data[5];
        Log.d(TAG, name);
    }

    /*void addFacultyToDatabase(DBAdapter dbAdapter, Element element){
        String str;
        String link;
        String name;

        str = element.toString();
        name = element.html();
        int ID = getFacultyID(str);

        if (ID == 0)
            if (name.contains(":") && generalName.equals("") ) {            //We have found a general prefix for some faculties
                generalName = name;
                generalName = generalName.replace(":", "");
            }
            else{
                name = generalName + " " + name;
                link = str.substring(9, 68);
                dbAdapter.insertFaculty(ID, name, link );
                addGroupsToDatabase(dbAdapter, ID);
            }
        else
            if (ID != -1){
                dbAdapter.insertFaculty(ID, name, null);
                addGroupsToDatabase(dbAdapter, ID);
            }
    }

    void addGroupsToDatabase(DBAdapter dbAdapter, int facultyID){
        if (facultyID != -1) {
            HttpGet request = new HttpGet(PARTIAL_URL_FACULTY + facultyID);
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(request);
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                //Form a string from the response
                String line;
                String htmlText = "";
                while ((line = reader.readLine()) != null)
                    htmlText += line;
                htmlText = htmlText.substring(htmlText.lastIndexOf("<tr>"), htmlText.lastIndexOf("</tr>"));                 // Delete unnecessary html code
                Elements elements = Jsoup.parse(htmlText).getElementsByTag("select");                                       // The <tr> elements
                Elements elements2;                                                                                         // The <td> elements
                Element e;
                int n = elements.size();
                String table = null;
                for(int i = 0; i < n; i++) {
                    e = elements.get(i);
                    elements2 = Jsoup.parse(e.toString()).getElementsByAttribute("value");
                    switch (i){
                        case 0:
                            table = SQLStmtHelper.UNDERGRADUATES_GROUPS_TABLE;
                            break;
                        case 1:
                            table = SQLStmtHelper.MASTERS_GROUPS_TABLE;
                            break;
                        case 2:
                            table = SQLStmtHelper.PHD_GROUPS_TABLE;
                            break;
                        default:
                            table = null;

                    }
                    for (Element element : elements2) {
                        dbAdapter.insertGroup(table, element.html(), getGroupID(element.toString()), facultyID);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int getFacultyID(String html){
        int id = 0, i = 0;
        int n = html.length();
        if (!html.startsWith("<a href") || html.contains("<li>"))
            return -1;
        while (i < (n - 1) && !Character.isDigit(html.charAt(i))){
            i++;
            if (Character.isDigit(html.charAt(i))) {
                if (html.charAt(i-1) == '(')
                    while (Character.isDigit(html.charAt(i))) {
                        if (id == -1)   id = 0;                 //first update
                        id = id * 10 + ((int) html.charAt(i) - 48);
                        i++;
                    }
                break;
            }
        }
        return id;
    }

    int getGroupID(String html){
        int id = 0, pos;
        char c;
        pos = html.indexOf("ID=");
        if (pos != -1){
            pos += 3;
            c = html.charAt(pos);
            while (Character.isDigit(c = html.charAt(pos))) {
                id = id * 10 + (c - 48);
                pos++;
            }
            return id;
        }
        return -1;
    }*/

    void showNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.refresh_anim)
                .setContentTitle("Descărcare")
                .setContentText("Sincronizare facultăți")
                .setProgress(0, 0, true);
        Notification n = builder.build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(REFRESH_NOTIF_CODE, n);
    }

    void cancelNotification(){
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(REFRESH_NOTIF_CODE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelNotification();
        Log.d(TAG, "finished");
    }
}

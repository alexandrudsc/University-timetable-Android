package com.developer.alexandru.aplicatie_studenti.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.developer.alexandru.aplicatie_studenti.SettingsActivity;
import com.developer.alexandru.aplicatie_studenti.MainActivity;
import com.developer.alexandru.aplicatie_studenti.app_widget.TimetableWidgetProvider;
import com.developer.alexandru.aplicatie_studenti.view_pager.ViewPagerAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Helper for parsing the HTML from a response to a GET request to a certain php file
 */
public class TimetableDownloader extends AsyncTask<String, Void, Void> {

    //Debug
    private static final boolean D = false;
    public static final String TAG = "Timetable downloader";

    public final String URL_TIME_ORG = "http://www.usv.ro/index.php/ro/1/Structura%20anului%20academic/257/3/252";

    private Document document;
    private String htmlText;
    private Elements elements;
    //Debug
    private Course course;
    private DBAdapter dbAdapter;

    private boolean success = false;

    private SettingsActivity activity;

    private SharedPreferences preferences;
    SharedPreferences timeOrganiser;

    String[] splitted;
    String[] commonData;

    public TimetableDownloader(SettingsActivity activity) {

        this.activity = activity;

        preferences = activity.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        timeOrganiser = activity.getSharedPreferences(MainActivity.TIME_ORGANISER_FILE_NAME,
                Context.MODE_PRIVATE);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showDialog();
    }

    /**
     * Do all the work off the main thread
     * @param params string containing the url to the php file with the required params
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        try {

            //Form a GET request for the time organization
            HttpGet request = new HttpGet(URL_TIME_ORG);
            //Default HTTP client
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);

            // If the connection was successful recreate the database
            dbAdapter = new DBAdapter(activity);
            // Create/recreate the database containing courses
            dbAdapter.open();
            dbAdapter.deleteCourses();
            dbAdapter.create();

            long start = System.currentTimeMillis();
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //Form a string from the response
            String line = "";
            while ((line = reader.readLine()) != null )
                htmlText += line;
            document = Jsoup.parse(htmlText);
            if (!D) Log.d(TAG, "" + ((System.currentTimeMillis() - start) / 1000));
            final int currentWeek = timeOrganiser.getInt(MainActivity.WEEK_OF_SEMESTER, MainActivity.WEEKS_IN_SEMESTER);
            saveTimeOrganization(timeOrganiser.edit());

            //Form a GET request for the timetable at the specified URL
            request = new HttpGet(params[0]);
            //Default HTTP client
            client = new DefaultHttpClient();
            response = client.execute(request);

            in = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(in));
            //Form a string from the response
            line = "";
            while ((line = reader.readLine()) != null ) {
                if(isCancelled())
                    throw new IOException("Background work cancelled");
                htmlText += line;
            }

            //Parse the response into a jsoup.nodes.document object.The same with an DOM object
            document = Jsoup.parse(htmlText);

            //Get all the elements needed (classes)
            elements = document.getElementsByAttribute("day");
            int noOfElements = elements.size();

            for(int i = 0; i < noOfElements; i++)
                try {
                    if (isCancelled())
                        throw new IOException("Background work cancelled");
                    course = getCourseFromHTMLElement(elements.get(i));
                    if(course != null) {
                        validateCourse(course);
                        dbAdapter.insertCourse(course, ViewPagerAdapter.days[Integer.valueOf(elements.get(i).attr("day"))]);
                    }
                }catch (ParseCourseException e){
                    e.printStackTrace();
                }

            success = true;
            if (ViewPagerAdapter.listsOfCourses != null)
                // Invalidate the old lists of courses in case it exists
                for (int  i = 0; i < ViewPagerAdapter.NUM_DAYS; i++) {
                    ViewPagerAdapter.listsOfCourses[i].clear();
                    ViewPagerAdapter.listsOfCourses[i] = null;
                }
            ViewPagerAdapter.listsOfCourses = null;

            dbAdapter.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (dbAdapter != null)
                dbAdapter.close();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(success)
            activity.setResult(Activity.RESULT_OK);
        else
            activity.setResult(Activity.RESULT_CANCELED);
        activity.cancelDialog();

        // Avoid accidental memory leaks
        activity = null;
    }

    /**
     * Create a course from an HTML element which reacts to an onmouseover event
     * @param element org.jsoup.nodes.element text like "a href="#" class="activ" onmouseover='Tip(<ul></ul>)'"
     * @return Course object
     */
    private Course getCourseFromHTMLElement(Element element) throws ParseCourseException {
        if (course == null)
            course = new Course();

        splitted = element.html().split("'");
        commonData = getCommonDataFromLink(splitted[2]);

        //Short (common) data about the course
        course.prof = commonData[1];
        course.location = commonData[2];
        course.name = commonData[0];
        course.parallelFaculties = commonData[4];
        course.info = commonData[3];

        //Get the ul element from the HTML element
        Document doc = Jsoup.parse(splitted[1]);
        Elements LIs;
        LIs = doc.getElementsByTag("li");

        //Extract the time from the first li element.If there is any extra info, extract that too
        String[] timing = getTimeFromLi(LIs.get(0).toString());
        course.time = timing[0];
        //The info extract here is like "in saptamanile pare" and sometimes must be added to a previous extracted info ( like " sapt. 1, 2 ,12-1h")
        if(timing[1] != null)
            if(course.info != null)
                course.info = timing[1] + " " + course.info;
            else
                course.info = timing[1];

        //Extract the full name and the type of the course
        String[] fullNameAndType = getFullNameAndTypeFromLi(LIs.get(1).toString());
        course.fullName = fullNameAndType[0];
        course.type = fullNameAndType[1];

        //Extract the full location.
        course.fullLocation = getFullLocationFromLi(LIs.get(2).toString());

        //Extract the full name of the prof
        course.fullProf = getFullNameProfFromLi(LIs.get(3).toString());

        return course;
    }

    /**
     * Extract the time to a simple format like: hour - hour from an HTML li element like
     * "activitate cuprinsa intre 08 sup 00 sup si 10 sup 00 sup"
     * @param li HTML li element within ul ( unordered list )
     * @return two strings with the time in simple format and info about timing (like "in saptamanile pare")
     */
    private String[] getTimeFromLi(String li) {
        li = li.replace("</li>", "");
        String[] timing = new String[2];
        String[] splittedString = li.split(", ");

        if (splittedString.length > 1) {
            //There is info in this <li> element
            // Extract the extra info about timing
            timing[1] = splittedString[1];
        }
        timing[0] = getTimeDigitsFromString(splittedString[0]);

        return timing;
    }

    /**
     * The time li element in an HTML unordered list is a complex string.Extract only the digits and for a simple formatted time
     * @param string the HTML text describing the li element
     * @return a string with the time in a simple format
     */
    private String getTimeDigitsFromString(String string){
        // Extract the exact hours when the class is held
        // Select every digit from the char array. @currentDigitInTime counts what is the meaning of each digit
        String[] splittedString = string.split(" ");
        return splittedString[3].substring(0, 2) + ":" + splittedString[3].substring(7, 9) + " - " +
                splittedString[5].substring(0, 2) + ":" + splittedString[5].substring(7, 9);
    }

    /**
     * Extract the full name and the type of a course described in an HTML li element within a list
     * @return a String[] object: on position 0 is the full name, on position 1 is the type
     */
    private String[] getFullNameAndTypeFromLi(String li){
        //The li element begins with a redundant word.Remove it
        li = li.substring("<li>disciplina: ".length());
        String[] fullNameAndType = li.split(",");
        if(fullNameAndType.length > 2) {
            for (int i = 1; i < fullNameAndType.length - 1; i++)
                fullNameAndType[0] += (", " + fullNameAndType[i]);
            fullNameAndType[fullNameAndType.length - 1] = fullNameAndType[fullNameAndType.length - 1].replace(" ", "");
            return new String[]{fullNameAndType[0], fullNameAndType[fullNameAndType.length - 1].replace("</li>", "")};
        }

        //The type must be a single word
        fullNameAndType[1] = fullNameAndType[1].replace("</li>","");
        fullNameAndType[1] = fullNameAndType[1].replace(" ","");

        return fullNameAndType;
    }

    /**
     * Extract the full location name from an HTML li element
     * @param li HTML  text describing the li element within the ul element
     * @return a string containing the full name of the location for a given course
     */
    private String getFullLocationFromLi(String li){
        li = li.replace("<li>sala: ", "");
        return li.replace("</li>", "");
    }

    /**
     * Extract the full name of the prof from an HTML li element
     * @param li the HTML text describing the li element
     * return a string containing the full name
     */
    private String getFullNameProfFromLi(String li){
        li = li.replace("<li>cadru didactic: ", "");
        return li.replace("</li>", "");
    }

    /**
     * Extract the common data: short names for prof, course;
     *                          info about timing e.g. "primele 10 saptamani", if exists;
     *                          parallel faculties attending to the course, if any;
     * @param text part of the HTML text describing the course: e.g. ")" onmouseout="UnTip()">PCLP1 ..." and contains a number of "br" tags
     * @return a String[] object with variable data: <br>
     *         position 0 short name for course <br>
     *         position 1 short name for prof <br>
     *         position 2 short name for location <br>
     *         position 3 info about timing <br>
     *         position 4 parallel faculties.
     */
    private String[] getCommonDataFromLink(String text){
        String[] commonData = new String[5];

        // Isolate every row of text
        String[] rows = text.split("<br>");

        //eliminate the unnecessary text
        rows[0] = rows[0].replace(")\" onmouseout=\"UnTip()\">", "");
        rows[rows.length - 1] =  rows[rows.length - 1].split("</a>")[0];

        //First row contains short names for prof, course name and type separated by comma
        String[] shortData = rows[0].split(",");
        try {
            //short name for course
            commonData[0] = shortData[0];
            //short name for location
            commonData[2] = shortData[2];

            //short name for prof
            commonData[1] = rows[1];
        }catch (IndexOutOfBoundsException e){
            //This course does not contains all kinds of common data ( for example a "STC" class)
        }
        if(rows.length > 2) {
            if (rows[2].startsWith("+"))
                //Parallel faculties
                commonData[4] = rows[2];
            else if (rows[2].startsWith(""))
                commonData[3] = rows[2];
        }
        return commonData;
    }

    // Add void values if were not in downloaded data
    private void validateCourse(Course c){
        if (c.info  == null)
            c.info = "";
        if (c.location == null)
            c.location = "";
    }

    private class ParseCourseException extends Exception{
    }

    private void saveTimeOrganization(SharedPreferences.Editor editor){

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        elements = document.getElementsByTag("td");
        String h1 = elements.get(6).getElementsByTag("strong").html();
        String h2 = elements.get(19).getElementsByTag("strong").html();

        try {
            // Start and stop for first semester
            long start1 = df.parse ( elements.get(3).getElementsByTag("strong").html().split("-")[0]).getTime();
            long stop1 = df.parse ( elements.get(4).getElementsByTag("strong").html().split("-")[1] ).getTime();

            // Start and stop for first holiday
            long startH1 = df.parse(h1.substring(0, 10)).getTime();
            long stopH1 = df.parse(h1.substring(11)).getTime() + TimetableWidgetProvider.DAY_TO_MILLIS;

            // Start and stop for second semester
            long start2 = df.parse(elements.get(16).getElementsByTag("strong").html().split("-")[0]).getTime();
            long stop2 = df.parse(elements.get(17).getElementsByTag("strong").html().split("-")[1]).getTime();

            // Start and stop for second holiday
            long startH2 = df.parse(h2.substring(0, 10)).getTime();
            long stopH2 = df.parse(h2.substring(11)).getTime() ;

            long curr = System.currentTimeMillis();

            // Detect semester
            long start = (curr >= start1 && curr <= stop1) ? start1 : start2;
            long stop = start == start1 ? stop1 : stop2;

            editor.putLong(MainActivity.START_DATE, start);
            editor.putLong(MainActivity.END_DATE, stop);

            editor.putInt(MainActivity.NUMBER_OF_HOLIDAYS, 1);
            if (start == start1)
                editor.putString(MainActivity.HOLIDAY + "_0", startH1 + "-" + stopH1);
            else
                editor.putString(MainActivity.HOLIDAY + "_0", startH2 + "-" + stopH2);

            if (Build.VERSION.SDK_INT > 8)
                editor.apply();
            else
                editor.commit();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}

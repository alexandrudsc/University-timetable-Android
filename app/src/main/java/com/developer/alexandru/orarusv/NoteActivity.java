package com.developer.alexandru.orarusv;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Alexandru A simple note editor. It will be used by the user to note general things
 * about every discipline.
 */
public class NoteActivity extends AppCompatActivity {

  // Debug
  private static final String TAG = "NoteActivity";

  // Used as tag in the bundle associated with the intent that started this activity
  public static final String COURSE_NAME_EXTRA = "course_name";

  private TextView noteTitle;
  private EditText note;
  private FileWriter writer;

  // Name of file keeping this note
  String fileName;
  // The content of the file as it was at opening or null if file does not exists
  String oldContent;
  // the content of the text interacting with the user.Most of times it will be different from
  // oldContent.
  String newContent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_note);

    init(getIntent().getExtras());
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Text read from file
    if (oldContent != null) note.setText(oldContent);
  }

  @Override
  protected void onPause() {
    super.onPause();
    // Save the newContent
    saveContentToFile();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater().inflate(R.menu.edit_note, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    // int id = item.getItemId();

    return super.onOptionsItemSelected(item);
  }

  private void init(Bundle data) {
    noteTitle = findViewById(R.id.note_title);
    noteTitle.setText(data.getString(COURSE_NAME_EXTRA));
    note = findViewById(R.id.note);

    fileName = getFilesDir().getAbsolutePath() + "/" + noteTitle.getText() + ".note";

    try {
      // Read the oldContent of the file. Update the editText in onResume()
      oldContent = getContentFromFile(note);
    } catch (IOException e) {
      errMessage();
    }
  }

  // Get the note saved and for displaying it
  // Treat the special case of note not created yet
  private String getContentFromFile(EditText editText) throws IOException {
    File file = new File(fileName);
    if (file.exists()) {
      FileReader in = new FileReader(fileName);
      Log.d(TAG, getFilesDir().getAbsolutePath() + "/" + noteTitle.getText() + ".note");
      BufferedReader reader = new BufferedReader(in);
      String line;
      StringBuilder builder = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        builder.append(line);
        builder.append("\n");
      }
      String text = builder.toString();
      Log.d(TAG, text);
      in.close();
      return text;
    } else {
      if (!file.createNewFile()) errMessage();
      return null;
    }
  }

  // Save the content of the note in case it has changed
  private void saveContentToFile() {
    newContent = note.getEditableText().toString();
    if (oldContent == null || !oldContent.equals(newContent)) {
      try {
        oldContent = newContent;
        newContent = newContent.replaceAll("\\n", System.getProperty("line.separator"));
        writer = new FileWriter(fileName, false);
        writer.write(newContent);
        writer.close();

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void errMessage() {
    Toast.makeText(
            getApplicationContext(), "Eroare de citire. Incercați din nou.", Toast.LENGTH_SHORT)
        .show();
  }
}

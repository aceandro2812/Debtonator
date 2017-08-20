package com.example.nagasudhir.debtonator;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class PersonEditActivity extends AppCompatActivity {

    private String mPersonId = null;
    private String mPersonName = null;
    private String mPersonPhone = null;
    private String mPersonEmail = null;
    private String mPersonMetadata = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mPersonId = intent.getExtras().getString("person_id");
        if (mPersonId == null) {
            backBtn(null);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Fetch and populate the person details
        new LongOperation().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                backBtn(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backBtn(null);
    }

    private void backBtn(View v) {
        // starts a new Intent to open home page
        Intent personListIntent = new Intent(getBaseContext(), PersonListActivity.class);
        startActivity(personListIntent);
        finish();
    }

    /*
    * Save Person Edits Button
    * */
    public void savePersonEditsBtn(View v) {

    }

    /*
    * Delete Person Button
    * */
    public void deletePersonBtn(View v) {

    }

    // Class with extends AsyncTask class
    private class LongOperation extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.
                Cursor personCursor = PersonEditActivity.this.getContentResolver().query(Uri.parse(Person.CONTENT_URI + "/" + mPersonId), null, null, null, null);
                try {
                    if (personCursor.moveToNext()) {
                        mPersonName = personCursor.getString(personCursor.getColumnIndex(PersonModel.KEY_USERNAME));
                        mPersonPhone = personCursor.getString(personCursor.getColumnIndex(PersonModel.KEY_PHONE_NUMBER));
                        mPersonEmail = personCursor.getString(personCursor.getColumnIndex(PersonModel.KEY_EMAIL_ID));
                        mPersonMetadata = personCursor.getString(personCursor.getColumnIndex(PersonModel.KEY_METADATA));
                    }
                } finally {
                    personCursor.close();
                }
            } catch (SQLException e) {

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
            // Setup the initial Transaction Detail State
            //setTitleOfActivity(mPersonName);
            ((EditText) findViewById(R.id.person_edit_name)).setText(mPersonName);
            ((EditText) findViewById(R.id.person_edit_email)).setText(mPersonEmail);
            ((EditText) findViewById(R.id.person_edit_phone)).setText(mPersonPhone);
            ((EditText) findViewById(R.id.person_edit_metadata)).setText(mPersonMetadata);
        }
    }

}

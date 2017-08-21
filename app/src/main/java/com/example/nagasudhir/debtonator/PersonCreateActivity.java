package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PersonCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    * Create person button listener
    * */
    public void createPersonBtn(View v) {
        String personName = ((EditText) findViewById(R.id.person_create_name)).getText().toString().trim();
        String personPhone = ((EditText) findViewById(R.id.person_create_phone)).getText().toString().trim();
        String personEmail = ((EditText) findViewById(R.id.person_create_email)).getText().toString().trim();
        String personMetadata = ((EditText) findViewById(R.id.person_create_metadata)).getText().toString().trim();
        ContentValues personCreateContentValues = new ContentValues();
        personCreateContentValues.put(PersonModel.KEY_USERNAME, personName);
        personCreateContentValues.put(PersonModel.KEY_PHONE_NUMBER, personPhone);
        personCreateContentValues.put(PersonModel.KEY_EMAIL_ID, personEmail);
        personCreateContentValues.put(PersonModel.KEY_METADATA, personMetadata);
        Uri newPersonUri = PersonCreateActivity.this.getContentResolver().insert(Person.CONTENT_URI, personCreateContentValues);
        int newPersonId = -1;
        try {
            newPersonId = Integer.parseInt(newPersonUri.getLastPathSegment());
        } catch (Exception e) {
            Toast.makeText(this, "ERROR in creating Person...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPersonId == -1) {
            Toast.makeText(this, "New Person NOT created...", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "New Person created !", Toast.LENGTH_SHORT).show();

        // pass in row Id to create the Content URI for a single row
        Intent personViewIntent = new Intent(getBaseContext(), PersonViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("person_id", newPersonId + "");
        bundle.putString("person_name", personName);
        personViewIntent.putExtras(bundle);
        startActivity(personViewIntent);
        finish();
    }

}

package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PersonCreateActivity extends AppCompatActivity {

    static final int PICK_CONTACT = 1;
    EditText mPersonName;
    EditText mPersonPhone;
    EditText mPersonEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_create);
        mPersonName = (EditText) findViewById(R.id.person_create_name);
        mPersonPhone = (EditText) findViewById(R.id.person_create_phone);
        mPersonEmail = (EditText) findViewById(R.id.person_create_email);
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
    * Import from contacts button listener
    * */
    public void contactsImportBtn(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    /*
    * After fetching the contact information from contacts app
    * */
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                try {
                    Uri result = data.getData();

                    // get the phone number id from the Uri
                    String id = result.getLastPathSegment();
                    // query the phone numbers for the selected phone number id
                    Cursor c = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone._ID + "=?",
                            new String[]{id}, null);

                    int phoneIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);


                    if (c.getCount() == 1) {
                        if (c.moveToFirst()) {
                            String contactNumber = c.getString(phoneIdx);
                            String contactName = c.getString(nameIdx);
                            contactNumber = contactNumber.replaceAll("\\s", "").trim();
                            mPersonName.setText(contactName);
                            mPersonPhone.setText(contactNumber);
                        }
                    }

                } catch (Exception e) {

                }
                break;
        }
    }

    /*
    * Create person button listener
    * */
    public void createPersonBtn(View v) {
        String personName = ((EditText) findViewById(R.id.person_create_name)).getText().toString().trim();
        String personPhone = ((EditText) findViewById(R.id.person_create_phone)).getText().toString().trim();
        String personEmail = ((EditText) findViewById(R.id.person_create_email)).getText().toString().trim();
        String personMetadata = ((EditText) findViewById(R.id.person_create_metadata)).getText().toString().trim();
        if (personPhone.equals("")) {
            personPhone = null;
        }
        if (personEmail.equals("")) {
            personEmail = null;
        }
        if (personMetadata.equals("")) {
            personMetadata = null;
        }
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

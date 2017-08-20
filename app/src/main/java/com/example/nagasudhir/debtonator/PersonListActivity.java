package com.example.nagasudhir.debtonator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class PersonListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView mPersonListView;
    SimpleCursorAdapter mPersonListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPersonListView = (ListView) findViewById(R.id.person_list);
        mPersonListAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_person_list_item,
                null,
                new String[]{PersonModel.KEY_ROW_ID, PersonModel.KEY_USERNAME, PersonModel.KEY_PHONE_NUMBER, PersonModel.KEY_EMAIL_ID, PersonModel.KEY_METADATA},
                new int[]{R.id.person_id, R.id.person_name, R.id.person_phone, R.id.person_email, R.id.person_metadata}, 0);

        mPersonListView.setAdapter(mPersonListAdapter);
        mPersonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String personName = cursor.getString(cursor.getColumnIndexOrThrow(PersonModel.KEY_USERNAME));
                Toast.makeText(getApplicationContext(), personName, Toast.LENGTH_SHORT).show();
                String personId = cursor.getString(cursor.getColumnIndexOrThrow(PersonModel.KEY_ROW_ID));
                // pass in row Id to create the Content URI for a single row
                Intent personViewIntent = new Intent(getBaseContext(), PersonViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("person_id", personId);
                personViewIntent.putExtras(bundle);
                startActivity(personViewIntent);
                finish();
            }
        });
        /* Creating a loader for populating list view from sqlite database
        This statement, invokes the method onCreatedLoader() */
        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.person_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent personCreateIntent = new Intent(getBaseContext(), PersonCreateActivity.class);
                startActivity(personCreateIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                homeBtn(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        homeBtn(null);
    }

    private void homeBtn(View v) {
        // starts a new Intent to open home page
        Intent homePageIntent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(homePageIntent);
        finish();
    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = Person.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        mPersonListAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mPersonListAdapter.swapCursor(null);
    }
}

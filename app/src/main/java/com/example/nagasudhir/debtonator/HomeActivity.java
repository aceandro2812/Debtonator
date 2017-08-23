package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    ListView mPersonsListView, mTransactionsListView;
    SimpleCursorAdapter mPersonsAdapter, mTransactionsAdapter;
    String mTransactionSetId = null;
    String mTranSetName = null;
    SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the transaction set Id to load from shared preferences
        mSharedPrefs = getSharedPreferences(GlobalVarClass.SHARED_PREFS_KEY, MODE_PRIVATE);
        mTransactionSetId = mSharedPrefs.getString(GlobalVarClass.CURRENT_TRAN_SET_ID_KEY, null);
        if (mTransactionSetId == null) {
            loadTranSetsBtn(null);
        }

        // Setting up the Transactions list
        mTransactionsListView = (ListView) findViewById(R.id.tranList);

        mTransactionsAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_home_transaction_list_item_layout,
                null,
                new String[]{"_id", TransactionModel.KEY_DESCRIPTION, TransactionModel.KEY_TRANSACTION_TIME, TransactionModel.KEY_METADATA, TransactionModel.VARIABLE_TRANSACTION_PEOPLE, TransactionModel.VARIABLE_TRANSACTION_SUM},
                new int[]{R.id.tran_id, R.id.tran_description, R.id.tran_time, R.id.tran_metadata, R.id.tran_people, R.id.tran_sum}, 0);

        mTransactionsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (columnIndex == cursor.getColumnIndex(TransactionModel.VARIABLE_TRANSACTION_SUM)) {
                    TextView textView = (TextView) view;
                    textView.setText(NumberFormat.getNumberInstance().format(cursor.getDouble(columnIndex)));
                    return true;
                } else if (columnIndex == cursor.getColumnIndex(TransactionModel.KEY_TRANSACTION_TIME)) {
                    TextView textView = (TextView) view;
                    String dateString = cursor.getString(columnIndex);

                    String originalStringFormat = "yyyy-MM-dd HH:mm:ss";
                    String desiredStringFormat = "dd/MM/yyyy HH:mm";

                    SimpleDateFormat readingFormat = new SimpleDateFormat(originalStringFormat);
                    SimpleDateFormat outputFormat = new SimpleDateFormat(desiredStringFormat);

                    try {
                        Date date = readingFormat.parse(dateString);
                        textView.setText(outputFormat.format(date));
                    } catch (ParseException e) {
                        textView.setText(dateString);
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        mTransactionsListView.setAdapter(mTransactionsAdapter);

        mTransactionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final String transaction_id = ((TextView) v.findViewById(R.id.tran_id)).getText().toString();
                // set the bundle to start transaction editing screen
                Intent intent = new Intent(getBaseContext(), TransactionEditActivity.class);
                intent.putExtra("transaction_id", transaction_id);
                startActivity(intent);
                finish();
            }
        });
        // Setting up the person list
        mPersonsListView = (ListView) findViewById(R.id.personList);

        mPersonsAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_home_person_list_item_layout,
                null,
                new String[]{PersonModel.KEY_USERNAME, "_id", PersonModel.VARIABLE_PERSON_BALANCE},
                new int[]{R.id.person_name, R.id.person_id, R.id.person_balance}, 0);

        mPersonsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                try {
                    if (columnIndex == cursor.getColumnIndexOrThrow(PersonModel.VARIABLE_PERSON_BALANCE)) {
                        TextView textView = (TextView) view;
                        Double personBalance = cursor.getDouble(columnIndex);
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setPositivePrefix("+");
                        textView.setText(df.format(personBalance));
                        if (personBalance > 0) {
                            textView.setTextColor(Color.parseColor("#4CAF50"));
                        } else if (personBalance < 0) {
                            textView.setTextColor(Color.parseColor("#FF9800"));
                        }
                        return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }
        });

        mPersonsListView.setAdapter(mPersonsAdapter);

        FloatingActionButton managePeopleFab = (FloatingActionButton) findViewById(R.id.fab_manage_persons);
        managePeopleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish this activity and start the Transaction Set List Displaying Activity
                Intent personListIntent = new Intent(getBaseContext(), PersonListActivity.class);
                startActivity(personListIntent);
                finish();
            }
        });
        FloatingActionButton addTranFab = (FloatingActionButton) findViewById(R.id.fab_add_tran);
        addTranFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTransactionAndView();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Creating a loader for populating listview from sqlite database */
        /* This statement, invokes the method onCreatedLoader() */
        getSupportLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().initLoader(1, null, this);

        // Create Object and call AsyncTask execute Method
        new LongOperation().execute();
    }

    /*
    * Load the TransactionSets Activity button
    * */
    public void loadTranSetsBtn(View v) {
        //Make loaded transaction Id as null
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(GlobalVarClass.CURRENT_TRAN_SET_ID_KEY, null);
        editor.apply();
        // Finish this activity and start the Transaction Set List Displaying Activity
        Intent transactionSetsDisplayIntent = new Intent(getBaseContext(), TransactionSetsActivity.class);
        startActivity(transactionSetsDisplayIntent);
        finish();
    }

    /*
    * Create a new transaction and open the screen
    * */
    public void createNewTransactionAndView() {
        ContentValues insertValues = new ContentValues();
        insertValues.put(TransactionModel.KEY_DESCRIPTION, "");
        insertValues.put(TransactionModel.KEY_METADATA, "");
        insertValues.put(TransactionModel.KEY_TRANSACTION_SET_ID, mTransactionSetId);
        insertValues.put(TransactionModel.KEY_TRANSACTION_TIME, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        Uri newTransactionUri = getContentResolver().insert(TransactionProvider.CONTENT_URI, insertValues);
        int newTransactionId = -1;
        try {
            newTransactionId = Integer.parseInt(newTransactionUri.getLastPathSegment());
        } catch (Exception e) {
            Toast.makeText(this, "ERROR creating new Transaction...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newTransactionId == -1) {
            Toast.makeText(this, "New Transaction NOT created...", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "New Transaction created !", Toast.LENGTH_SHORT).show();

        // Start the Transaction Editing Activity
        Intent intent = new Intent(getBaseContext(), TransactionEditActivity.class);
        intent.putExtra("transaction_id", newTransactionId + "");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                Cursor transactionSetCursor = HomeActivity.this.getContentResolver().query(Uri.parse(TransactionSetProvider.CONTENT_URI + "/" + mTransactionSetId), null, null, null, null);
                try {
                    if (transactionSetCursor.moveToNext()) {
                        mTranSetName = transactionSetCursor.getString(transactionSetCursor.getColumnIndex(TransactionSetModel.KEY_NAME_STRING));
                    }
                } finally {
                    transactionSetCursor.close();
                }

            } catch (SQLException e) {

            } catch (Exception e) {

            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
            ((TextView) findViewById(R.id.tranSetName)).setText(mTranSetName);
        }
    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        if (id == 0) {
            Uri uri = Uri.parse(Person.CONTENT_URI + "/by_transaction_set/" + mTransactionSetId);
            return new CursorLoader(this, uri, null, null, null, null);
        } else if (id == 1) {
            Uri uri = Uri.parse(TransactionProvider.CONTENT_URI + "/by_transaction_set/" + mTransactionSetId);
            return new CursorLoader(this, uri, null, null, null, null);
        }
        return null;
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        if (arg0.getId() == 0) {
            mPersonsAdapter.swapCursor(arg1);
        } else if (arg0.getId() == 1) {
            mTransactionsAdapter.swapCursor(arg1);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        if (arg0.getId() == 0) {
            mPersonsAdapter.swapCursor(null);
        } else if (arg0.getId() == 1) {
            mTransactionsAdapter.swapCursor(null);
        }
    }
}

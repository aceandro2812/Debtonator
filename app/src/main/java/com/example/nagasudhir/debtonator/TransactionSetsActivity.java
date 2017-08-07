package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.nagasudhir.debtonator.TransactionSetModel.KEY_NAME_STRING;

public class TransactionSetsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    ListView mTranSetsListView;
    SimpleCursorAdapter mTranSetsAdapter;
    static final String DEFAULT_NEW_TRAN_SET_NAME = "Transaction Set";
    SharedPreferences sharedpreferences;
    ArrayList<String> mTranSetNamesList = new ArrayList<String>();
    //Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_sets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedpreferences = getSharedPreferences(GlobalVarClass.SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        // Get the transaction set Id to load from shared preferences
        String loadedTransactionSetId = sharedpreferences.getString(GlobalVarClass.CURRENT_TRAN_SET_ID_KEY, null);
        if (loadedTransactionSetId != null) {
            homeBtn(null);
        }

        // Setting up Context that can used for listener and other stuff
        //mContext = this;

        // Setting up the person list
        mTranSetsListView = (ListView) findViewById(R.id.tran_set_list);

        mTranSetsAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_transaction_sets_list_item,
                null,
                new String[]{KEY_NAME_STRING, TransactionSetModel.KEY_ROW_ID},
                new int[]{R.id.tran_set_name, R.id.tran_set_id}, 0);

        mTranSetsListView.setAdapter(mTranSetsAdapter);

        /* Creating a loader for populating listview from sqlite database */
        /* This statement, invokes the method onCreatedLoader() */
        getSupportLoaderManager().initLoader(0, null, this);

        // Create Object and call AsyncTask execute Method
        new LongOperation().execute();
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
                mTranSetNamesList = new ArrayList<String>();
                Cursor transactionSetCursor = TransactionSetsActivity.this.getContentResolver().query(TransactionSetProvider.CONTENT_URI, null, null, null, null);
                try {
                    while (transactionSetCursor.moveToNext()) {
                        mTranSetNamesList.add(transactionSetCursor.getString(transactionSetCursor.getColumnIndex(TransactionSetModel.KEY_NAME_STRING)));
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
        }
    }

    /*
    * Create a Transaction Set Button Listener
    * */
    public void createTranSetBtn(final View v) {
        final EditText input = new EditText(this);
        input.setText("");
        input.setSingleLine();
        new AlertDialog.Builder(TransactionSetsActivity.this)
                .setTitle("Creating Transaction Set...")
                .setMessage("Enter the new Transaction Set Name")
                .setView(input)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        String newName = value.toString();
                        newName = createNonConflictName(newName, mTranSetNamesList, DEFAULT_NEW_TRAN_SET_NAME, 10000);
                        ContentValues insertValues = new ContentValues();
                        insertValues.put(TransactionSetModel.KEY_NAME_STRING, newName);
                        long insertRowId = Long.parseLong(TransactionSetsActivity.this.getContentResolver().insert(TransactionSetProvider.CONTENT_URI, insertValues).getLastPathSegment());
                        if (insertRowId > 0) {
                            Toast.makeText(getApplicationContext(), "New Transaction Set Created!", Toast.LENGTH_SHORT).show();
                            getSupportLoaderManager().restartLoader(0, null, TransactionSetsActivity.this);
                            new LongOperation().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Transaction Set NOT created. May be due to duplicate Name", Toast.LENGTH_SHORT).show();
                            ((Button) v).callOnClick();
                        }
                    }
                }).setNegativeButton("CANCEL", null).show();
    }

    /*
    * Rename a Transaction Set Button Listener
    * */
    public void renameTranSetBtn(View v) {
        final String rowId = ((TextView) ((ViewGroup) v.getParent()).findViewById(R.id.tran_set_id)).getText().toString();
        final String tranSetName = ((TextView) ((ViewGroup) v.getParent()).findViewById(R.id.tran_set_name)).getText().toString();
        final EditText input = new EditText(this);
        input.setText(tranSetName);
        input.setSingleLine();
        new AlertDialog.Builder(TransactionSetsActivity.this)
                .setTitle("Reaniming Transaction Set...")
                .setMessage("Enter the new Transacton Set Name")
                .setView(input)
                .setPositiveButton("RENAME", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        ContentValues updatedValues = new ContentValues();
                        String renamedString = value.toString().trim();
                        if (renamedString.equals("")) {
                            Toast.makeText(getApplicationContext(), "Empty Name Not Allowed...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        updatedValues.put(TransactionSetModel.KEY_NAME_STRING, renamedString);
                        //int numUpdatedRows = TransactionSetModel.updateTransactionSet(new AppDB(getApplicationContext()).getWritableDB(), updatedValues, "id=?", new String[]{rowId});
                        int numUpdatedRows = TransactionSetsActivity.this.getContentResolver().update(TransactionSetProvider.CONTENT_URI, updatedValues, "id=?", new String[]{rowId});
                        if (numUpdatedRows > 0) {
                            Toast.makeText(getApplicationContext(), "Transaction Set Renamed!", Toast.LENGTH_SHORT).show();
                            getSupportLoaderManager().restartLoader(0, null, TransactionSetsActivity.this);
                            new LongOperation().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Transaction Set NOT Renamed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    /*
    * Load a Transaction Set Button Listener
    * */
    public void loadTranSetBtn(View v) {
        final String rowId = ((TextView) ((ViewGroup) v.getParent()).findViewById(R.id.tran_set_id)).getText().toString();
        final String tranSetName = ((TextView) ((ViewGroup) v.getParent()).findViewById(R.id.tran_set_name)).getText().toString();
        // set the shared preferences to set the transaction set to be opened
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(GlobalVarClass.CURRENT_TRAN_SET_ID_KEY, rowId);
        editor.apply();
        homeBtn(null);
    }

    /*
    * Delete a Transaction Set Button Listener
    * */
    public void deleteTranSetBtn(View v) {
        final String rowId = ((TextView) ((ViewGroup) v.getParent()).findViewById(R.id.tran_set_id)).getText().toString();
        final String tranSetName = ((TextView) ((ViewGroup) v.getParent()).findViewById(R.id.tran_set_name)).getText().toString();
        new AlertDialog.Builder(TransactionSetsActivity.this)
                .setTitle("Delete Transaction Set?")
                .setMessage("Are you sure you want to delete the Transaction Set '" + tranSetName + "' ?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //int numAffectedRows = TransactionSetModel.deleteTransactionSet(new AppDB(getApplicationContext()).getWritableDB(), "id=?", new String[]{rowId});
                        int numAffectedRows = TransactionSetsActivity.this.getContentResolver().delete(TransactionSetProvider.CONTENT_URI, "id=?", new String[]{rowId});
                        if (numAffectedRows > 0) {
                            Toast.makeText(getApplicationContext(), "Transaction Set Deleted!", Toast.LENGTH_SHORT).show();
                            getSupportLoaderManager().restartLoader(0, null, TransactionSetsActivity.this);
                            new LongOperation().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "No Transaction Set is deleted...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create().show();
    }

    /*
    * When the 'home' button is clicked
    * */
    public void homeBtn(View v) {
        // starts a new Intent to open home page
        Intent HomePageIntent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(HomePageIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            homeBtn(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transaction_sets, menu);
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

    private String createNonConflictName(String strIn, ArrayList<String> strList, String defaultBlankName, int maxIterations) {
        String tempNewName = strIn.trim();
        if (tempNewName.equals("")) {
            tempNewName = defaultBlankName;
        }
        int iterations = 0;
        if (strList.indexOf(tempNewName) == -1) {
            return tempNewName;
        }

        while (iterations < maxIterations) {
            iterations++;
            if (strList.indexOf(tempNewName + "_" + iterations) == -1) {
                tempNewName = tempNewName + "_" + iterations;
                break;
            }
        }
        return tempNewName;
    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // todo filter loader by arg0
        Uri uri = TransactionSetProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        mTranSetsAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mTranSetsAdapter.swapCursor(null);
    }
}

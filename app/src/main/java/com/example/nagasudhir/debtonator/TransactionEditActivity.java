package com.example.nagasudhir.debtonator;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransactionEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView mTransactionsContributionsListView;
    SimpleCursorAdapter mTransactionsContributionsAdapter;
    String mTransactionId = null;
    String mTransactionDesc = null;
    String mTransactionMetadata = null;
    Date mTransactionDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mTransactionId = intent.getExtras().getString("transaction_id");
        new LongOperation().execute();

        // Setting up the Transactions list
        mTransactionsContributionsListView = (ListView) findViewById(R.id.tran_contr_list);

        mTransactionsContributionsAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.activity_transaction_edit_contribution_list_item,
                null,
                new String[]{"_id", TransactionContributionModel.KEY_PERSON_ID, TransactionContributionModel.KEY_CONTRIBUTION, TransactionContributionModel.KEY_IS_CONSUMER},
                new int[]{R.id.tran_contr_id, R.id.tran_person_name, R.id.tran_contribution, R.id.tran_is_consumer}, 0);

        mTransactionsContributionsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (columnIndex == cursor.getColumnIndex(TransactionContributionModel.KEY_IS_CONSUMER)) {
                    CheckBox chkBox = (CheckBox) view;
                    chkBox.setChecked((cursor.getDouble(columnIndex) == 0 ? false : true));
                    return true;
                }
                return false;
            }
        });

        mTransactionsContributionsListView.setAdapter(mTransactionsContributionsAdapter);
        /* Creating a loader for populating listview from sqlite database */
        /* This statement, invokes the method onCreatedLoader() */
        getSupportLoaderManager().initLoader(0, null, this);
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

    /*
    * When the 'home' button is clicked
    * */
    public void homeBtn(View v) {
        // starts a new Intent to open home page
        Intent HomePageIntent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(HomePageIntent);
        finish();
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
                Cursor transactionCursor = TransactionEditActivity.this.getContentResolver().query(Uri.parse(TransactionProvider.CONTENT_URI + "/" + mTransactionId), null, null, null, null);
                try {
                    if (transactionCursor.moveToNext()) {
                        mTransactionDesc = transactionCursor.getString(transactionCursor.getColumnIndex(TransactionModel.KEY_DESCRIPTION));
                        mTransactionMetadata = transactionCursor.getString(transactionCursor.getColumnIndex(TransactionModel.KEY_METADATA));
                        String tranDateString = transactionCursor.getString(transactionCursor.getColumnIndex(TransactionModel.KEY_TRANSACTION_TIME));
                        mTransactionDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(tranDateString);
                    }
                } finally {
                    transactionCursor.close();
                }

            } catch (SQLException e) {

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
            // Set the views
            ((TextView) findViewById(R.id.tran_description)).setText(mTransactionDesc);
            ((TextView) findViewById(R.id.tran_metadata)).setText(mTransactionMetadata);
            ((Button) findViewById(R.id.tran_date_btn)).setText((new SimpleDateFormat("dd/MM/yyyy")).format(mTransactionDate));
            ((Button) findViewById(R.id.tran_time_btn)).setText((new SimpleDateFormat("HH:mm")).format(mTransactionDate));
        }
    }

    public void showDatePickerDialog(View v) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(mTransactionDate);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mTransactionDate.setYear(year - 1900);
                        mTransactionDate.setMonth(monthOfYear);
                        mTransactionDate.setDate(dayOfMonth);
                        ((Button) findViewById(R.id.tran_date_btn)).setText((new SimpleDateFormat("dd/MM/yyyy")).format(mTransactionDate));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void showTimePickerDialog(View v) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(mTransactionDate);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mTransactionDate.setHours(hourOfDay);
                        mTransactionDate.setMinutes(minute);
                        ((Button) findViewById(R.id.tran_time_btn)).setText((new SimpleDateFormat("HH:mm")).format(mTransactionDate));
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {

        Uri uri = Uri.parse(TransactionContributionProvider.CONTENT_URI + "/by_transaction_detail/" + mTransactionId);
        return new CursorLoader(this, uri, null, null, null, null);

    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        mTransactionsContributionsAdapter.swapCursor(arg1);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mTransactionsContributionsAdapter.swapCursor(null);
    }
}

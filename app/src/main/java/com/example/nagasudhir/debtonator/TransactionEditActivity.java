package com.example.nagasudhir.debtonator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.example.nagasudhir.debtonator.PersonModel.KEY_USERNAME;

public class TransactionEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView mTransactionsContributionsListView;
    MultiAutoCompleteTextView mTransactionTagsTextView;
    String mTransactionSetId = null;
    String mTransactionId = null;
    String mTransactionDesc = null;
    String mTransactionMetadata = null;
    Date mTransactionDate = new Date();
    InitialTransactionDetailState mInitialTransactionDetailState;
    String mNextTranId = null;
    String mPrevTranId = null;
    boolean mIsStateSaved = false;
    ArrayList<TransactionContributionListItem> mTransactionContributionsList = new ArrayList<TransactionContributionListItem>();
    ArrayList<String> mTransactionTagSuggestionsList = new ArrayList<String>();
    ArrayList<String> mInitialTransactionTagList = new ArrayList<String>();
    TransactionContributionAdapter mTransactionContributionsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the transaction set Id to load from shared preferences
        SharedPreferences mSharedPrefs = getSharedPreferences(GlobalVarClass.SHARED_PREFS_KEY, MODE_PRIVATE);
        mTransactionSetId = mSharedPrefs.getString(GlobalVarClass.CURRENT_TRAN_SET_ID_KEY, null);
        if (mTransactionSetId == null) {
            homeBtn(null);
        }
        Intent intent = getIntent();
        mTransactionId = intent.getExtras().getString("transaction_id");

        new LongOperation().execute();

        // Setting up the Transactions list
        mTransactionsContributionsListView = (ListView) findViewById(R.id.tran_contr_list);

        mTransactionTagsTextView = (MultiAutoCompleteTextView) findViewById(R.id.transaction_edit_categories_tv);
        mTransactionTagsTextView.setThreshold(1);

        mTransactionTagsTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        /* Creating a loader for populating listview from sqlite database */
        /* This statement, invokes the method onCreatedLoader() */
        getSupportLoaderManager().initLoader(0, null, this);

        setupUI(findViewById(R.id.transaction_edit_constraint_layout));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putString("TransactionId", mTransactionId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getSupportLoaderManager().restartLoader(0, null, this);

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
    protected void onStop() {
        super.onStop();
        if (!mIsStateSaved) {
            saveChangesToDB();
        }
    }

    @Override
    public void onBackPressed() {
        homeBtn(null);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    hideSoftKeyboard();
                    return false;
                }

            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    protected void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void saveChangesToDB() {
        mIsStateSaved = true;
        mTransactionContributionsArrayAdapter.updateAllDirtyContributionItemsToDB();
        saveTransactionDetailsChanges();
        saveTransactionTagsChanges();
    }

    private class InitialTransactionDetailState {
        String transactionDescription;
        String transactionMetadata;
        Date transactionDateTime;

        public InitialTransactionDetailState(String transactionDescription, String transactionMetadata, Date transactionDateTime) {
            this.transactionDescription = (transactionDescription == null) ? "" : transactionDescription;
            this.transactionMetadata = (transactionMetadata == null) ? "" : transactionMetadata;
            this.transactionDateTime = new Date(transactionDateTime.getTime());
        }
    }

    private void saveTransactionDetailsChanges() {
        try {
            if (mTransactionId != null && mInitialTransactionDetailState != null) {
                String descText = ((EditText) findViewById(R.id.tran_description)).getText().toString().trim();
                String metaDataText = ((EditText) findViewById(R.id.tran_metadata)).getText().toString().trim();
                if (mInitialTransactionDetailState.transactionDescription == null) {
                    mInitialTransactionDetailState.transactionDescription = "";
                }
                if (mInitialTransactionDetailState.transactionMetadata == null) {
                    mInitialTransactionDetailState.transactionMetadata = "";
                }
                mInitialTransactionDetailState.transactionDescription = mInitialTransactionDetailState.transactionDescription.trim();
                mInitialTransactionDetailState.transactionMetadata = mInitialTransactionDetailState.transactionMetadata.trim();
                if ((mInitialTransactionDetailState.transactionDateTime.getTime() != mTransactionDate.getTime()) || !mInitialTransactionDetailState.transactionDescription.equals(descText) || !mInitialTransactionDetailState.transactionMetadata.equals(metaDataText)) {
                    ContentValues updatedValues = new ContentValues();
                    updatedValues.put(TransactionModel.KEY_DESCRIPTION, descText);
                    updatedValues.put(TransactionModel.KEY_METADATA, metaDataText);
                    updatedValues.put(TransactionModel.KEY_TRANSACTION_TIME, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(mTransactionDate));
                    // The changes need to be saved
                    getContentResolver().update(TransactionProvider.CONTENT_URI, updatedValues, "id=?", new String[]{mTransactionId});
                }
            }
        } catch (Exception e) {

        }
    }

    private void saveTransactionTagsChanges() {
        // Get the transaction tags from text view into an array list
        ArrayList<String> finalTags = new ArrayList<String>(Arrays.asList(mTransactionTagsTextView.getText().toString().split(",")));

        // Trimming the tags
        for (int i = 0; i < finalTags.size(); i++) {
            finalTags.set(i, finalTags.get(i).trim());
        }

        // Get the insertion List
        ArrayList<String> tagDeletionList = new ArrayList<String>();
        ArrayList<String> tagInsertionList = new ArrayList<String>();

        for (int i = 0; i < finalTags.size(); i++) {
            if (mInitialTransactionTagList.indexOf(finalTags.get(i)) == -1) {
                String str = finalTags.get(i);
                if (!str.equals("")) {
                    tagInsertionList.add(str);
                }
            }
        }

        // Get the deletion list
        for (int i = 0; i < mInitialTransactionTagList.size(); i++) {
            if (finalTags.indexOf(mInitialTransactionTagList.get(i)) == -1) {
                tagDeletionList.add(mInitialTransactionTagList.get(i));
            }
        }
        try {
            // Do the deletion
            ContentValues updatedValues = new ContentValues();
            for (int i = 0; i < tagDeletionList.size(); i++) {
                getContentResolver().delete(TransactionTagProvider.CONTENT_URI, "name_string=? AND transactions_details_id=?", new String[]{tagDeletionList.get(i), mTransactionId});
            }

            // Do the insertion
            updatedValues = new ContentValues();
            for (int i = 0; i < tagInsertionList.size(); i++) {
                updatedValues.put(TransactionTagModel.KEY_NAME, tagInsertionList.get(i));
                updatedValues.put(TransactionTagModel.KEY_TRANSACTION_DETAILS_ID, mTransactionId);
            }
            getContentResolver().insert(TransactionTagProvider.CONTENT_URI, updatedValues);
        } catch (Exception e) {

        }
    }

    /*
    * When the transaction delete button is pressed
    * */
    public void tranDeleteBtn(View v) {
        new AlertDialog.Builder(TransactionEditActivity.this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this Transaction ?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Changes need not to be saved
                        mIsStateSaved = true;
                        int numTransactionDeleted = TransactionEditActivity.this.getContentResolver().delete(Uri.parse(TransactionProvider.CONTENT_URI + "/" + mTransactionId), null, null);
                        if (numTransactionDeleted == 0) {
                            Toast.makeText(getApplicationContext(), "Transaction NOT deleted...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Transaction Deleted!", Toast.LENGTH_SHORT).show();
                        if (mNextTranId != null) {
                            nextTranBtn(null);
                        } else if (mPrevTranId != null) {
                            prevTranBtn(null);
                        } else {
                            homeBtn(null);
                        }
                    }
                }).create().show();
    }

    /*
    * When the transaction add button is pressed
    * */
    public void addTranBtn(View v) {
        createNewTransactionAndView();
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

    /*
    * When the 'home' button is clicked
    * */
    public void homeBtn(View v) {
        // save the changes
        if (!mIsStateSaved) {
            saveChangesToDB();
        }
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
                    transactionCursor.close();
                    transactionCursor = TransactionEditActivity.this.getContentResolver().query(Uri.parse(TransactionProvider.CONTENT_URI + "/next_prev/" + mTransactionSetId + "/" + mTransactionId), null, null, null, null);
                    if (transactionCursor.moveToNext()) {
                        mNextTranId = transactionCursor.getString(transactionCursor.getColumnIndex(TransactionModel.VARIABLE_NEXT_TRAN_ID));
                        mPrevTranId = transactionCursor.getString(transactionCursor.getColumnIndex(TransactionModel.VARIABLE_PREV_TRAN_ID));
                    }
                    transactionCursor.close();
                    transactionCursor = TransactionEditActivity.this.getContentResolver().query(TransactionTagProvider.CONTENT_URI, null, null, null, null);
                    mTransactionTagSuggestionsList = new ArrayList<String>();
                    while (transactionCursor.moveToNext()) {
                        mTransactionTagSuggestionsList.add(transactionCursor.getString(transactionCursor.getColumnIndex(TransactionTagModel.KEY_NAME)));
                    }
                    transactionCursor.close();
                    transactionCursor = TransactionEditActivity.this.getContentResolver().query(Uri.parse(TransactionTagProvider.CONTENT_URI + "/transaction_detail/" + mTransactionId), null, null, null, null);
                    mInitialTransactionTagList = new ArrayList<String>();
                    while (transactionCursor.moveToNext()) {
                        mInitialTransactionTagList.add(transactionCursor.getString(transactionCursor.getColumnIndex(TransactionTagModel.KEY_NAME)));
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
            // Setup the initial Transaction Detail State
            mInitialTransactionDetailState = new InitialTransactionDetailState(mTransactionDesc, mTransactionMetadata, mTransactionDate);
            // Set the views
            ((EditText) findViewById(R.id.tran_description)).setText(mInitialTransactionDetailState.transactionDescription);
            ((EditText) findViewById(R.id.tran_description)).setSelection(mInitialTransactionDetailState.transactionDescription.length());
            ((EditText) findViewById(R.id.tran_metadata)).setText(mInitialTransactionDetailState.transactionMetadata);
            ((Button) findViewById(R.id.tran_date_btn)).setText((new SimpleDateFormat("dd/MM/yyyy")).format(mTransactionDate));
            ((Button) findViewById(R.id.tran_time_btn)).setText((new SimpleDateFormat("HH:mm")).format(mTransactionDate));
            mTransactionTagsTextView.setText(android.text.TextUtils.join(", ", mInitialTransactionTagList));
            mTransactionTagsTextView.setAdapter(new ArrayAdapter<String>(TransactionEditActivity.this, android.R.layout.simple_dropdown_item_1line, mTransactionTagSuggestionsList));
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

    /*
    * on Next Transaction Button Click
    * */
    public void nextTranBtn(View v) {
        if (mNextTranId != null) {
            Intent intent = new Intent(getBaseContext(), TransactionEditActivity.class);
            intent.putExtra("transaction_id", mNextTranId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Next Transaction Not Present", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * on Previous Transaction Button Click
    * */
    public void prevTranBtn(View v) {
        if (mPrevTranId != null) {
            Intent intent = new Intent(getBaseContext(), TransactionEditActivity.class);
            intent.putExtra("transaction_id", mPrevTranId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Previous Transaction Not Present", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Setting binding the contributions list */
    private void setUpTransactionContributionListAdapter() {
        mTransactionContributionsArrayAdapter = new TransactionContributionAdapter(TransactionEditActivity.this, R.layout.activity_transaction_edit_contribution_list_item, mTransactionContributionsList);
        mTransactionsContributionsListView.setAdapter(mTransactionContributionsArrayAdapter);
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
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        // Populate the adapter list items
        try {
            try {
                while (cursor.moveToNext()) {
                    TransactionContributionListItem transactionContributionListItem = new TransactionContributionListItem();

                    transactionContributionListItem.setId(cursor.getString(cursor.getColumnIndex(TransactionContributionModel.KEY_ROW_ID)));
                    transactionContributionListItem.setContribution(cursor.getString(cursor.getColumnIndex(TransactionContributionModel.KEY_CONTRIBUTION)));
                    transactionContributionListItem.setPersonId(cursor.getString(cursor.getColumnIndex("_id")));
                    transactionContributionListItem.setTransactionsDetailsId(mTransactionId);
                    transactionContributionListItem.setPersonName(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
                    String isConsumerText = cursor.getString(cursor.getColumnIndex(TransactionContributionModel.KEY_IS_CONSUMER));
                    try {
                        transactionContributionListItem.setConsumer(Integer.parseInt(isConsumerText) != 0);
                    } catch (Exception e) {
                        transactionContributionListItem.setConsumer(false);
                    }

                    mTransactionContributionsList.add(transactionContributionListItem);
                }
            } finally {
                cursor.close();
                setUpTransactionContributionListAdapter();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        //mContributionsCursor = null;
        mTransactionContributionsList = new ArrayList<TransactionContributionListItem>();
        setUpTransactionContributionListAdapter();
    }
}

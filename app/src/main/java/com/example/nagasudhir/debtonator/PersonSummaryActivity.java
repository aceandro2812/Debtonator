package com.example.nagasudhir.debtonator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class PersonSummaryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    String mPersonId = null;
    String mPersonName = null;
    ListView mPersonSummaryListView;
    ArrayList<PersonSummaryListItem> mPersonSummaryList = new ArrayList<PersonSummaryListItem>();
    PersonSummaryAdapter mPersonSummaryArrayAdapter;
    String mTransactionSetId = null;
    SharedPreferences mSharedPrefs;
    String mPersonSummarySortField = "consumption";
    String mPersonSummarySortOrder = "DESC";
    Spinner mPersonSummarySortFieldSpinner;
    Spinner mPersonSummarySortOrderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the transaction set Id to load from shared preferences
        mSharedPrefs = getSharedPreferences(GlobalVarClass.SHARED_PREFS_KEY, MODE_PRIVATE);
        mTransactionSetId = mSharedPrefs.getString(GlobalVarClass.CURRENT_TRAN_SET_ID_KEY, null);
        if (mTransactionSetId == null) {
            backBtn(null);
        }

        Intent intent = getIntent();
        mPersonId = intent.getExtras().getString("person_id");
        if (mPersonId == null) {
            backBtn(null);
        }
        mPersonName = intent.getExtras().getString("person_name");
        if (mPersonName != null) {
            // Display the person Name
            setTitleOfActivity(mPersonName + "'s summary");
        }

        mPersonSummaryListView = (ListView) findViewById(R.id.person_summary_contr_cons_list);

        // Incorporating the list view without scroll bar feature
        mPersonSummaryListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mPersonSummarySortFieldSpinner = (Spinner) findViewById(R.id.person_summary_sort_spinner);
        mPersonSummarySortField = mPersonSummarySortFieldSpinner.getSelectedItem().toString();
        mPersonSummarySortFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String sortField = parent.getItemAtPosition(pos).toString();
                switch (sortField){
                    case "CONTRIBUTION":
                        mPersonSummarySortField = "contribution";
                        break;
                    case "DESCRIPTION":
                        mPersonSummarySortField = "description";
                        break;
                    case "NUM_PEOPLE":
                        mPersonSummarySortField = "tran_people";
                        break;
                    case "TRANSACTION_WORTH":
                        mPersonSummarySortField = "tran_sum";
                        break;
                    default:
                        mPersonSummarySortField = "consumption";
                        break;
                }
                getSupportLoaderManager().restartLoader(0, null, PersonSummaryActivity.this);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mPersonSummarySortOrderSpinner = (Spinner) findViewById(R.id.person_summary_asc_spinner);
        mPersonSummarySortOrder = mPersonSummarySortOrderSpinner.getSelectedItem().toString();
        mPersonSummarySortOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mPersonSummarySortOrder = parent.getItemAtPosition(pos).toString();
                getSupportLoaderManager().restartLoader(0, null, PersonSummaryActivity.this);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        FloatingActionButton personViewFab = (FloatingActionButton) findViewById(R.id.person_summary_person_view_fab);
        personViewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pass in row Id to create the Content URI for a single row
                Intent personViewIntent = new Intent(getBaseContext(), PersonViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("person_id", mPersonId);
                bundle.putString("person_name", mPersonName);
                personViewIntent.putExtras(bundle);
                startActivity(personViewIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Creating a loader for populating listview from sqlite database */
        /* This statement, invokes the method onCreatedLoader() */
        getSupportLoaderManager().initLoader(0, null, this);

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
        Intent homeIntent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void setTitleOfActivity(String titleStr) {
        getSupportActionBar().setTitle(titleStr);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /*
        * Setting binding the contributions list */
    private void setUpPersonSummaryListAdapter() {
        mPersonSummaryArrayAdapter = new PersonSummaryAdapter(PersonSummaryActivity.this, R.layout.activity_person_summary_cons_list_item, mPersonSummaryList);
        mPersonSummaryListView.setAdapter(mPersonSummaryArrayAdapter);
        setListViewHeightBasedOnChildren(mPersonSummaryListView);
    }

    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
        Uri uri = Uri.parse(Person.CONTENT_URI + "/" + mPersonId + "/by_transaction_set/" + mTransactionSetId + "/" + mPersonSummarySortField + "/" + mPersonSummarySortOrder);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        // Populate the adapter list items
        try {
            mPersonSummaryList = new ArrayList<PersonSummaryListItem>();
            try {
                while (cursor.moveToNext()) {
                    PersonSummaryListItem personSummaryListItem = new PersonSummaryListItem();

                    personSummaryListItem.setPersonContribution(cursor.getDouble(cursor.getColumnIndex("contribution")));
                    personSummaryListItem.setPersonConsumption(cursor.getDouble(cursor.getColumnIndex("consumption")));
                    personSummaryListItem.setTransactionWorth(cursor.getDouble(cursor.getColumnIndex("tran_sum")));
                    personSummaryListItem.setNumTransactionPeople(cursor.getInt(cursor.getColumnIndex("tran_people")));
                    personSummaryListItem.setTransactionName(cursor.getString(cursor.getColumnIndex(TransactionModel.KEY_DESCRIPTION)));
                    mPersonSummaryList.add(personSummaryListItem);
                }
            } finally {
                cursor.close();
                setUpPersonSummaryListAdapter();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        //mContributionsCursor = null;
        mPersonSummaryList = new ArrayList<PersonSummaryListItem>();
        setUpPersonSummaryListAdapter();
    }

}

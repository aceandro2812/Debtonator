package com.example.nagasudhir.debtonator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
                v.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        mPersonSummarySortFieldSpinner = (Spinner) findViewById(R.id.person_summary_sort_spinner);
        mPersonSummarySortField = mPersonSummarySortFieldSpinner.getSelectedItem().toString();
        mPersonSummarySortFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String sortField = parent.getItemAtPosition(pos).toString();
                switch (sortField) {
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
    * * Setting binding the contributions list
    * */
    private void setUpPersonSummaryListAdapter() {
        mPersonSummaryArrayAdapter = new PersonSummaryAdapter(PersonSummaryActivity.this, R.layout.activity_person_summary_cons_list_item, mPersonSummaryList);
        mPersonSummaryListView.setAdapter(mPersonSummaryArrayAdapter);
        setListViewHeightBasedOnChildren(mPersonSummaryListView);
    }

    /*
    * * Setting the pie chart
    * */
    private void setUpPersonSummaryPieChart() {
        DecimalFormat df = new DecimalFormat("#.##");
        PieChart pieChart = (PieChart) findViewById(R.id.person_summary_pie_chart);

        List<PieEntry> entries = new ArrayList<>();
        int[] pieColors = new int[mPersonSummaryList.size()];
        for (int i = 0; i < mPersonSummaryList.size(); i++) {
            entries.add(new PieEntry(mPersonSummaryList.get(i).getPersonConsumption().floatValue(), mPersonSummaryList.get(i).getTransactionName() + " (" + df.format(mPersonSummaryList.get(i).getPersonConsumption()) + ")"));
            pieColors[i] = Color.HSVToColor(new float[]{(360 * i) / pieColors.length, 0.6f, 0.96f});
        }

        PieDataSet set = new PieDataSet(entries, null);
        set.setColors(pieColors);
        PieData data = new PieData(set);
        pieChart.setData(data);
        Description pieChartDescription = new Description();
        pieChartDescription.setText("");
        pieChart.setDescription(pieChartDescription);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawEntryLabels(false);
        data.setDrawValues(false);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.setHoleRadius(35f);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();

        PieChart contributionPieChart = (PieChart) findViewById(R.id.person_summary_contribution_pie_chart);

        entries = new ArrayList<>();
        for (int i = 0; i < mPersonSummaryList.size(); i++) {
            entries.add(new PieEntry(mPersonSummaryList.get(i).getPersonContribution().floatValue(), mPersonSummaryList.get(i).getTransactionName() + " (" + df.format(mPersonSummaryList.get(i).getPersonContribution()) + ")"));
            pieColors[i] = Color.HSVToColor(new float[]{(360 * i) / pieColors.length, 0.6f, 0.96f});
        }

        set = new PieDataSet(entries, null);
        set.setColors(pieColors);
        data = new PieData(set);
        contributionPieChart.setData(data);
        contributionPieChart.setDescription(pieChartDescription);
        contributionPieChart.setDrawHoleEnabled(true);
        contributionPieChart.setDrawEntryLabels(false);
        data.setDrawValues(false);
        contributionPieChart.setTransparentCircleRadius(25f);
        contributionPieChart.setHoleRadius(35f);
        contributionPieChart.getLegend().setWordWrapEnabled(true);
        contributionPieChart.animateXY(1000, 1000);
        contributionPieChart.invalidate();
    }

    /*
    * * Setting the Transaction Timeline
    * */
    private void setUpPersonSummaryTransactionTimeline() {
        LineChart tranTimeLineChart = (LineChart) findViewById(R.id.person_summary_transaction_timeline_chart);
        List<Entry> transactionTimeLineEntries = new ArrayList<Entry>();
        ArrayList<PersonSummaryListItem> mPersonSummaryListSortedByTime = new ArrayList<PersonSummaryListItem>(mPersonSummaryList);
        Collections.sort(mPersonSummaryListSortedByTime, new Comparator<PersonSummaryListItem>() {
            @Override
            public int compare(PersonSummaryListItem p1, PersonSummaryListItem p2) {
                try {
                    return (int) ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(p1.getTransactionTime()).getTime() - (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(p2.getTransactionTime()).getTime());
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
        long refTimeStamp = 0;
        try {
            if (mPersonSummaryListSortedByTime.size() > 0) {
                refTimeStamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(mPersonSummaryListSortedByTime.get(0).getTransactionTime()).getTime();
            }
        } catch (ParseException e) {

        }

        for (int i = 0; i < mPersonSummaryListSortedByTime.size(); i++) {
            try {
                Date tranDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(mPersonSummaryListSortedByTime.get(i).getTransactionTime());
                transactionTimeLineEntries.add(new Entry(tranDate.getTime() - refTimeStamp, mPersonSummaryListSortedByTime.get(i).getPersonConsumption().floatValue()));
            } catch (Exception e) {
                Log.e(PersonSummaryActivity.class.toString(), e.toString());
            }
        }
        LineDataSet tranTimeLineDataSet = new LineDataSet(transactionTimeLineEntries, null); // add transactionTimeLineEntries to dataset
        tranTimeLineDataSet.setDrawFilled(true);
        //tranTimeLineDataSet.setColor(Color.parseColor("#4CAF50"));
        //tranTimeLineDataSet.setCircleColor(Color.parseColor("#4CAF50"));
        //tranTimeLineDataSet.setFillColor(Color.parseColor("#4CAF50"));
        //tranTimeLineDataSet.setValueTextColor(Color.HSVToColor(new float[]{120f, 0.6f, 0.96f}));
        tranTimeLineDataSet.setDrawCircleHole(false);
        tranTimeLineDataSet.setDrawValues(false);
        LineData TranTimeLineData = new LineData(tranTimeLineDataSet);
        tranTimeLineChart.setData(TranTimeLineData);
        tranTimeLineChart.getLegend().setEnabled(false);
        tranTimeLineChart.getDescription().setEnabled(false);

        DayAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(refTimeStamp);
        XAxis xAxis = tranTimeLineChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);

        MyMarkerView myMarkerView = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view, refTimeStamp);
        tranTimeLineChart.setMarkerView(myMarkerView);

        // enable touch gestures
        tranTimeLineChart.setTouchEnabled(true);

        // enable scaling and dragging
        tranTimeLineChart.setDragEnabled(true);
        tranTimeLineChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        tranTimeLineChart.getAxisLeft().setDrawGridLines(false);
        tranTimeLineChart.getAxisRight().setDrawGridLines(false);
        tranTimeLineChart.getXAxis().setDrawGridLines(false);
        //tranTimeLineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        tranTimeLineChart.invalidate();
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
                    personSummaryListItem.setTransactionTime(cursor.getString(cursor.getColumnIndex(TransactionModel.KEY_TRANSACTION_TIME)));
                    mPersonSummaryList.add(personSummaryListItem);
                }
            } finally {
                cursor.close();
                setUpPersonSummaryListAdapter();
                setUpPersonSummaryPieChart();
                setUpPersonSummaryTransactionTimeline();
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

package com.example.nagasudhir.debtonator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class PersonSummaryActivity extends AppCompatActivity {

    String mPersonId = null;
    String mPersonName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

}

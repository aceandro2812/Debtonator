package com.example.nagasudhir.debtonator;

/**
 * Created by Nagasudhir on 9/15/2017.
 */

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private long referenceTimestamp;  // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;

    public MyMarkerView(Context context, int layoutResource, long referenceTimestamp) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("dd-MMM-yy HH:mm", Locale.ENGLISH);
        this.mDate = new Date();
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DecimalFormat df = new DecimalFormat("#.##");
        long currentTimestamp = (int) e.getX() + referenceTimestamp;

        tvContent.setText(df.format(e.getY()) + "\n" + e.getData() + "\n" + getTimedate(currentTimestamp)); // set the entry-value as the display text
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    private String getTimedate(long timestamp) {

        try {
            mDate.setTime(timestamp);
            return mDataFormat.format(mDate);
        } catch (Exception ex) {
            return "xx";
        }
    }
}
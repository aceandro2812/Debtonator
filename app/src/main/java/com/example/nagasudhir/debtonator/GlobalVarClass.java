package com.example.nagasudhir.debtonator;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nagasudhir on 8/6/2017.
 */

public class GlobalVarClass extends Application {
    public static final String SHARED_PREFS_KEY = "SHARED_PREFS_KEY";
    public static final String CURRENT_TRAN_SET_ID_KEY = "CURRENT_TRAN_SET_ID_KEY";

    public static String changeDateFormat(String originalStringFormat, String desiredStringFormat, String inDateString) {
        SimpleDateFormat readingFormat = new SimpleDateFormat(originalStringFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(desiredStringFormat);
        String outDateString = null;
        try {
            Date date = readingFormat.parse(inDateString);
            outDateString = outputFormat.format(date);
        } catch (Exception e) {
            outDateString = null;
            e.printStackTrace();
        }
        return outDateString;
    }
}

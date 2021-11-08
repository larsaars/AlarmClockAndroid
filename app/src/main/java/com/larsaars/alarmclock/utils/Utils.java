package com.larsaars.alarmclock.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // makes a time long pretty
    public static String formatTimeLong(long timestamp) {
        return Constants.prettyTime.format(new Date(timestamp));
    }

    // get the prefs
    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(Constants.DEFAULT_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

                                          // returns a string which is beautifully built (with a max len)
    public static String buildBeautifulListing(@NonNull String[] items, int maxStringLen) {
        if(items.length == 0)
            return "";

        StringBuilder outBuilder = new StringBuilder(items[0]);
        String points = "…";
        for(int i = 0; i < items.length; i++) {
            boolean appendPoints = (outBuilder.length() > maxStringLen) && (maxStringLen != -1);
            if(i == items.length - 1 && i != 0 && !appendPoints)
                outBuilder.append(" & ");
            else if(i != 0 && !appendPoints)
                outBuilder.append(", ");

            if(appendPoints) {
                outBuilder.append(points);
                break;
            }else if(i != 0) {
                outBuilder.append(items[i]);
            }
        }

        return outBuilder.toString();
    }


    public static String getCurrentTimeString() {
        DateFormat df = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return df.format(new Date());
    }
}

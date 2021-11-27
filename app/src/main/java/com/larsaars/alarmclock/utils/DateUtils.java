/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 12.11.21, 15:10
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/*
 * all time longs should be in millis
 */
public class DateUtils {
    public static String getCurrentTimeString() {
        return getTimeStringH_mm_a(System.currentTimeMillis());
    }

    public static String getTimeStringH_mm_a(long timestamp) {
        DateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return df.format(new Date(timestamp));
    }

    // makes a time long pretty
    public static String formatTimePretty(long timestamp) {
        return Constants.prettyTime.format(new Date(timestamp));
    }

    // formats for the duration method
    public static final String DURATION_FORMAT_HHhMMm = "%02dh%02dm",
            DURATION_FORMAT_HH_colon_MM = "%02d:%02d";

    // format a duration long with
    // given format string
    public static String formatDuration_HH_mm(Context context, long countdown, String format) {
        long hours = TimeUnit.MILLISECONDS.toHours(countdown),
                minutes = TimeUnit.MILLISECONDS.toMinutes(countdown) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countdown));

        if (android.text.format.DateFormat.is24HourFormat(context)) {
            return String.format(Locale.getDefault(), format, hours, minutes);
        } else {
            boolean am = true;
            if (hours > 12) {
                am = false;
                hours -= 12;
            }

            return String.format(
                    Locale.getDefault(),
                    format + " %s",
                    hours,
                    minutes,
                    am ? "AM" : "PM"
            );
        }
    }
}

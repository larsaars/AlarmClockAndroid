/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 12.11.21, 15:10
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getCurrentTimeString() {
        return getTimeStringH_mm_a(System.currentTimeMillis());
    }

    public static String getTimeStringH_mm_a(long time) {
        DateFormat df = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return df.format(new Date(time));
    }

    // makes a time long pretty
    public static String formatTimeLong(long timestamp) {
        return Constants.prettyTime.format(new Date(timestamp));
    }
}

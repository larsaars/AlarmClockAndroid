/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 08.11.21, 18:39
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.lurzapps.nhie.utility;

import android.util.Log;

import com.larsaars.alarmclock.BuildConfig;


public class Logg {
    private static final String DEFAULT_TAG_START = "AlarmClockLog -- ";

    public static Object[] m(Object... os) {
        for (Object o : os)
            l(o);

        return os;
    }

    public static <T> T l(T o) {
        String s = String.valueOf(o);

        if (o == null)
            s = "null";

        if (BuildConfig.DEBUG) {
            if (o instanceof Throwable) {
                Log.d(DEFAULT_TAG_START + getCallerClassName(), "error", (Throwable) o);
            } else {
                Log.d(DEFAULT_TAG_START + getCallerClassName(), s);
            }
        }

        return o;
    }

    public static void l(String format, Object... os) {
        l(String.format(format, os));
    }

    private static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logg.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClassName() + "." + ste.getMethodName() + ": " + ste.getLineNumber();
            }
        }
        return "UNKNOWN";
    }
}

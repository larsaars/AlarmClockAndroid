/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 16.12.21, 18:07
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

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

    public static void inputStreamToFile(InputStream input, File file) throws IOException {
        FileOutputStream output = new FileOutputStream(file);

        byte[] buffer = new byte[4 * 1024]; // or other buffer size
        int read;

        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }

        output.flush();
    }

    public static int pendingIntentFlags(int flags) {
        return flags | PendingIntent.FLAG_IMMUTABLE;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 14.12.21, 20:18
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.ui.view;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastMaker {
    private static Toast last;

    public static void make(Context context, String message) {
        if (last != null)
            last.cancel();

        last = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        last.show();
    }

    public static void make(Context context, @StringRes int resId, Object... format) {
        make(context, context.getString(resId, format));
    }
}

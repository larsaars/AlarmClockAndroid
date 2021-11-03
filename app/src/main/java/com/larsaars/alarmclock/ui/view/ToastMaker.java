/*
 *  Created by Lurzapps on 05.07.20 19:31
 *  Copyright (c) 2020 . All rights reserved.
 *  Last modified 05.07.20 19:31
 *  Project: NHIE
 */

package com.larsaars.alarmclock.ui.view;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastMaker {
    public static void make(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void make(Context context, @StringRes int resId, Object... format) {
        make(context, context.getString(resId, format));
    }
}

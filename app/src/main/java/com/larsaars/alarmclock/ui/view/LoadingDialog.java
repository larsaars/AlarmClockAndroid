/*
 *  Created by Lars Specht
 *  Copyright (c) 2022. All rights reserved.
 *  last modified by me on 03.01.22, 14:59
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.ui.view;

import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;


public class LoadingDialog {
    private static CDialog dialog;

    public static void show(Context context, boolean asSystemAlert) {
        show(context, asSystemAlert, null);
    }

    public static void show(Context context, boolean asSystemAlert, @StringRes int resId) {
        show(context, asSystemAlert, context.getString(resId));
    }

    public static void show(Context context, boolean asSystemAlert, String text) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = CDialog.dialog(context);
            dialog.setContentView(R.layout.loading_screen);
            dialog.setCancelable(false);

            // if is showing as system alert, this will be on top of EVERYTHING (even calls etc)
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            if (text != null)
                ((AppCompatTextView) dialog.findViewById(R.id.loadingScreenTv)).setText(text);

            dialog.show();
        }
    }

    public static void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}

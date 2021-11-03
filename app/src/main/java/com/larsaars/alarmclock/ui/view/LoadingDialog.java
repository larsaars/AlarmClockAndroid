/*
 *  Created by Lurzapps
 *  Copyright (c) 2020. All rights reserved.
 *  last modified by larsl on 18.08.20 18:18
 *  project NHIE in module app
 */

package com.larsaars.alarmclock.ui.view;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;


public class LoadingDialog {
    private static CDialog dialog;

    public static void show(Context context) {
        show(context, null);
    }

    public static void show(Context context, int resId) {
        show(context, context.getString(resId));
    }

    public static void show(Context context, String text) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = CDialog.dialog(context);
            dialog.setContentView(R.layout.loading_screen);
            dialog.setCancelable(false);

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

/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 26.11.21, 10:39
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.utils.Executable;

public class TimePickerDialog {
    public static void showTimePickerDialog(@NonNull Context context, @NonNull Executable<Long> time) {
        CDialog dialog = CDialog.dialog(context);
        dialog.setContentView(R.layout.time_picker_dialog);
        dialog.show();
    }
}

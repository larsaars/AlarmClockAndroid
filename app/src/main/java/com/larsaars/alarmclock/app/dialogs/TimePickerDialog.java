/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 26.11.21, 10:39
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.dialogs;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Executable;

public class TimePickerDialog {
    public static void showTimePickerDialog(@NonNull Context context, @NonNull Executable<Long> result) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.time_picker_dialog, null);

        TimePicker timePicker = rootView.findViewById(R.id.timePickerDialogTimePicker);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(context));

        CDialog.alertDialog(context)
                .setView(rootView)
                .setPositiveButton(R.string.ok, (dialog, which) ->
                    result.run(timePicker.getHour() * Constants.HOUR + timePicker.getMinute() * Constants.MINUTE)
                ).setNegativeButton(R.string.cancel, null)
                .show();
    }
}

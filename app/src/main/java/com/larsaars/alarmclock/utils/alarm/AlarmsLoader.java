/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 25.11.21, 18:32
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.alarm;

import android.content.Context;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class AlarmsLoader {
    public static List<Alarm> load(@NonNull Context context, @NonNull String key, @NonNull AlarmType type) {
        // load list of alarms from prefs
        List<Alarm> alarms = Arrays.asList(Constants.gsonExpose.fromJson(
                Utils.prefs(context).getString(key, ""),
                Alarm[].class
        ));
        // change type of items
        for (Alarm alarm : alarms)
            alarm.type = type;

        return alarms;
    }

    // save to prefs as array
    public static void save(@NonNull Context context, @NonNull String key, @NonNull List<Alarm> alarms) {
        Utils.prefs(context)
                .edit()
                .putString(
                        key,
                        Constants.gsonExpose.toJson(alarms.toArray(new Alarm[0]))
                ).apply();
    }
}

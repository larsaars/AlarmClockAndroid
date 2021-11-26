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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmsLoader {
    public static Set<Alarm> load(@NonNull Context context, @NonNull String key, @NonNull AlarmType type) {
        Set<Alarm> alarms = new HashSet<>();
        // load all current alarms from ram
        for (String alarmJson : Utils.prefs(context).getStringSet(key, new HashSet<>())) {
            Alarm alarm = Constants.gsonExpose.fromJson(alarmJson, Alarm.class);
            alarm.type = type;
            alarms.add(alarm);
        }
        return alarms;
    }

    public static void save(@NonNull Context context, @NonNull String key, @NonNull List<Alarm> alarms) {
        Set<String> alarmsJson = new HashSet<>();
        for (Alarm alarm : alarms)
            alarmsJson.add(Constants.gsonExpose.toJson(alarm));
        Utils.prefs(context).edit().putStringSet(key, alarmsJson).apply();
    }
}

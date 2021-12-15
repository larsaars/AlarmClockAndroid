/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 15.12.21, 17:52
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils;

import static com.larsaars.alarmclock.utils.Constants.COUNTDOWN_ALARMS;
import static com.larsaars.alarmclock.utils.Constants.HOUR;
import static com.larsaars.alarmclock.utils.Constants.MINUTE;
import static com.larsaars.alarmclock.utils.Constants.REGULAR_ALARMS;

import android.content.Context;

import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmType;
import com.larsaars.alarmclock.utils.alarm.AlarmsLoader;

import java.util.ArrayList;
import java.util.List;

public class DefaultActions {
    public static void addDefaultAlarms(Context context) {
        // create lists with default countdown and regular alarms
        // alarm ids are not important
        List<Alarm> countdowns = new ArrayList<>(), regular = new ArrayList<>();

        countdowns.add(new Alarm(0, MINUTE * 26, AlarmType.COUNTDOWN));
        countdowns.add(new Alarm(0, HOUR, AlarmType.COUNTDOWN));

        regular.add(new Alarm(0, HOUR * 7 + MINUTE * 30, AlarmType.REGULAR));

        // save in prefs
        AlarmsLoader.save(context, COUNTDOWN_ALARMS, countdowns);
        AlarmsLoader.save(context, REGULAR_ALARMS, regular);
    }
}

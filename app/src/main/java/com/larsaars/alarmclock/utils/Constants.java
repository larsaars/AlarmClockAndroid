/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 07.12.21, 01:23
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;

import com.google.gson.Gson;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.util.Locale;
import java.util.Random;

public class Constants {
    public static final Random random = new Random();
    public static final Handler handler = new Handler();
    public static final Gson gson = new Gson();
    public static PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

    public static final long SECOND = 1000, MINUTE = 60 * SECOND, HOUR = MINUTE * 60,
            TOLERANCE_SAME_TIME = SECOND * 40;

    public static final String DEFAULT_SHARED_PREFS_NAME = "default_prefs";

    public static final String ALARM_ID_MAX = "alarm_id",
            ACTIVE_ALARMS = "active_alarms",
            REGULAR_ALARMS = "regular_alarms",
            COUNTDOWN_ALARMS = "countdown_alarms",
            SETTINGS = "settings",
            NIGHT_MODE = "night_mode",
            TOGGLE_NIGHT_MODE = "toggle_night_mode",
            FIRST_START = "first_start";

    public static final String EXTRA_ALARM_ID = "alarm_id",
            EXTRA_EXIT = "exit",
            EXTRA_PERMISSION = "permission",
            EXTRA_SPOTIFY_LINK = "spotify_link";

    public static final String TAG = "AlarmClockLog";

    public static long[] VIBRATION_PATTERN_ALARM = new long[]{0, 400, 400, 400, 400, 400, 400, 400};
    public static final int[] VIBRATION_AMPLITUDES_ALARM = new int[]{0, 128, 0, 128, 0, 128, 0, 128};

    public static final String ACTION_NOTIFICATION_DISMISS_ALARM = "com.larsaars.alarmclock.action.DISMISS_ALARM",
            ACTION_NOTIFICATION_SNOOZE_ALARM = "com.larsaars.alarmclock.action.SNOOZE_ALARM",
            ACTION_NOTIFICATION_DISMISS_UPCOMING_ALARM = "com.larsaars.alarmclock.action.DISMISS_UPCOMING_ALARM",
            ACTION_SHOW_NOTIFICATION_OF_UPCOMING_ALARM = "com.larsaars.alarmclock.action.SHOW_NOTIFICATION_OF_UPCOMING_ALARM";

    public static final IntentFilter INTENT_FILTER_NOTIFICATION_ACTIONS;

    static {
        INTENT_FILTER_NOTIFICATION_ACTIONS = new IntentFilter();
        INTENT_FILTER_NOTIFICATION_ACTIONS.addAction(ACTION_NOTIFICATION_DISMISS_ALARM);
        INTENT_FILTER_NOTIFICATION_ACTIONS.addAction(ACTION_NOTIFICATION_SNOOZE_ALARM);
    }

    public static File DEFAULT_RINGTONE_FILE(Context context) {
        return new File(context.getFilesDir(), "ringtone.mp3");
    }
}

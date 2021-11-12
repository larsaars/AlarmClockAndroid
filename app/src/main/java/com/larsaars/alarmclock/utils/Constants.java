package com.larsaars.alarmclock.utils;

import android.content.IntentFilter;

import com.google.gson.Gson;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Locale;
import java.util.Random;

public class Constants {
    public static final Random random = new Random();
    public static final Gson gson = new Gson();
    public static PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

    public static final long MINUTE = 60000, HOUR = MINUTE * 60;

    public static final String DEFAULT_SHARED_PREFS_NAME = "default_prefs";

    public static final String ALARM_ID_MAX = "alarm_id", ALARMS = "alarms", SETTINGS = "settings";

    public static final String EXTRA_ALARM_ID = "alarm_id", EXTRA_EXIT = "exit";

    public static final String TAG = "AlarmClock";

    public static long[] VIBRATION_PATTERN_ALARM = {0, 100, 125};

    public static final String ACTION_NOTIFICATION_DISMISS_ALARM = "com.larsaars.alarmclock.action.DISMISS_ALARM",
            ACTION_NOTIFICATION_SNOOZE_ALARM = "com.larsaars.alarmclock.action.SNOOZE_ALARM";

    public static final IntentFilter INTENT_FILTER_NOTIFICATION_ACTIONS;

    static {
        INTENT_FILTER_NOTIFICATION_ACTIONS = new IntentFilter();
        INTENT_FILTER_NOTIFICATION_ACTIONS.addAction(ACTION_NOTIFICATION_DISMISS_ALARM);
        INTENT_FILTER_NOTIFICATION_ACTIONS.addAction(ACTION_NOTIFICATION_SNOOZE_ALARM);
    }
}

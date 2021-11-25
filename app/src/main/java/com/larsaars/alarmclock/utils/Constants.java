package com.larsaars.alarmclock.utils;

import android.content.IntentFilter;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Locale;
import java.util.Random;

public class Constants {
    public static final Random random = new Random();
    public static final Handler handler = new Handler();
    public static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    public static PrettyTime prettyTime = new PrettyTime(Locale.getDefault());

    public static final long SECOND = 1000, MINUTE = 60 * SECOND, HOUR = MINUTE * 60;

    public static final String DEFAULT_SHARED_PREFS_NAME = "default_prefs";

    public static final String ALARM_ID_MAX = "alarm_id",
            ACTIVE_ALARMS = "active_alarms",
            REGULAR_ALARMS = "regular_alarms",
            COUNTDOWN_ALARMS = "countdown_alarms",
            SETTINGS = "settings";

    public static final String EXTRA_ALARM_ID = "alarm_id",
            EXTRA_EXIT = "exit";

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
}

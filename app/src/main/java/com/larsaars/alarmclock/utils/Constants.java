package com.larsaars.alarmclock.utils;

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

    public static final String ALARM_ID_MAX = "alarm_id", ALARMS = "alarms";
}

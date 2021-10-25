package com.larsaars.alarmclock.utils;

import java.util.Random;

public class Constants {
    public static Random random = new Random();

    public static final long MINUTE = 60000, HOUR = MINUTE * 60;

    public static final String DEFAULT_SHARED_PREFS_NAME = "default_prefs";
}

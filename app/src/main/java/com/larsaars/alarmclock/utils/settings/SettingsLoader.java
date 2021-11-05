/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 04.11.21, 01:01
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import android.content.Context;

import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;

/* save and load the current settings
* stored in json in prefs */

public class SettingsLoader {
    public static Settings load(Context context) {
        String defaultSettings = Constants.gson.toJson(new Settings());
        return Constants.gson.fromJson(Utils.prefs(context).getString(Constants.SETTINGS, defaultSettings), Settings.class);
    }

    public static void save(Context context, Settings settings) {
        Utils.prefs(context).edit().putString(Constants.SETTINGS, Constants.gson.toJson(settings)).apply();
    }
}

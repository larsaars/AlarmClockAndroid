/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 28.11.21, 15:29
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.ui.theme;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;

public class ThemeUtils {

    public static boolean isNightModeEnabled(Context context) {
        return Utils.prefs(context).getBoolean(Constants.NIGHT_MODE, false);
    }

    public static void setIsNightModeEnabled(Context context, boolean isNightModeEnabled) {
        Utils.prefs(context).edit().putBoolean(Constants.NIGHT_MODE, isNightModeEnabled).apply();
    }

    public static void setIsToggleEnabled(Context context, boolean isToggleEnabled) {
        Utils.prefs(context).edit().putBoolean(Constants.TOGGLE_NIGHT_MODE, isToggleEnabled).apply();
    }

    public static boolean isToggleEnabled(Context context) {
        return Utils.prefs(context).getBoolean(Constants.TOGGLE_NIGHT_MODE, false);
    }

    public static boolean isDarkMode(Context activity) {
        return (activity.getResources().getConfiguration()
                .uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void ensureAutomaticThemeIsSet(Context context) {
        if (ThemeUtils.isToggleEnabled(context)) {
            AppCompatDelegate.setDefaultNightMode(ThemeUtils.isNightModeEnabled(context) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            if (ThemeUtils.isDarkMode(context)) {
                ThemeUtils.setIsNightModeEnabled(context, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                ThemeUtils.setIsNightModeEnabled(context, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}

package com.larsaars.alarmclock.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.larsaars.alarmclock.ui.theme.ThemeUtils;

public class MyApp extends Application {

    public MyApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // init firebase app
        FirebaseApp.initializeApp(this);
        // ensure toggle is supported
        if (ThemeUtils.isToggleEnabled(this)) {
            AppCompatDelegate.setDefaultNightMode(ThemeUtils.isNightModeEnabled(this) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            if (ThemeUtils.isDarkMode(this)) {
                ThemeUtils.setIsNightModeEnabled(this, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                ThemeUtils.setIsNightModeEnabled(this, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}
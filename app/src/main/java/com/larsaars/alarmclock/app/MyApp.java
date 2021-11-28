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
        ThemeUtils.ensureAutomaticThemeIsSet(this);
    }
}
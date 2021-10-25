package com.larsaars.alarmclock.app;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // init firebase app
        FirebaseApp.initializeApp(this);
    }
}
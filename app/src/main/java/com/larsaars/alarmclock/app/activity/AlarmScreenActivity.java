/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 24.10.21, 20:02
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.service.AlarmService;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.ui.view.TwoWaySlider;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

public class AlarmScreenActivity extends RootActivity {

    Alarm alarm;
    Settings settings;
    boolean exitFromActivity = true;

    TwoWaySlider twoWaySlider;
    AnimatedTextView tvTriggerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // set content
        setContentView(R.layout.activity_alarm_screen);

        // ensure the activity is also shown when screen is locked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);

        }

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        // alarm id is stored as extra in the intent
        int alarmId = getIntent().getIntExtra(Constants.EXTRA_ALARM_ID, -1);

        // get the alarm instance from the id
        alarm = new AlarmController(this).getAlarm(alarmId);

        //init other classes
        settings = SettingsLoader.load(this);

        twoWaySlider = findViewById(R.id.alarmScreenTwoWaySliderControl);
        tvTriggerTime = findViewById(R.id.alarmScreenTvTriggerTime);

        // set listeners on the two way slider
        // the right side is cancel, left side is snooze
        // perform according actions
        twoWaySlider.setListener(new TwoWaySlider.OnTwowaySliderListener() {
            @Override
            public void onSliderMoveLeft() {
                snoozeAlarm();
            }

            @Override
            public void onSliderMoveRight() {
                finish();
            }

            @Override
            public void onSliderLongPress() {

            }
        });

        // set trigger time of alarm
        tvTriggerTime.set(DateUtils.getTimeStringH_mm_a(alarm.triggerTime));

        // register receiver to exit the app
        registerReceiver(broadcastReceiverDismissOrSnooze, Constants.INTENT_FILTER_NOTIFICATION_ACTIONS);
    }

    BroadcastReceiver broadcastReceiverDismissOrSnooze = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // exit the activity immediately without interfering with stopping service in on destroy
            // since the activity has been stopped from the service
            exitFromActivity = false;
            finish();
        }
    };

    void snoozeAlarm() {
        // reschedule alarm in n millis
        AlarmController alarmController = new AlarmController(this);
        alarmController.scheduleAlarm(null, System.currentTimeMillis() + settings.snoozeCooldown);
        alarmController.save();
        // and finish this activity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (exitFromActivity) {
            // destroy service with the activity: to stop foreground,
            // pass extra (which will be passed onStartCommand)
            Intent serviceIntent = new Intent(this, AlarmService.class);
            serviceIntent.putExtra(Constants.EXTRA_EXIT, true);
            startService(serviceIntent);
        }

        unregisterReceiver(broadcastReceiverDismissOrSnooze);
    }

    // this activity is declared as singleInstance -> alarm screen will always be created in single task with one instance only
    // if the activity is opened again (by the notification for example), it will be rerouted to this if the activity already exists
    // and not be opened in another instance
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 24.10.21, 20:02
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.service.AlarmService;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

import java.io.IOException;

public class AlarmScreenActivity extends RootActivity {

    AlarmController alarmController;
    Alarm alarm;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        // ensure the activity is also shown when screen is locked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );
        }

        // alarm id is stored as extra in the intent
        int alarmId = getIntent().getIntExtra(Constants.EXTRA_ALARM_ID, -1);

        // get the alarm instance from the id
        alarmController = new AlarmController(this);
        alarm = alarmController.getAlarm(alarmId);

        //init other classes
        settings = SettingsLoader.load(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // destroy service with the activity
        stopService(new Intent(this, AlarmService.class));
    }

    // this activity is declared as singleInstance -> alarm screen will always be created in single task with one instance only
    // if the activity is opened again (by the notification for example), it will be rerouted to this if the activity already exists
    // and not be opened in another instance
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 24.10.21, 20:02
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

public class AlarmScreenActivity extends RootActivity {

    Alarm alarm;
    Settings settings;

    Vibrator vibrator;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        // alarm id is stored as extra in the intent
        int alarmId = getIntent().getIntExtra(Constants.EXTRA_ALARM_ID, -1);

        // get the alarm instance from the id
        AlarmController alarmController = new AlarmController(this);
        alarm = alarmController.getAlarm(alarmId);

        // if the alarm does not exist, exit immediately
        if(alarm == null) {
            ToastMaker.make(this, getString(R.string.internal_error));
            finish(true);
            return;
        }

        // else start ringtone, vibration, etc
        // init all classes
        settings = SettingsLoader.load(this);

        // start sound and vibration if needed etc
        if (settings.vibrationOn)
            startVibrating();

        alarmSoundHelper.start();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // disable vibrating, alarm sound
    }
}
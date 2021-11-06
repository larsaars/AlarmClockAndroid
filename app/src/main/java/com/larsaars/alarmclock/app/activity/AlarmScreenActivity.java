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

import androidx.core.app.NotificationCompat;

import com.larsaars.alarmclock.R;
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
        if (alarm == null) {
            ToastMaker.make(this, getString(R.string.internal_error));
            finish(true);
            return;
        }

        // else start ringtone, vibration, etc
        // init all classes
        settings = SettingsLoader.load(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = new MediaPlayer();

        // enable media player will repeat and the stream type to alarm sound
        mediaPlayer.setLooping(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        // and play the sound once prepared
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        // start user feedback
        startSound();
        startVibration();
        showNotification();
    }

    void showNotification() {
        // create pending intent on itself
        Intent notificationIntent = new Intent(this, AlarmScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        // build the notification, it is shown as long as the activity exists, for the effect
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.alarm_notification_channel_name))
                .setContentTitle(getString(R.string.alarm))
                .setContentText(Utils.getCurrentTimeString())
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent, true)
                .build();

        startfore
    }

    void startVibration() {
        if (!settings.vibrationOn)
            return;

        if (Build.VERSION.SDK_INT >= 26)
            vibrator.vibrate(VibrationEffect.createWaveform(Constants.VIBRATION_PATTERN_ALARM, 0));
        else
            vibrator.vibrate(Constants.VIBRATION_PATTERN_ALARM, 0);
    }

    void startSound() {
        switch (settings.alarmSoundType) {

            case SPOTIFY:
                break;
            case DEFAULT:
            default:
                // if is default sound start with media player
                // get the device default ringtone
                Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);

                // play the sound
                try {
                    mediaPlayer.setDataSource(getBaseContext(), ringtoneUri);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // disable vibrating, alarm sound on finishing the activiy
        vibrator.cancel();
        mediaPlayer.stop();
    }

    // this activity is declared as singleInstance -> alarm screen will always be created in single task with one instance only
    // if the activity is opened again (by the notification for example), it will be rerouted to this if the activity already exists
    // and not be opened in another instance
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
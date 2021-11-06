/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 06.11.21, 15:15
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.AlarmScreenActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.io.IOException;

public class AlarmService extends Service {


    Alarm alarm;
    Settings settings;

    Vibrator vibrator;
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // alarm id is stored as extra in the intent
        int alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1);

        // get the alarm instance from the id
        AlarmController alarmController = new AlarmController(this);
        alarm = alarmController.getAlarm(alarmId);

        // if the alarm does not exist, exit immediately
        if (alarm == null || alarmId == -1) {
            ToastMaker.make(this, getString(R.string.internal_error));
            stopSelf();
        } else {
            // start user feedback
            startSound();
            startVibration();
            showNotification();
        }

        // start sticky means:
        // if the device gets out of memory, on start command will be started again
        return START_NOT_STICKY;
    }

    void showNotification() {
        // create pending intent for the alarm activity (for the fullscreen screen)
        Intent notificationIntent = new Intent(this, AlarmScreenActivity.class);
        notificationIntent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        // build the notification, it is shown as the alarm sounds
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

        // start the foreground notification
        startForeground(1, notification);
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
                // play via spotify api as alarm sound

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
    public void onDestroy() {
        super.onDestroy();

        // disable vibrating, alarm sound on finishing the activiy
        vibrator.cancel();
        mediaPlayer.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
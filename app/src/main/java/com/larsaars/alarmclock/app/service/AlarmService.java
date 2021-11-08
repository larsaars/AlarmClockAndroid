/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 06.11.21, 15:15
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.service;

import android.app.Notification;
import android.app.NotificationManager;
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
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.AlarmScreenActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.lurzapps.nhie.utility.Logg;

import java.io.IOException;

public class AlarmService extends Service {

    public static boolean RUNNING = false;


    Alarm alarm;
    Settings settings;

    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // set the service is running to true
        RUNNING = true;

        // else start ringtone, vibration, etc
        // init all classes
        settings = SettingsLoader.load(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        notificationManager = NotificationManagerCompat.from(this);
        mediaPlayer = new MediaPlayer();

        // enable media player will repeat and the stream type to alarm sound
        mediaPlayer.setLooping(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        // and play the sound once prepared
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getBooleanExtra(Constants.EXTRA_EXIT, false)) {
            stopService();
        } else {
            // alarm id is stored as extra in the intent
            int alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1);

            // get the alarm instance from the id
            alarm = new AlarmController(this).getAlarm(alarmId);

            // if the alarm does not exist, exit immediately
            if (alarm == null || alarmId == -1) {
                ToastMaker.make(this, R.string.internal_error);
                stopService();
            } else {
                // start user feedback
                startSound();
                startVibration();
                showNotification();
            }
        }

        // start sticky means:
        // if the device gets out of memory, on start command will be started again
        return START_STICKY;
    }

    void stopService() {
        stopForeground(true);
        stopSelf();
    }

    void showNotification() {
        // create pending intent for the alarm activity (for the fullscreen screen)
        Intent notificationIntent = new Intent(this, AlarmScreenActivity.class);
        notificationIntent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));

        // build the notification channel
        String notificationChannelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel() : "";
        // build the notification, it is shown as the alarm sounds
        Notification notification = new NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle(getString(R.string.alarm))
                .setContentText(Utils.getCurrentTimeString())
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent, true)
                .build();


        Logg.l("starting foreground service");

        // start the foreground notification
        startForeground(1, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    String createNotificationChannel() {
        String channelName = getString(R.string.alarm_notification_channel_name);
        String channelId = "alarm_service";
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_MAX)
                .setName(channelName)
                .build();
        notificationManager.createNotificationChannel(channel);
        return channelId;
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
                // TODO
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

        // disable vibrating, alarm sound on finishing the activity
        vibrator.cancel();
        mediaPlayer.stop();

        // remove the current alarm from alarm controller and save
        AlarmController alarmController = new AlarmController(this);
        alarmController.removeAlarm(alarm);
        alarmController.save();

        // set service is not running anymore
        RUNNING = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
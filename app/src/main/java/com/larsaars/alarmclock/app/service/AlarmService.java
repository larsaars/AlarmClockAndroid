/*
 *  Created by Lars Specht
 *  Copyright (c) 2022. All rights reserved.
 *  last modified by me on 03.01.22, 15:04
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.AlarmScreenActivity;
import com.larsaars.alarmclock.app.activity.SpotifyActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.AlarmSound;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AlarmService extends Service {

    public static boolean RUNNING = false;

    Alarm alarm;
    Settings settings;

    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
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
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();

        // enable media player will repeat
        mediaPlayer.setLooping(true);
        // set playing alarm
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        // and play the sound once prepared
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        // register receiver for the notification dismiss and snooze actions
        registerReceiver(broadcastReceiverDismissOrSnooze, Constants.INTENT_FILTER_NOTIFICATION_ACTIONS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(Constants.EXTRA_EXIT, false)) {
            stopService();
        } else {
            // alarm id is stored as extra in the intent
            int alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1);

            // get the alarm instance from the id
            alarm = AlarmController.getAlarm(this, alarmId);

            // remove upcoming alarm notification
            NotificationManagerCompat.from(this).cancel(alarmId);


            // get the current timed alarm sound
            AlarmSound alarmSound = getCurrentAlarmSound();

            // start user feedback
            startSound(alarmSound);
            startVibration();
            showNotification(alarmSound);
        }

        // start sticky means:
        // if the device gets out of memory, on start command will be started again
        return START_NOT_STICKY;
    }


    BroadcastReceiver broadcastReceiverDismissOrSnooze = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.ACTION_NOTIFICATION_SNOOZE_ALARM)) {
                ToastMaker.make(getBaseContext(), R.string.notification_snoozed_alarm);
                // reschedule alarm in n millis
                AlarmController.scheduleAlarm(getBaseContext(), null, System.currentTimeMillis() + settings.snoozeCooldown, true);
            }

            // stop self
            stopService();
        }
    };

    void stopService() {
        stopForeground(true);
        stopSelf();
    }

    void showNotification(AlarmSound alarmSound) {
        // create pending intent for the alarm activity (for the fullscreen screen)
        Intent alarmScreenIntent = new Intent(this, AlarmScreenActivity.class);
        alarmScreenIntent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        alarmScreenIntent.putExtra(Constants.EXTRA_HARD_ALARM, alarmSound.hardDisabling);
        alarmScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntentForegroundActivity = PendingIntent.getActivity(this, 0, alarmScreenIntent, Utils.pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT));

        // build the broadcast pending intents which will be able to exit the
        // alarm screen app or finish it in case of dismissing
        // or snoozing the alarm over the notification actions
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, Constants.random.nextInt(), new Intent(Constants.ACTION_NOTIFICATION_SNOOZE_ALARM), Utils.pendingIntentFlags(PendingIntent.FLAG_CANCEL_CURRENT)),
                dismissPendingIntent = PendingIntent.getBroadcast(this, Constants.random.nextInt(), new Intent(Constants.ACTION_NOTIFICATION_DISMISS_ALARM), Utils.pendingIntentFlags(PendingIntent.FLAG_CANCEL_CURRENT));

        // build the notification channel
        String notificationChannelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel() : "";
        // build the notification, it is shown as the alarm sounds
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle(getString(alarmSound.hardDisabling ? R.string.hard_disabling_active : R.string.alarm))
                .setContentText(DateUtils.getTimeStringH_mm_a(getApplicationContext(), alarm.time))
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntentForegroundActivity, true);

        // if is hard to disable, don't show actions on notification
        if (!alarmSound.hardDisabling) {
            notificationBuilder.addAction(R.drawable.snooze, getString(R.string.snooze), snoozePendingIntent)
                    .addAction(R.drawable.cross, getString(R.string.dismiss), dismissPendingIntent);
        }

        // start the foreground notification
        startForeground(Constants.random.nextInt(), notificationBuilder.build());

        // and start the activity with the notification
        startActivity(alarmScreenIntent);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createWaveform(Constants.VIBRATION_PATTERN_ALARM, Constants.VIBRATION_AMPLITUDES_ALARM, 0));
        else
            vibrator.vibrate(Constants.VIBRATION_PATTERN_ALARM, 0);
    }

    // this app uses timed alarm sounds, meaning the alarm sound can vary depending
    // on the time of the day
    @NonNull
    AlarmSound getCurrentAlarmSound() {
        int currentHourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        for (AlarmSound alarmSound : settings.alarmSounds) {
            if (currentHourOfDay >= alarmSound.alarmBeginHour && currentHourOfDay <= alarmSound.alarmEndHour) {
                return alarmSound;
            }
        }

        return new AlarmSound();
    }

    void startSound(AlarmSound alarmSound) {
        if (alarmSound == null)
            return;

        switch (alarmSound.type) {
            case SPOTIFY:
                // play via spotify api as alarm sound
                // for that use the play method from the activity
                SpotifyActivity.connectAndPlay(this, false, alarmSound.content, true, result -> {
                    // if the result was bad play default sound
                    if (result != Activity.RESULT_OK)
                        playSound(Constants.DEFAULT_RINGTONE_FILE(this));
                });
                break;
            case PATH:
                playSound(new File(alarmSound.content));
                break;
            case DEFAULT:
            default:
                // if is default sound start with media player
                playSound(Constants.DEFAULT_RINGTONE_FILE(this));
                break;
        }
    }

    void playSound(File path) {
        Uri ringtoneUri = Uri.fromFile(path);

        // play the sound
        try {
            mediaPlayer.setDataSource(getBaseContext(), ringtoneUri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // disable vibrating, alarm sound on finishing the activity
        vibrator.cancel();
        mediaPlayer.stop();

        // remove the current alarm from alarm controller and save
        AlarmController.removeAlarm(this, alarm);

        // unregister broadcast receiver
        unregisterReceiver(broadcastReceiverDismissOrSnooze);

        // set service is not running anymore
        RUNNING = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
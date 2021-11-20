/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 15.11.21, 21:44
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

public class ExpectingAlarmReceiver extends BroadcastReceiver {

    NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);

        Alarm alarm = AlarmController.getAlarm(context, intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1));

        if(alarm == null)
            return;

        // show notification that alarm is to be expected
        Intent dismissIntent = new Intent(Constants.ACTION_NOTIFICATION_DISMISS_EXPECTED_ALARM);
        dismissIntent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, Utils.pendingIntentFlags(0));

        // build the notification channel
        String notificationChannelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(context) : "";
        // build the notification, it is shown as the alarm sounds
        Notification notification = new NotificationCompat.Builder(context, notificationChannelId)
                .setContentTitle(context.getString(R.string.alarm_expected))
                .setContentText(DateUtils.getTimeStringH_mm_a(alarm.triggerTime))
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.cross, context.getString(R.string.dismiss), dismissPendingIntent)
                .build();

        // show notification with the alarm id as id
        notificationManager.notify(alarm.id, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    String createNotificationChannel(Context context) {
        String channelName = context.getString(R.string.alarm_expected_notification_channel);
        String channelId = "alarm_expected";
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(channelName)
                .build();
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
}
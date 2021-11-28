/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 20.11.21, 01:41
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Logg;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

public class DismissUpcomingAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = AlarmController.getAlarm(context, intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1));

        if (alarm == null)
            return;

        // alarm should be dismissed
        AlarmController.cancelAlarm(context, alarm);

        // dismiss also the notification
        NotificationManagerCompat.from(context).cancel(alarm.id);

        // to update the main activity send another broadcast
        //if(!intent.getBooleanExtra(Constants.EXTRA_EXIT, false))
            context.sendBroadcast(new Intent(Constants.ACTION_NOTIFICATION_DISMISS_UPCOMING_ALARM));
    }
}
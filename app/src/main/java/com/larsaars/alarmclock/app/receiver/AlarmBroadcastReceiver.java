/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 06.11.21, 21:46
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.larsaars.alarmclock.app.service.AlarmService;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

/*
 * this receiver is both used when an alarm is sent
 * or the device has been rebooted
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // receive that the device has been rebooted, reschedule alarms, since after the device has been shut down, all alarms will have been shut down
        if (intent.getAction() != null && (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON"))) {
            // via the alarm controller
            AlarmController.scheduleAlarmsFromList(context);
        }
        // else: the service has been started by calling an alarm
        // start the alarm service as foreground service (from api o and up)
        // but: only if the alarm service is not already running (only the case when another alarm is running)
        else if (!AlarmService.RUNNING) {
            int alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1);

            // start the alarm service only if it is secured that the alarm object exists in the stored array
            // and the alarm id is not null
            if (alarmId != -1 && AlarmController.getAlarm(context, alarmId) != null) {
                Intent intentService = new Intent(context, AlarmService.class);
                intentService.putExtra(Constants.EXTRA_ALARM_ID, alarmId);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(intentService);
                else
                    context.startService(intentService);
            }
        }
    }
}
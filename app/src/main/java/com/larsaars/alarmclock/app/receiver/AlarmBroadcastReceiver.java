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

/*
* this receiver is both used when an alarm is sent
* or the device has been rebooted
* */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // receive that the device has been rebooted, reschedule alarms
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON"))
            startRescheduleAlarmsService();
        // else: the service has been started by calling an alarm
        // start the alarm service as foreground service (from api o and up)
        else {
            Intent intentService = new Intent(context, AlarmService.class);
            intentService.putExtra(Constants.EXTRA_ALARM_ID, intentService.getIntExtra(Constants.EXTRA_ALARM_ID, -1));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intentService);
            else
                context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService() {
    }
}
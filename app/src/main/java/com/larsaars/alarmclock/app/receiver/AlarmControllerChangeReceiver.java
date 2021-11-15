/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 15.11.21, 19:24
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.larsaars.alarmclock.utils.Constants;

public class AlarmControllerChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constants.ACTION_ALARM_CONTROLLER_CHANGE)) {

        }
    }
}
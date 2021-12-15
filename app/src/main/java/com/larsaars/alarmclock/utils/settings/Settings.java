/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 15.12.21, 17:52
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import com.larsaars.alarmclock.utils.Constants;

import java.util.ArrayList;

public class Settings {
    public boolean vibrationOn = true;
    public long snoozeCooldown = Constants.MINUTE * 5,
            durationToShowNotificationBeforeAlarm = Constants.HOUR * 2,
            rescheduleTime = Constants.MINUTE * 5;

    public ArrayList<AlarmSound> alarmSounds = new ArrayList<>();

    public static Settings defaultSettings() {
        return new Settings();
    }
}

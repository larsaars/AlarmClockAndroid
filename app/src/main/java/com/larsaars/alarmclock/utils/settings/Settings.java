/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 04.11.21, 01:00
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import com.larsaars.alarmclock.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    public boolean vibrationOn = true;
    public long snoozeCooldown = Constants.MINUTE * 5,
            timeToShowNotificationBeforeAlarm = Constants.HOUR * 2,
            activeAlarmsRescheduleByTime = Constants.MINUTE * 5;

    public List<AlarmSound> alarmSounds = new ArrayList<>();

    public static Settings defaultSettings() {
        Settings settings = new Settings();
        settings.alarmSounds.add(new AlarmSound());
        return settings;
    }
}

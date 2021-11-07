/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 04.11.21, 01:00
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import com.larsaars.alarmclock.utils.Constants;

public class Settings {
    public boolean vibrationOn = true;
    public AlarmSoundType alarmSoundType = AlarmSoundType.DEFAULT;
    public  long snoozeCooldown = Constants.MINUTE * 5;
}

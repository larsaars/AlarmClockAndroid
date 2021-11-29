/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 12.11.21, 21:40
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import android.content.Context;

import com.larsaars.alarmclock.utils.alarm.Alarm;

public class AlarmSound {
    public int alarmBeginHour = 0, alarmEndHour = 23;
    public AlarmSoundType alarmSoundType = AlarmSoundType.DEFAULT;

    public String alarmContent = "default";

    public AlarmSound() {

    }

    public AlarmSound(int begin, int end, AlarmSoundType type, String content) {
        this.alarmContent = content;
        this.alarmEndHour = end;
        this.alarmBeginHour = begin;
        this.alarmSoundType = type;
    }

    public String format(Context context) {
        return alarmContent;
    }
}

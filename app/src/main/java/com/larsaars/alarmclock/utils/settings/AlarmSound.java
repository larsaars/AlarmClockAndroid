/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 12.11.21, 21:40
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.utils.alarm.Alarm;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmSound)) return false;
        AlarmSound that = (AlarmSound) o;
        return alarmBeginHour == that.alarmBeginHour && alarmEndHour == that.alarmEndHour && alarmSoundType == that.alarmSoundType && Objects.equals(alarmContent, that.alarmContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmBeginHour, alarmEndHour, alarmSoundType, alarmContent);
    }

    @NonNull
    @Override
    public String toString() {
        return "AlarmSound{" +
                "alarmBeginHour=" + alarmBeginHour +
                ", alarmEndHour=" + alarmEndHour +
                ", alarmSoundType=" + alarmSoundType +
                ", alarmContent='" + alarmContent + '\'' +
                '}';
    }
}

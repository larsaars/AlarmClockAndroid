/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 12.11.21, 21:40
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.R;

import java.io.File;
import java.util.Objects;

public class AlarmSound {
    public int alarmBeginHour = 0, alarmEndHour = 23;
    public AlarmSoundType type = AlarmSoundType.DEFAULT;

    public String content = "default";

    public AlarmSound() {

    }

    public AlarmSound(int begin, int end, AlarmSoundType type, String content) {
        this.content = content;
        this.alarmEndHour = end;
        this.alarmBeginHour = begin;
        this.type = type;
    }

    public String format(Context context) {
        switch (type) {
            case SPOTIFY:
                return content;
            case PATH:
                return new File(content).getName();
            case DEFAULT:
            default:
                return context.getString(R.string.default_alarm_sound);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmSound)) return false;
        AlarmSound that = (AlarmSound) o;
        return alarmBeginHour == that.alarmBeginHour && alarmEndHour == that.alarmEndHour && type == that.type && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmBeginHour, alarmEndHour, type, content);
    }

    @NonNull
    @Override
    public String toString() {
        return "AlarmSound{" +
                "alarmBeginHour=" + alarmBeginHour +
                ", alarmEndHour=" + alarmEndHour +
                ", alarmSoundType=" + type +
                ", alarmContent='" + content + '\'' +
                '}';
    }
}

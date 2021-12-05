/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 05.12.21, 14:39
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.activity.customize_alarm_sounds;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.framgia.library.calendardayview.data.IEvent;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.utils.settings.AlarmSound;

import java.util.Calendar;

/**
 * Created by FRAMGIA\pham.van.khac on 07/07/2016.
 */
public class Event implements IEvent {

    public final AlarmSound alarmSound;
    private Calendar timeStart, timeEnd;
    private final String mName;
    private final int mColor;

    public Event(Context context, AlarmSound alarmSound) {
        this.alarmSound = alarmSound;
        this.mName = alarmSound.format(context);
        this.mColor = ContextCompat.getColor(context, R.color.eventColor);

        updateStartAndEndTime();
    }

    public void updateStartAndEndTime() {
        timeStart = Calendar.getInstance();
        timeStart.set(Calendar.HOUR_OF_DAY, alarmSound.alarmBeginHour);
        timeStart.set(Calendar.MINUTE, 0);

        // if end hour is last hour, set to 23:59,
        // since calendar view cannot display 24 o'clock
        int endHour = alarmSound.alarmEndHour + 1;
        int endMinute = 0;
        if (alarmSound.alarmEndHour == 23) {
            endHour = 23;
            endMinute = 59;
        }

        timeEnd = Calendar.getInstance();
        timeEnd.set(Calendar.HOUR_OF_DAY, endHour);
        timeEnd.set(Calendar.MINUTE, endMinute);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Override
    public Calendar getStartTime() {
        return timeStart;
    }

    @Override
    public Calendar getEndTime() {
        return timeEnd;
    }
}


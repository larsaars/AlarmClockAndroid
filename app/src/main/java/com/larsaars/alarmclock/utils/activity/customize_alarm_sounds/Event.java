/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 29.11.21, 00:17
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.activity.customize_alarm_sounds;

import android.graphics.Bitmap;

import com.framgia.library.calendardayview.data.IEvent;
import com.larsaars.alarmclock.utils.settings.AlarmSound;

import java.util.Calendar;

/**
 * Created by FRAMGIA\pham.van.khac on 07/07/2016.
 */
public class Event implements IEvent {

    public AlarmSound alarmSound;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private int mColor;

    public Event() {

    }

    public Event(AlarmSound alarmSound, Calendar mStartTime, Calendar mEndTime, String mName, int mColor) {
        this.alarmSound = alarmSound;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mName = mName;
        this.mColor = mColor;
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
        return mStartTime;
    }

    @Override
    public Calendar getEndTime() {
        return mEndTime;
    }
}


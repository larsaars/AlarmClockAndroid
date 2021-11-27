/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 25.11.21, 01:43
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.alarm;

import com.google.gson.annotations.SerializedName;

public enum AlarmType {
    @SerializedName("0")
    ACTIVE,
    @SerializedName("1")
    COUNTDOWN,
    @SerializedName("2")
    REGULAR
}

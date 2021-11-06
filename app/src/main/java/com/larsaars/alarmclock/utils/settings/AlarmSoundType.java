/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 06.11.21, 13:56
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.settings;

import com.google.gson.annotations.SerializedName;

public enum AlarmSoundType {
    @SerializedName("0")
    DEFAULT,
    @SerializedName("1")
    SPOTIFY,
}

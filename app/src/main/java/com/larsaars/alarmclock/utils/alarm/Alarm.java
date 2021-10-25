package com.larsaars.alarmclock.utils.alarm;

public class Alarm {
    public int id;
    public long triggerTime; // in millis


    public Alarm() {

    }

    public Alarm(int id, long triggerTime) {
        this.id = id;
        this.triggerTime = triggerTime;
    }
}

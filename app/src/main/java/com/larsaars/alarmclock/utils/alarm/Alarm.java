package com.larsaars.alarmclock.utils.alarm;

import java.util.Objects;

public class Alarm {
    public int id;
    public long triggerTime; // in millis

    public Alarm() {}

    public Alarm(int id, long triggerTime) {
        this.id = id;
        this.triggerTime = triggerTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alarm)) return false;
        Alarm alarm = (Alarm) o;
        return id == alarm.id && triggerTime == alarm.triggerTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, triggerTime);
    }
}

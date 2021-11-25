package com.larsaars.alarmclock.utils.alarm;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.larsaars.alarmclock.utils.DateUtils;

import java.util.Calendar;
import java.util.Objects;

/*
 * gson only serializes objects marked with @Expose
 */
public class Alarm implements Comparable<Alarm>{
    // the alarms id
    @Expose
    public int id;

    /*
     * if the alarm is type
     * active: trigger time in millis
     * countdown: countdown time in millis
     * regular: time of the day starting at 0 at 0 o'clock of the day in millis
     */
    @Expose
    public long time;

    // do not save the alarm type when serialized with gson
    public AlarmType alarmType = AlarmType.ACTIVE;

    public Alarm() {
    }

    public Alarm(int id, long time, AlarmType alarmType) {
        this.id = id;
        this.time = time;
        this.alarmType = alarmType;
    }

    // creates a copy of the alarm
    // as it should be in active form
    // use when making stored regular or timed alarm a scheduled (active) one
    public Alarm makeActive(Context context) {
        // if this alarm is already active, return this one
        if(alarmType == AlarmType.ACTIVE)
            return this;

        // else create new alarm with new id and set the trigger time
        // according to the formats with countdown or regular alarm
        long triggerTime, currentTime = System.currentTimeMillis();

        switch (alarmType) {
            case COUNTDOWN:
                triggerTime = currentTime + time;
                break;
            case REGULAR:
            default:
                // get zero hour of the current day
                Calendar today = Calendar.getInstance();
                zeroHour(today);
                // add to zero hour of the day the regular trigger time
                long triggerTimeToday = today.getTimeInMillis() + time;
                // if the trigger time is bigger than current time,
                // the trigger time still lays in the future, else we have to schedule
                // the alarm tomorrow (next day)
                if(triggerTimeToday > currentTime) {
                    triggerTime = triggerTimeToday;
                } else {
                    // get zero time of tomorrow
                    // for that we can just increment today for one day
                    today.add(Calendar.DATE, 1);
                    // the trigger time is tomorrow + regular alarm trigger time
                    triggerTime = today.getTimeInMillis() + time;
                }

                break;
        }

        return new Alarm(
                AlarmController.generateNewId(context),
                triggerTime,
                AlarmType.ACTIVE
        );
    }

    // sets zero hour of day in calendar instance
    private void zeroHour(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

    public String formatToText() {
        switch (alarmType) {
            case REGULAR:
                return DateUtils.formatDuration_HH_mm(time, DateUtils.DURATION_FORMAT_HH_colon_MM);
            case COUNTDOWN:
                return DateUtils.formatDuration_HH_mm(time, DateUtils.DURATION_FORMAT_HHhMMm);
            case ACTIVE:
            default:
                return DateUtils.getTimeStringH_mm_a(time);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alarm)) return false;
        Alarm alarm = (Alarm) o;
        return id == alarm.id && time == alarm.time && alarmType == alarm.alarmType;
    }

    @Override
    public int compareTo(Alarm o) {
        return Long.compare(time, o.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, alarmType);
    }

    @NonNull
    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", time=" + time +
                ", alarmType=" + alarmType +
                '}';
    }
}

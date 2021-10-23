package com.larsaars.alarmclock.utils.alarm;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class AlarmController {
    Context context;
    AlarmManager alarmManager;

    public static final long MINUTE = 60000, HOUR = MINUTE * 60;

    public AlarmController(Context context) {
        this.context = context;

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleAlarmInNMillis(long inMillis) {
        scheduleAlarm(System.currentTimeMillis() + inMillis);
    }

    // returns weather setting the alarm was successful
    public boolean scheduleAlarm(long triggerTimeExactMillis) {
        // if cannot schedule an exact alarm (can be disabled from api level 31+),
        // request the permission and cancel the action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            context.startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            return false;
        }

        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerTimeExactMillis, ));

        return true;
    }
}

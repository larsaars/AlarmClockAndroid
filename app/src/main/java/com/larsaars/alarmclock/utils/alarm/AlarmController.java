package com.larsaars.alarmclock.utils.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import com.larsaars.alarmclock.AlarmScreenActivity;
import com.larsaars.alarmclock.app.activities.MainActivity;
import com.larsaars.alarmclock.utils.Constants;

public class AlarmController {
    Context context;
    AlarmManager alarmManager;
    SharedPreferences prefs;

    int idCounter = 0;


    public AlarmController(Context context) {
        this.context = context;

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        prefs = context.getSharedPreferences(Constants.DEFAULT_SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // read id counter from storage (using counting id, not random for the alarms)
        d
    }

    public Alarm scheduleAlarmInNMillis(long inMillis) {
        return scheduleAlarm(System.currentTimeMillis() + inMillis);
    }

    // returns weather setting the alarm was successful
    public Alarm scheduleAlarm(long triggerTimeExactMillis) {
        // if cannot schedule an exact alarm (can be disabled from api level 31+),
        // request the permission and cancel the action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            context.startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            return null;
        }

        // register alarm with system
        // the first pending intent can be executed to edit the alarm
        // the second is the pending intent actually executed when alarm is triggered
        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(triggerTimeExactMillis,
                        PendingIntent.getActivity(context, 0,
                                new Intent(context, MainActivity.class), (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0))),
                getIntent());

        // remember alarm in this app
        // create instance of it and save it on storage

        Alarm alarm = new Alarm()

        // return instance of this alarm in case it shall be unregistered (for direct view updates)
        return alarm;
    }

    // intent which will be executed when alarm is triggered
    private PendingIntent getIntent() {
        Intent intent = new Intent(context, AlarmScreenActivity.class);
        return PendingIntent.getActivity(context, 0, intent, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
    }

    public void cancelAlarm()
}

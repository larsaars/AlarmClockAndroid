package com.larsaars.alarmclock.utils.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.larsaars.alarmclock.app.activity.MainActivity;
import com.larsaars.alarmclock.app.receiver.AlarmBroadcastReceiver;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmController {
    // the current max id
    private static int idCounter(Context context) {
        return Utils.prefs(context).getInt(Constants.ALARM_ID_MAX, 1);
    }

    // load the alarms list
    private static Set<Alarm> alarms(Context context) {
        Set<Alarm> alarms = new HashSet<>();
        // load all current alarms from ram
        for (String alarmJson : Utils.prefs(context).getStringSet(Constants.ALARMS, new HashSet<>()))
            alarms.add(Constants.gson.fromJson(alarmJson, Alarm.class));
        return alarms;
    }

    // init the alarm manager
    private static AlarmManager alarmManager(@NonNull Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    // returns weather setting the alarm was successful
    // pass the alarm instance as null if is new alarm and only the trigger time
    // else pass the old alarm object and the triggerTime long can be 0
    @Nullable
    public static Alarm scheduleAlarm(@NonNull Context context, @Nullable Alarm alarm, long triggerTimeExactMillis) {
        AlarmManager alarmManager = alarmManager(context);

        // if cannot schedule an exact alarm (can be disabled from api level 31+),
        // request the permission and cancel the action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            context.startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            return null;
        }


        // remember alarm in this app
        // create instance of it and save it on storage
        // using an counting id instead of random id for alarms
        if (alarm == null) {
            int idCounter = idCounter(context) + 1;
            alarm = new Alarm(idCounter, triggerTimeExactMillis);
            saveIdCounter(context, idCounter);

            Set<Alarm> alarms = alarms(context);
            alarms.add(alarm);
            saveAlarms(context, alarms);
        }

        // register alarm with system
        // the first pending intent can be executed to edit the alarm
        // the second is the pending intent actually executed when alarm is triggered
        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(alarm.triggerTime,
                        PendingIntent.getActivity(context, 0,
                                new Intent(context, MainActivity.class), Utils.pendingIntentFlags(0))),
                getIntent(context, alarm, 0));
        // return instance of this alarm in case it shall be unregistered (for direct view updates)
        return alarm;
    }


    // schedule the alarms from that were unscheduled by the system on restart
    public static void scheduleAlarmsFromList(@NonNull Context context) {
        long currentTime = System.currentTimeMillis();
        for(Alarm alarm : alarms(context)) {
            if(alarm.triggerTime > currentTime)
                scheduleAlarm(context, alarm, -1);
        }
    }

    // intent which will be executed when alarm is triggered
    private static PendingIntent getIntent(@NonNull Context context, @NonNull Alarm alarm, int flags) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        return PendingIntent.getBroadcast(context, alarm.id, intent, Utils.pendingIntentFlags(flags));
    }

    // cancel specific alarm
    public static void disableAlarm(@NonNull Context context, @NonNull Alarm alarm) {
        // get the pending intent instance without creating it
        PendingIntent pendingIntent = getIntent(context, alarm, PendingIntent.FLAG_NO_CREATE);
        // if it exists, cancel it
        if (pendingIntent != null)
            alarmManager(context).cancel(pendingIntent);

        // remove alarm from the list
        removeAlarm(context, alarm);
    }

    // remove an alarm after it has served its purpose
    public static void removeAlarm(@NonNull Context context, @NonNull Alarm alarm) {
        Set<Alarm> alarms = alarms(context);
        alarms.remove(alarm);
        saveAlarms(context, alarms);
    }

    // get alarm from its id
    @Nullable
    public static Alarm getAlarm(@NonNull Context context, int id) {
        for (Alarm alarm : alarms(context))
            if (alarm.id == id)
                return alarm;
        return null;
    }

    private static void saveAlarms(@NonNull Context context, @NonNull Set<Alarm> alarms) {
        long oneDayAgo = System.currentTimeMillis() - Constants.HOUR * 24;
        // save the alarms
        Set<String> alarmsJson = new HashSet<>();
        for (Alarm alarm : alarms) {
            // add alarms on saving only if they are not already in the past for over a day
            if (alarm.triggerTime > oneDayAgo)
                alarmsJson.add(Constants.gson.toJson(alarm));
        }
        Utils.prefs(context).edit().putStringSet(Constants.ALARMS, alarmsJson).apply();
    }

    private static void saveIdCounter(@NonNull Context context, int idCount)  {
        Utils.prefs(context).edit().putInt(Constants.ALARM_ID_MAX, idCount).apply();
    }



    // returns the alarm which will go off next
    @Nullable
    public static Alarm getNextAlarm(@NonNull Context context) {
        // searching 'smallest' time (long),
        // also all time longs have to be over the current time
        long currentTime = System.currentTimeMillis();

        long smallestTime = Long.MAX_VALUE;
        Alarm smallest = null;

        for (Alarm alarm : alarms(context))
            if (alarm.triggerTime < smallestTime && alarm.triggerTime > currentTime) {
                smallestTime = alarm.triggerTime;
                smallest = alarm;
            }

        return smallest;
    }
}

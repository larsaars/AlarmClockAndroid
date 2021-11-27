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
import com.larsaars.alarmclock.app.receiver.ExpectingAlarmReceiver;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Logg;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AlarmController {
    // generate new id:
    // read old max id, increment and save the new id
    public static int generateNewId(@NonNull Context context) {
        SharedPreferences prefs = Utils.prefs(context);
        int newId = prefs.getInt(Constants.ALARM_ID_MAX, 0) + 1;
        prefs.edit().putInt(Constants.ALARM_ID_MAX, newId).apply();
        return newId;
    }

    // load the alarms list
    private static List<Alarm> alarms(Context context) {
        return AlarmsLoader.load(context, Constants.ACTIVE_ALARMS, AlarmType.ACTIVE);
    }

    // return only the alarms that are really upcoming
    // and not already in the past
    public static List<Alarm> activeAlarms(Context context) {
        List<Alarm> alarms = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (Alarm alarm : alarms(context)) {
            if (alarm.time > currentTime)
                alarms.add(alarm);
        }

        // sort the list
        Collections.sort(alarms);

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
    public static Alarm scheduleAlarm(@NonNull Context context, @Nullable Alarm alarm, long triggerTimeExactMillis, boolean newAlarm) {
        AlarmManager alarmManager = alarmManager(context);

        // if cannot schedule an exact alarm (can be disabled from api level 31+),
        // request the permission and cancel the action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            context.startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            return null;
        }

        // also return null if triggerTimeMillis is smaller current time and is not -1
        if (triggerTimeExactMillis != -1 && triggerTimeExactMillis < System.currentTimeMillis())
            return null;

        // load list of all alarms
        List<Alarm> alarms = alarms(context);

        // broadcast intent for expecting alarm notification
        Intent expectingAlarmReceiverIntent = new Intent(context, ExpectingAlarmReceiver.class);
        expectingAlarmReceiverIntent.setAction(Constants.ACTION_SHOW_NOTIFICATION_OF_UPCOMING_ALARM);
        // the trigger time of the expect alarm
        long timeToShowNotificationBeforeAlarm = SettingsLoader.load(context).timeToShowNotificationBeforeAlarm,
                expectedAlarmTriggerTime = triggerTimeExactMillis - timeToShowNotificationBeforeAlarm;

        // remember alarm in this app
        // create instance of it and save it on storage
        // using an counting id instead of random id for alarms
        if (alarm == null) {
            alarm = new Alarm(generateNewId(context), triggerTimeExactMillis, AlarmType.ACTIVE);
        } else {
            expectedAlarmTriggerTime = alarm.time - timeToShowNotificationBeforeAlarm;
            // make this alarm active one if countdown or regular is handed over
            alarm = alarm.makeActive(context);
        }

        // don't add alarm if another alarm in the same interval already exists (return null)
        for(Alarm other : alarms) {
            if(Math.abs(other.time - alarm.time) < Constants.TOLERANCE_SAME_TIME)
                return null;
        }

        // if the alarm is a new one, save to list
        // else just schedule
        if (newAlarm) {
            alarms.add(alarm);
            saveAlarms(context, alarms);
        }

        // put extra alarm id in the upcoming alarm pending
        expectingAlarmReceiverIntent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);

        // register alarm with system
        // the first pending intent can be executed to edit the alarm
        // the second is the pending intent actually executed when alarm is triggered
        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(alarm.time,
                        PendingIntent.getActivity(context, Constants.random.nextInt(),
                                new Intent(context, MainActivity.class), Utils.pendingIntentFlags(0))),
                getIntent(context, alarm, 0));

        // if the expected trigger time is lower than current time, start the notification now
        if (expectedAlarmTriggerTime < System.currentTimeMillis()) {
            context.sendBroadcast(expectingAlarmReceiverIntent);
        } else {
            // schedule inexact alarm a specific time before the actual alarm for expecting alarm notification
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    expectedAlarmTriggerTime,
                    PendingIntent.getBroadcast(context, Constants.random.nextInt(), expectingAlarmReceiverIntent, Utils.pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT))
            );
        }

        // return instance of this alarm in case it shall be unregistered (for direct view updates)
        return alarm;
    }


    // schedule the alarms from that were unscheduled by the system on restart
    public static void scheduleAlarmsFromList(@NonNull Context context) {
        long currentTime = System.currentTimeMillis();
        for (Alarm alarm : alarms(context)) {
            if (alarm.time > currentTime)
                scheduleAlarm(context, alarm, -1, false);
        }
    }

    // intent which will be executed when alarm is triggered
    private static PendingIntent getIntent(@NonNull Context context, @NonNull Alarm alarm, int flags) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        return PendingIntent.getBroadcast(context, alarm.id, intent, Utils.pendingIntentFlags(flags));
    }

    // cancel specific alarm
    public static void cancelAlarm(@NonNull Context context, @NonNull Alarm alarm) {
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
        List<Alarm> alarms = alarms(context);
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

    private static void saveAlarms(@NonNull Context context, @NonNull List<Alarm> alarms) {
        long oneDayAgo = System.currentTimeMillis() - Constants.HOUR * 24;
        // filter alarms out that are older than one day
        Iterator<Alarm> iterator = alarms.iterator();
        while (iterator.hasNext()) {
            Alarm next = iterator.next();

            if (next.time < oneDayAgo)
                iterator.remove();
        }

        // save in prefs as array
        Utils.prefs(context).edit().putString(
                Constants.ACTIVE_ALARMS,
                Constants.gson.toJson(alarms.toArray(new Alarm[0]))
        ).apply();


        Logg.l(Utils.prefs(context).getString(Constants.ACTIVE_ALARMS, "[]"));
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
            if (alarm.time < smallestTime && alarm.time > currentTime) {
                smallestTime = alarm.time;
                smallest = alarm;
            }

        return smallest;
    }
}

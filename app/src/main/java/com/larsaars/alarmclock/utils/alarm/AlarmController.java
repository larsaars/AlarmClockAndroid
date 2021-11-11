package com.larsaars.alarmclock.utils.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.larsaars.alarmclock.app.activity.MainActivity;
import com.larsaars.alarmclock.app.receiver.AlarmBroadcastReceiver;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class AlarmController {
    Context context;
    AlarmManager alarmManager;
    SharedPreferences prefs;

    // temporary counter of the ids
    int idCounter;

    // list in ram of the registered alarms
    public Set<Alarm> alarms = new HashSet<>();


    public AlarmController(Context context) {
        this.context = context;

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        prefs = Utils.prefs(context);

        // read id counter from storage (using counting id, not random for the alarms)
        idCounter = prefs.getInt(Constants.ALARM_ID_MAX, 1);

        // load all current alarms from ram
        for (String alarmJson : prefs.getStringSet(Constants.ALARMS, new HashSet<>()))
            alarms.add(Constants.gson.fromJson(alarmJson, Alarm.class));
    }

    // returns weather setting the alarm was successful
    // pass the alarm instance as null if is new alarm and only the trigger time
    // else pass the old alarm object and the triggerTime long can be 0
    @Nullable
    public Alarm scheduleAlarm(Alarm alarm, long triggerTimeExactMillis) {
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
            alarm = new Alarm(idCounter++, triggerTimeExactMillis);
            alarms.add(alarm);
        }

        // register alarm with system
        // the first pending intent can be executed to edit the alarm
        // the second is the pending intent actually executed when alarm is triggered
        alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(alarm.triggerTime,
                        PendingIntent.getActivity(context, 0,
                                new Intent(context, MainActivity.class), (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0))),
                getIntent(alarm, 0));
        // return instance of this alarm in case it shall be unregistered (for direct view updates)
        return alarm;
    }

    // intent which will be executed when alarm is triggered
    private PendingIntent getIntent(Alarm alarm, int flags) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
        return PendingIntent.getBroadcast(context, alarm.id, intent, flags | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
    }

    // cancel specific alarm
    public void disableAlarm(Alarm alarm) {
        // get the pending intent instance without creating it
        PendingIntent pendingIntent = getIntent(alarm, PendingIntent.FLAG_NO_CREATE);
        // if it exists, cancel it
        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);

        // remove alarm from the list
        alarms.remove(alarm);
    }

    // remove an alarm after it has served its purpose
    public void removeAlarm(Alarm alarm) {
        alarms.remove(alarm);
    }

    // get alarm from its id
    @Nullable
    public Alarm getAlarm(int id) {
        for (Alarm alarm : alarms)
            if (alarm.id == id)
                return alarm;
        return null;
    }

    // should be called on application pause, everything is saved as string set
    public void save() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences.Editor editor = prefs.edit();
        // save the alarms
        Set<String> alarmsJson = new HashSet<>();
        for (Alarm alarm : alarms) {
            // add alarms on saving only if they are not already in the past
            if (alarm.triggerTime > currentTime)
                alarmsJson.add(Constants.gson.toJson(alarm));
        }
        editor.putStringSet(Constants.ALARMS, alarmsJson);
        // and id counter
        editor.putInt(Constants.ALARM_ID_MAX, idCounter);
        // apply the changes
        editor.apply();
    }

    // returns the alarm which will go off next
    @Nullable
    public Alarm getNextAlarm() {
        // searching 'smallest' time (long),
        // also all time longs have to be over the current time
        long currentTime = System.currentTimeMillis();

        long smallestTime = Long.MAX_VALUE;
        Alarm smallest = null;

        for (Alarm alarm : alarms)
            if (alarm.triggerTime < smallestTime && alarm.triggerTime > currentTime) {
                smallestTime = alarm.triggerTime;
                smallest = alarm;
            }

        return smallest;
    }
}

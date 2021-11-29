/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 28.11.21, 23:52
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.PopupView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.data.IPopup;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.activity.customize_alarm_sounds.Event;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.settings.AlarmSound;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomizeAlarmSoundsActivity extends RootActivity {
    CalendarDayView dayView;

    Settings settings;
    List<IEvent> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_alarm_sounds);

        // load vars
        settings = SettingsLoader.load(this);

        // init views
        dayView = findViewById(R.id.customizeDayView);

        // on event click listener
        ((CdvDecorationDefault) (dayView.getDecoration())).setOnEventClickListener(
                new EventView.OnEventClickListener() {
                    @Override
                    public void onEventClick(EventView view, IEvent data) {
                    }

                    @Override
                    public void onEventViewClick(View view, EventView eventView, IEvent data) {
                        if (data instanceof Event) {
                            // show edit dialog
                            showEditEventDialog((Event) data);
                            // update events
                            dayView.setEvents(events);
                        }
                    }
                });

        // load timed alarm sounds as events
        int eventColor = ContextCompat.getColor(this, R.color.eventColor);
        for (AlarmSound alarmSound : settings.alarmSounds) {
            Calendar timeStart = Calendar.getInstance();
            timeStart.set(Calendar.HOUR_OF_DAY, alarmSound.alarmBeginHour);
            timeStart.set(Calendar.MINUTE, 0);

            // if end hour is last hour, set to 23:59,
            // since calendar view cannot display 24 o'clock
            int endHour = alarmSound.alarmEndHour + 1;
            int endMinute = 0;
            if(alarmSound.alarmEndHour == 23) {
                endHour = 23;
                endMinute = 59;
            }

            Calendar timeEnd = Calendar.getInstance();
            timeEnd.set(Calendar.HOUR_OF_DAY, endHour);
            timeEnd.set(Calendar.MINUTE, endMinute);

            Event event = new Event(alarmSound, timeStart, timeEnd, alarmSound.format(this), eventColor);
            events.add(event);
        }

        dayView.setEvents(events);
    }

    void showEditEventDialog(Event event) {
        // empty event (null) means new event shall be created

        // inflate view
        View rootView = getLayoutInflater().inflate(R.layout.dialog_customize_alarm_sound, null);

        CDialog.alertDialog(this)
                .setView(rootView)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_alarms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuConfigAdd) {
            showEditEventDialog(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SettingsLoader.save(this, settings);
    }
}

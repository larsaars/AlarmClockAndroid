/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 28.11.21, 23:52
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.PopupView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.data.IPopup;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
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
    List<IEvent> events;
    List<IPopup> popups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_alarm_sounds);

        // load vars
        settings = SettingsLoader.load(this);

        // init views
        dayView = findViewById(R.id.customizeDayView);


        ((CdvDecorationDefault) (dayView.getDecoration())).setOnEventClickListener(
                new EventView.OnEventClickListener() {
                    @Override
                    public void onEventClick(EventView view, IEvent data) {
                        Log.e("TAG", "onEventClick:" + data.getName());
                    }

                    @Override
                    public void onEventViewClick(View view, EventView eventView, IEvent data) {
                        Log.e("TAG", "onEventViewClick:" + data.getName());
                        if (data instanceof Event) {
                            // change event (ex: set event color)
                            dayView.setEvents(events);
                        }
                    }
                });

        ((CdvDecorationDefault) (dayView.getDecoration())).setOnPopupClickListener(
                new PopupView.OnEventPopupClickListener() {
                    @Override
                    public void onPopupClick(PopupView view, IPopup data) {
                        Log.e("TAG", "onPopupClick:" + data.getTitle());
                    }

                    @Override
                    public void onQuoteClick(PopupView view, IPopup data) {
                        Log.e("TAG", "onQuoteClick:" + data.getTitle());
                    }

                    @Override
                    public void onLoadData(PopupView view, ImageView start, ImageView end,
                                           IPopup data) {
                        start.setImageResource(R.drawable.ic_launcher);
                    }
                });

        events = new ArrayList<>();

        {
            int eventColor = ContextCompat.getColor(this, R.color.eventColor);
            Calendar timeStart = Calendar.getInstance();
            timeStart.set(Calendar.HOUR_OF_DAY, 11);
            timeStart.set(Calendar.MINUTE, 0);
            Calendar timeEnd = (Calendar) timeStart.clone();
            timeEnd.set(Calendar.HOUR_OF_DAY, 15);
            timeEnd.set(Calendar.MINUTE, 30);
            Event event = new Event(new AlarmSound(), timeStart, timeEnd, "Event",  eventColor);

            events.add(event);
        }

        {
            int eventColor = ContextCompat.getColor(this, R.color.eventColor);
            Calendar timeStart = Calendar.getInstance();
            timeStart.set(Calendar.HOUR_OF_DAY, 18);
            timeStart.set(Calendar.MINUTE, 0);
            Calendar timeEnd = (Calendar) timeStart.clone();
            timeEnd.set(Calendar.HOUR_OF_DAY, 20);
            timeEnd.set(Calendar.MINUTE, 30);
            Event event = new Event(new AlarmSound(), timeStart, timeEnd, "Another event",  eventColor);

            events.add(event);
        }

        popups = new ArrayList<>();
        dayView.setEvents(events);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SettingsLoader.save(this, settings);
    }
}

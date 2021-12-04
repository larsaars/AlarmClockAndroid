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

import android.content.DialogInterface;
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
import com.google.android.material.slider.RangeSlider;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.activity.customize_alarm_sounds.Event;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.settings.AlarmSound;
import com.larsaars.alarmclock.utils.settings.AlarmSoundType;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomizeAlarmSoundsActivity extends RootActivity {
    CalendarDayView dayView;

    Settings settings;
    List<IEvent> events = new ArrayList<>();

    boolean editDialogShowing = false;

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
                            showEditEventDialog((Event) data, false);
                            // update events
                            dayView.setEvents(events);
                        }
                    }
                });

        // load timed alarm sounds as events
        for (AlarmSound alarmSound : settings.alarmSounds) {
            events.add(new Event(this, alarmSound));
        }

        dayView.setEvents(events);
    }

    void showEditEventDialog(Event event, boolean isNewEvent) {
        if (editDialogShowing)
            return;

        // if event is null
        // first select a new source
        if (event == null) {
            File path = new File(getFilesDir(), Math.abs(Constants.random.nextInt()) + ".mp3");
            SettingsActivity.changeRingtoneWithPermissionCheck(this,
                    path,
                    success -> {
                        if (success) showEditEventDialog(
                                new Event(this, new AlarmSound(13, 15, AlarmSoundType.PATH, path.getAbsolutePath())),
                                true
                        );
                    });
            return;
        }

        // empty event (null) means new event shall be created
        // inflate view
        View rootView = getLayoutInflater().inflate(R.layout.dialog_customize_alarm_sound, null);

        final RangeSlider rangeSlider = rootView.findViewById(R.id.customizeRangeSlider);

        // init values of range slider
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(23);
        rangeSlider.setStepSize(1);
        rangeSlider.setValues((float) event.alarmSound.alarmBeginHour, (float) event.alarmSound.alarmEndHour);

        CDialog.alertDialog(this)
                .setView(rootView)
                .setOnCancelListener(dialog -> editDialogShowing = false)
                .setOnDismissListener(dialog -> editDialogShowing = false)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // update this events
                    List<Float> values = rangeSlider.getValues();
                    event.alarmSound.alarmBeginHour = values.get(0).intValue();
                    event.alarmSound.alarmEndHour = values.get(1).intValue();
                    // update calendar variables in event object
                    event.updateStartAndEndTime();
                    // if is new event add to list
                    if (isNewEvent)
                        events.add(event);
                    // update events list
                    dayView.setEvents(events);
                }).setNeutralButton(R.string.delete, (dialog, which) -> {
            // delete the alarm
            if (event.alarmSound.alarmSoundType == AlarmSoundType.PATH)
                //noinspection ResultOfMethodCallIgnored
                new File(event.alarmSound.alarmContent).delete();
            // and event out of list, as well as the alarm tone
            settings.alarmSounds.remove(event.alarmSound);
            events.remove(event);
            // update events list
            dayView.setEvents(events);
        }).show();

        editDialogShowing = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_alarms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuConfigAdd) {
            showEditEventDialog(null, true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SettingsLoader.save(this, settings);
    }
}

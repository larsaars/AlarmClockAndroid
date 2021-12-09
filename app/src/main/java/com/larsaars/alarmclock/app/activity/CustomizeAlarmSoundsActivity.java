/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 06.12.21, 17:10
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;
import com.google.android.material.slider.RangeSlider;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.activity.customize_alarm_sounds.Event;
import com.larsaars.alarmclock.utils.settings.AlarmSound;
import com.larsaars.alarmclock.utils.settings.AlarmSoundType;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.io.File;
import java.util.ArrayList;
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
                        }
                    }
                });
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
                    // get values off from range slider
                    List<Float> values = rangeSlider.getValues();
                    int beginHour = values.get(0).intValue();
                    int endHour = values.get(1).intValue();

                    // return this method if intersects with already existing events
                    if (beginHour >= event.alarmSound.alarmBeginHour && endHour <= event.alarmSound.alarmEndHour) {
                        ToastMaker.make(getApplicationContext(), R.string.alarm_time_intersects_with_other_alarm);
                        return;
                    }

                    // update this events
                    event.alarmSound.alarmBeginHour = beginHour;
                    event.alarmSound.alarmEndHour = endHour;
                    // update calendar variables in event object
                    event.updateStartAndEndTime();
                    // if is new event add to list
                    if (isNewEvent) {
                        settings.alarmSounds.add(event.alarmSound);
                        events.add(event);
                    }
                    // update events list
                    dayView.setEvents(events);
                }).setNeutralButton(R.string.delete, (dialog, which) -> {
            // delete the alarm
            if (event.alarmSound.type == AlarmSoundType.PATH)
                //noinspection ResultOfMethodCallIgnored
                new File(event.alarmSound.content).delete();
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
/*
        if (item.getItemId() == R.id.menuConfigAdd) {
            showEditEventDialog(null, true);
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // load vars
        settings = SettingsLoader.load(this);
        // load timed alarm sounds as events
        for (AlarmSound alarmSound : settings.alarmSounds) {
            events.add(new Event(this, alarmSound));
        }

        dayView.setEvents(events);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SettingsLoader.save(this, settings);
    }
}

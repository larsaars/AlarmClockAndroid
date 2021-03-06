/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 21.12.21, 02:44
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.framgia.library.calendardayview.CalendarDayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;
import com.google.android.material.slider.RangeSlider;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.dialogs.SpotifyPickerDialog;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Logg;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.activity.customize_alarm_sounds.Event;
import com.larsaars.alarmclock.utils.settings.AlarmSound;
import com.larsaars.alarmclock.utils.settings.AlarmSoundType;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

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

        // limit to one day
        dayView.setLimitTime(0, 23);

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
                            // with belonging alarm sound type
                            Event event = (Event) data;
                            showEditEventDialog(event, false, event.alarmSound.type);
                        }
                    }
                });

        // show tutorial if asked for in intent extra
        if (getIntent().getBooleanExtra(Constants.EXTRA_SHOW_TUTORIAL, false))
            showTutorial();
    }


    void showTutorial() {
        // show queue
        new FancyShowCaseQueue()
                .add(new FancyShowCaseView.Builder(this)
                        .title(getString(R.string.tutorial_customize_msg1))
                        .build())
                .show();
    }

    /*
     * show the event dialog;
     * arguments specify if is new event and the type of the alarm sound
     */
    void showEditEventDialog(Event event, boolean isNewEvent, AlarmSoundType alarmSoundType) {
        if (editDialogShowing)
            return;

        // if event is null
        // first select a new source
        if (event == null) {
            // if alarm sound type is file
            if (alarmSoundType == AlarmSoundType.PATH) {
                File path = new File(getFilesDir(), Math.abs(Constants.random.nextInt()) + ".mp3");
                SettingsActivity.changeRingtoneWithPermissionCheck(this,
                        path,
                        success -> {
                            if (success) showEditEventDialog(
                                    new Event(this, new AlarmSound(13, 15, alarmSoundType, path.getAbsolutePath())),
                                    true,
                                    alarmSoundType
                            );
                        });
            } else if (alarmSoundType == AlarmSoundType.SPOTIFY) {
                // start the spotify play activity for authorization testing
                // or authorizing if necessary
                // with no song to play as extra, since this is not the purpose
                activityLauncher.launch(
                        new Intent(getApplicationContext(), SpotifyActivity.class),
                        result -> {
                            if (result.getResultCode() == RESULT_OK) {
                                // now open dialog to enter the spotify song link,
                                // on result (success) add spotify link
                                SpotifyPickerDialog.show(this, spotifyLink -> showEditEventDialog(
                                        new Event(this, new AlarmSound(13, 15, alarmSoundType, spotifyLink)),
                                        true,
                                        alarmSoundType
                                ));
                            }
                        }
                );
            } else if (alarmSoundType == AlarmSoundType.DEFAULT) {
                // add a default alarm event
                showEditEventDialog(
                        new Event(this, new AlarmSound(13, 15, alarmSoundType, "default")),
                        true,
                        alarmSoundType
                );
            }
            return;
        }

        // empty event (null) means new event shall be created
        // inflate view
        View rootView = getLayoutInflater().inflate(R.layout.dialog_customize_alarm_sound, null);

        RangeSlider rangeSlider = rootView.findViewById(R.id.customizeRangeSlider);
        SwitchCompat hardDisablingSwitch = rootView.findViewById(R.id.customizeHardDisabling);

        // init values of range slider
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(23);
        rangeSlider.setStepSize(1);
        rangeSlider.setValues((float) event.alarmSound.alarmBeginHour, (float) event.alarmSound.alarmEndHour);

        // and of switch
        hardDisablingSwitch.setChecked(event.alarmSound.hardDisabling);

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
                    for (IEvent ie : events) {
                        if (ie == event)
                            continue;

                        Event e = (Event) ie;

                        Logg.l(ie);

                        if (Math.min(endHour, e.alarmSound.alarmEndHour) - Math.max(beginHour, e.alarmSound.alarmBeginHour) >= 0) {
                            ToastMaker.make(getApplicationContext(), R.string.alarm_time_intersects_with_other_alarm);
                            return;
                        }
                    }

                    // update this events
                    event.alarmSound.alarmBeginHour = beginHour;
                    event.alarmSound.alarmEndHour = endHour;
                    event.alarmSound.hardDisabling = hardDisablingSwitch.isChecked();
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

        if (!Utils.isPackageInstalled(getString(R.string.spotify_package_name), getPackageManager())) {
            menu.findItem(R.id.menuConfigAddAlarmSpotify).setVisible(false);
            invalidateOptionsMenu();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuConfigAddAlarmSpotify)
            showEditEventDialog(null, true, AlarmSoundType.SPOTIFY);
        else if (id == R.id.menuConfigAddAlarmFile)
            showEditEventDialog(null, true, AlarmSoundType.PATH);
        else if (id == R.id.menuConfigAddAlarmDefault)
            showEditEventDialog(null, true, AlarmSoundType.DEFAULT);

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

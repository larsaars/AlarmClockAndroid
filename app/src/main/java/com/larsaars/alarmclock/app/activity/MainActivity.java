/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 15.12.21, 17:41
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationManagerCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.dialogs.TimePickerDialog;
import com.larsaars.alarmclock.ui.adapter.ActiveAlarmsAdapter;
import com.larsaars.alarmclock.ui.adapter.RegularAndCountdownAdapter;
import com.larsaars.alarmclock.ui.etc.GridAutofitLayoutManager;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.GridRecyclerView;
import com.larsaars.alarmclock.ui.view.clickableiv.RotatingClickableImageView;
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.DefaultActions;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.alarm.AlarmType;
import com.larsaars.alarmclock.utils.alarm.AlarmsLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class MainActivity extends RootActivity {

    GridRecyclerView rvActiveAlarms, rvCountdownAlarms, dragLvRegularAlarms;
    AppCompatTextView tvNextAlarm;
    RotatingClickableImageView ivSettings;
    ShiftingClickableImageView ivAddCountdown, ivAddRegular, ivAddActive, ivMenu, ivDeleteActiveAlarms;
    LinearLayoutCompat llActiveAlarms, llCountdowns, llRegularAlarms;

    RegularAndCountdownAdapter regularAdapter, countdownAdapter;
    ActiveAlarmsAdapter activeAdapter;

    SharedPreferences prefs;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init needed vars
        prefs = Utils.prefs(this);
        timer = new Timer();

        // actions to be performed on first start
        if (prefs.getBoolean(Constants.FIRST_START, true)) {
            prefs.edit().putBoolean(Constants.FIRST_START, false).apply();

            // copy default ringtone to default alarm sound path
            AlarmsLoader.resetAlarmSoundToSystemStandard(this);

            // execute defaulting actions
            DefaultActions.addDefaultAlarms(this);
        }

        // initialize views
        tvNextAlarm = findViewById(R.id.mainTextViewNextAlarm);
        rvCountdownAlarms = findViewById(R.id.mainGridViewCooldownAlarms);
        dragLvRegularAlarms = findViewById(R.id.mainGridViewRegularAlarms);
        rvActiveAlarms = findViewById(R.id.mainGridViewActiveAlarms);
        ivSettings = findViewById(R.id.mainClickableIvSettings);
        ivAddCountdown = findViewById(R.id.mainAddCountdownAlarm);
        ivAddRegular = findViewById(R.id.mainAddRegularAlarm);
        ivMenu = findViewById(R.id.mainClickableIvMenu);
        ivAddActive = findViewById(R.id.mainAddActiveAlarm);
        ivDeleteActiveAlarms = findViewById(R.id.mainDeleteAllActiveAlarms);
        llActiveAlarms = findViewById(R.id.mainLLActiveAlarms);
        llCountdowns = findViewById(R.id.mainLLCountdownAlarms);
        llRegularAlarms = findViewById(R.id.mainLLRegularAlarms);

        // init the recycler views
        Animation gridAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_top);
        for (GridRecyclerView rv : new GridRecyclerView[]{rvCountdownAlarms, dragLvRegularAlarms, rvActiveAlarms}) {
            // init and set animation controller
            GridLayoutAnimationController controller = new GridLayoutAnimationController(gridAnimation);
            controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
            controller.setDirection(GridLayoutAnimationController.DIRECTION_LEFT_TO_RIGHT | GridLayoutAnimationController.DIRECTION_TOP_TO_BOTTOM);
            controller.setColumnDelay(0.15f);
            controller.setRowDelay(0.15f);
            rv.setLayoutAnimation(controller);
            rv.startLayoutAnimation();
            // set layout manager (autofit rows)
            rv.setLayoutManager(
                    new GridAutofitLayoutManager(
                            this,
                            getResources().getDimensionPixelSize(R.dimen.alarm_item_width)
                    )
            );
        }

        // and corresponding adapters
        dragLvRegularAlarms.setAdapter(regularAdapter = new RegularAndCountdownAdapter(this));
        rvCountdownAlarms.setAdapter(countdownAdapter = new RegularAndCountdownAdapter(this));
        rvActiveAlarms.setAdapter(activeAdapter = new ActiveAlarmsAdapter(this));

        // start corresponding activities on iv click
        ivMenu.setOnClickListener(this::showPopupMenu);
        ivSettings.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), SettingsActivity.class)));

        // add buttons click actions
        ivAddActive.setOnClickListener(this::onAddActive);
        ivAddRegular.setOnClickListener(this::onAddRegular);
        ivAddCountdown.setOnClickListener(this::onAddCountdown);

        // listener on delete all active alarms
        ivDeleteActiveAlarms.setOnClickListener(v -> {
            for (Alarm alarm : AlarmController.activeAlarms(getApplicationContext())) {
                AlarmController.cancelAlarm(getApplicationContext(), alarm);
                NotificationManagerCompat.from(getApplicationContext()).cancel(alarm.id);
            }
            updateActiveAlarms();
        });

        // register receiver: dismissed upcoming alarm via notification
        // --> list shall of active alarms has to be updated
        registerReceiver(dismissedUpcomingAlarmReceiver, new IntentFilter(Constants.ACTION_NOTIFICATION_DISMISS_UPCOMING_ALARM));

        // update tv at fixed rate (alarms)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> updateActiveAlarms());
            }
        }, 0, Constants.MINUTE);


        // show tutorial without forcing on first start
        showTutorial(false);
    }


    /*
     * add alarm methods
     */
    void onAddActive(View view) {
        TimePickerDialog.showTimePickerDialog(this, time -> {
            AlarmController.scheduleAlarm(
                    getApplicationContext(),
                    new Alarm(0, time, AlarmType.REGULAR).makeActive(getApplicationContext()),
                    -1,
                    true
            );

            updateActiveAlarms();
        });
    }

    void onAddRegular(View view) {
        TimePickerDialog.showTimePickerDialog(this, time -> {
                    // add the new alarm
                    regularAdapter.add(
                            new Alarm(
                                    0, // id not important
                                    time,
                                    AlarmType.REGULAR
                            )
                    );
                }
        );
    }

    void onAddCountdown(View view) {
        TimePickerDialog.showCountdownPickerDialog(this, time -> {
                    // add the new alarm
                    countdownAdapter.add(
                            new Alarm(
                                    0, // id not important
                                    time,
                                    AlarmType.COUNTDOWN
                            )
                    );
                }
        );
    }

    BroadcastReceiver dismissedUpcomingAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateActiveAlarms();
        }
    };

    public void updateActiveAlarms() {
        // sets next alarm on text view on top of the app
        Alarm next = AlarmController.getNextAlarm(this);
        tvNextAlarm.setText(next == null ? getString(R.string.no_active_alarms) : DateUtils.formatTimePretty(next.time));

        // reload the whole adapter on active alarms
        activeAdapter.reloadAlarmsList();
    }

    void showPopupMenu(View view) {
        // create popup menu shown at position of view with inflating the
        // main menu xml file
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());

        // define actions of menu items
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menuMainAbout) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            } else if (id == R.id.menuMainShowTutorial) {
                showTutorial(true);
            }
            return true;
        });

        popup.show();
    }

    /*
     * show tutorial if has not been yet shown (or is set to be shown again)
     */
    void showTutorial(boolean forceShowing) {
        if (prefs.getBoolean(Constants.TUTORIAL_NOT_SEEN, true) || forceShowing) {
            // set false that has never seen tutorial
            prefs.edit().putBoolean(Constants.TUTORIAL_NOT_SEEN, false).apply();

            // show tutorial queue
            FancyShowCaseQueue queue = new FancyShowCaseQueue()
                    .add(new FancyShowCaseView.Builder(this)
                            .title(getString(R.string.tutorial_main_msg1))
                            .build())
                    .add(new FancyShowCaseView.Builder(this)
                            .focusShape(FocusShape.ROUNDED_RECTANGLE)
                            .focusOn(llActiveAlarms)
                            .title(getString(R.string.tutorial_main_msg2))
                            .build())
                    .add(new FancyShowCaseView.Builder(this)
                            .focusShape(FocusShape.ROUNDED_RECTANGLE)
                            .focusOn(llCountdowns)
                            .title(getString(R.string.tutorial_main_msg3))
                            .build())
                    .add(new FancyShowCaseView.Builder(this)
                            .focusShape(FocusShape.ROUNDED_RECTANGLE)
                            .focusOn(llRegularAlarms)
                            .title(getString(R.string.tutorial_main_msg4))
                            .build())
                    .add(new FancyShowCaseView.Builder(this)
                            .title(getString(R.string.tutorial_main_msg5))
                            .build())
                    .add(new FancyShowCaseView.Builder(this)
                            .focusOn(ivSettings)
                            .title(getString(R.string.tutorial_main_msg6))
                            .build());

            // on ending go to settings
            queue.setCompleteListener(() ->
                    startActivity(new Intent(this, SettingsActivity.class).putExtra(Constants.EXTRA_SHOW_TUTORIAL, true))
            );
            // and show
            queue.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // reload alarms of all types from prefs
        countdownAdapter.clear();
        regularAdapter.clear();
        countdownAdapter.addAll(AlarmsLoader.load(this, Constants.COUNTDOWN_ALARMS, AlarmType.COUNTDOWN));
        regularAdapter.addAll(AlarmsLoader.load(this, Constants.REGULAR_ALARMS, AlarmType.REGULAR));

        // update new alarm text view
        updateActiveAlarms();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save all alarm types to prefs
        // for that we first have to load data from sorted list
        // of the adapter
        List<Alarm> countdownAlarms = new ArrayList<>(), regularAlarms = new ArrayList<>();
        for (int i = 0; i < countdownAdapter.getItemCount(); i++)
            countdownAlarms.add(countdownAdapter.get(i));
        for (int i = 0; i < regularAdapter.getItemCount(); i++)
            regularAlarms.add(regularAdapter.get(i));
        // then save to prefs with helper methods
        AlarmsLoader.save(this, Constants.COUNTDOWN_ALARMS, countdownAlarms);
        AlarmsLoader.save(this, Constants.REGULAR_ALARMS, regularAlarms);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister receivers
        unregisterReceiver(dismissedUpcomingAlarmReceiver);

        // cancel timers
        if (timer != null)
            timer.cancel();
    }
}
/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 14.12.21, 20:45
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.service.AlarmService;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.ui.view.TwoWaySlider;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.skydoves.transformationlayout.TransformationLayout;

public class AlarmScreenActivity extends RootActivity {

    Alarm alarm;
    Settings settings;

    TwoWaySlider twoWaySlider;
    AnimatedTextView tvTriggerTime;
    TransformationLayout transformationLayoutResult;
    AppCompatImageView ivResult;

    boolean hardToCancel = false;

    int timesTriedToCancel = 0;
    boolean exitLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content
        setContentView(R.layout.activity_alarm_screen);

        // ensure the activity is also shown when screen is locked
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);

        }

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        // alarm id is stored as extra in the intent
        int alarmId = getIntent().getIntExtra(Constants.EXTRA_ALARM_ID, -1);
        // also boolean if is hard to cancel alarm
        hardToCancel = getIntent().getBooleanExtra(Constants.EXTRA_HARD_ALARM, false);

        // get the alarm instance from the id
        alarm = AlarmController.getAlarm(this, alarmId);

        //init other classes
        settings = SettingsLoader.load(this);

        twoWaySlider = findViewById(R.id.alarmScreenTwoWaySliderControl);
        tvTriggerTime = findViewById(R.id.alarmScreenTvTriggerTime);
        transformationLayoutResult = findViewById(R.id.alarmScreenTransformationLayout);
        ivResult = findViewById(R.id.alarmScreenIvResult);

        // set listeners on the two way slider
        // the right side is cancel, left side is snooze
        // perform according actions
        twoWaySlider.setListener(new TwoWaySlider.OnTwowaySliderListener() {
            @Override
            public void onSliderMoveLeft() {
                if (hardToCancel) {
                    // show message: no snooze
                    ToastMaker.make(getApplicationContext(), R.string.cannot_snooze_hard_to_disable_alarm);
                } else {
                    // snooze, start transition to result image view
                    // set correct image
                    ivResult.setImageResource(R.drawable.snooze);
                    snoozeAlarm();
                    transformationLayoutResult.startTransform();

                    exitService();
                    finishAfterWaiting();
                }
            }

            @Override
            public void onSliderMoveRight() {
                if (hardToCancel) {
                    hardToCancelExit();
                } else {
                    // dismiss, set correct image resource
                    ivResult.setImageResource(R.drawable.cross);
                    transformationLayoutResult.startTransform();

                    exitService();
                    finishAfterWaiting();
                }
            }

            @Override
            public void onSliderLongPress() {

            }
        });

        // set trigger time of alarm
        tvTriggerTime.set(DateUtils.getTimeStringH_mm_a(this, alarm.time));

        // register receiver to exit the app
        registerReceiver(broadcastReceiverDismissOrSnooze, Constants.INTENT_FILTER_NOTIFICATION_ACTIONS);
    }

    /*
     * simple puzzle for harder exiting alarm
     * has to pull the slider every time a message appears
     * this message appears n times after x seconds
     */
    void hardToCancelExit() {
        if (exitLocked) {
            tvTriggerTime.set(R.string.exit_puzzle_msg_bad);
        } else {
            timesTriedToCancel++;
        }

        if (timesTriedToCancel == 1) {
            ToastMaker.make(this, R.string.exit_puzzle_first_msg);
        } else if (timesTriedToCancel == 2) {
            tvTriggerTime.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.normal_text_size));
            tvTriggerTime.set(R.string.exit_puzzle_game_start_msg);
        } else if (timesTriedToCancel > 7) {
            // can exit now
            tvTriggerTime.set(R.string.exit_puzzle_enough);
            hardToCancel = false;
        } else {
            if (!exitLocked) {
                tvTriggerTime.set(R.string.exit_puzzle_msg_ok);
                // after n milliseconds show signal
                Constants.handler.postDelayed(() -> {
                    // show next signal
                    tvTriggerTime.set(R.string.exit_puzzle_now);
                    // not locked anymore
                    exitLocked = false;
                }, 2200 + Constants.random.nextInt(3900));
                // exit is locked from now on
                exitLocked = true;
            }
        }
    }

    void finishAfterWaiting() {
        Constants.handler.postDelayed(this::finishAndRemoveTask, 3 * Constants.SECOND);
    }

    BroadcastReceiver broadcastReceiverDismissOrSnooze = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // exit the activity immediately without interfering with stopping service in on destroy
            // since the activity has been stopped from the service
            finishAndRemoveTask();
        }
    };

    void snoozeAlarm() {
        // reschedule alarm in n millis
        AlarmController.scheduleAlarm(this, null, System.currentTimeMillis() + settings.snoozeCooldown, true);
    }

    void exitService() {
        // destroy service with the activity: to stop foreground,
        // pass extra (which will be passed onStartCommand)
        Intent serviceIntent = new Intent(this, AlarmService.class);
        serviceIntent.putExtra(Constants.EXTRA_EXIT, true);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiverDismissOrSnooze);
    }

    // this activity is declared as singleInstance -> alarm screen will always be created in single task with one instance only
    // if the activity is opened again (by the notification for example), it will be rerouted to this if the activity already exists
    // and not be opened in another instance
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    // disable clicking back on activity to finish
    @Override
    public void onBackPressed() {

    }
}
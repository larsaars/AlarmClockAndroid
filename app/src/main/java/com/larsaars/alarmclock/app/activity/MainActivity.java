package com.larsaars.alarmclock.app.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.larsaars.alarmclock.utils.SortedList;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.alarm.AlarmType;
import com.larsaars.alarmclock.utils.alarm.AlarmsLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RootActivity {

    GridRecyclerView rvActiveAlarms, rvCountdownAlarms, dragLvRegularAlarms;
    AppCompatTextView tvNextAlarm;
    RotatingClickableImageView ivSettings;
    ShiftingClickableImageView ivAddCountdown, ivAddRegular, ivAddActive, ivMenu, ivDeleteActiveAlarms;

    RegularAndCountdownAdapter regularAdapter, countdownAdapter;
    ActiveAlarmsAdapter activeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

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
            if (item.getItemId() == R.id.menuMainAbout) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
            return true;
        });

        popup.show();
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
        for(int i = 0; i < countdownAdapter.getItemCount(); i++)
            countdownAlarms.add(countdownAdapter.get(i));
        for(int i = 0; i < regularAdapter.getItemCount(); i++)
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
    }
}
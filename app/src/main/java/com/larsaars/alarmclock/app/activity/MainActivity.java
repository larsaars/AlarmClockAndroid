package com.larsaars.alarmclock.app.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.adapter.draglv.ActiveAlarmsAdapter;
import com.larsaars.alarmclock.ui.adapter.draglv.RegularAndCountdownAdapter;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.clickableiv.RotatingClickableImageView;
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.alarm.AlarmType;
import com.larsaars.alarmclock.utils.alarm.AlarmsLoader;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RootActivity {

    DragListView dragLvActiveAlarms, dragLvCountdownAlarms, dragLvRegularAlarms;
    AppCompatTextView tvNextAlarm;
    RotatingClickableImageView ivAbout, ivSettings;
    ShiftingClickableImageView ivAddCountdown, ivAddRegular;

    List<Alarm> countdownAlarms = new ArrayList<>(), regularAlarms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // initialize views
        tvNextAlarm = findViewById(R.id.mainTextViewNextAlarm);
        dragLvCountdownAlarms = findViewById(R.id.mainGridViewCooldownAlarms);
        dragLvRegularAlarms = findViewById(R.id.mainGridViewRegularAlarms);
        dragLvActiveAlarms = findViewById(R.id.mainGridViewActiveAlarms);
        ivAbout = findViewById(R.id.mainClickableIvAbout);
        ivSettings = findViewById(R.id.mainClickableIvSettings);
        ivAddCountdown = findViewById(R.id.mainAddCountdownAlarm);
        ivAddRegular = findViewById(R.id.mainAddRegularAlarm); //TODO

        // init the drag list views
        for (DragListView dragLv : new DragListView[]{dragLvCountdownAlarms, dragLvRegularAlarms, dragLvActiveAlarms})
            dragLv.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));

        // and corresponding adapters
        dragLvRegularAlarms.setAdapter(new RegularAndCountdownAdapter(this, regularAlarms), true);
        dragLvCountdownAlarms.setAdapter(new RegularAndCountdownAdapter(this, countdownAlarms), true);

        // start corresponding activities on iv click
        ivAbout.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AboutActivity.class)));
        ivSettings.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), SettingsActivity.class)));

        // register receiver: dismissed upcoming alarm via notification
        // --> list shall of active alarms has to be updated
        registerReceiver(dismissedUpcomingAlarmReceiver, new IntentFilter(Constants.ACTION_NOTIFICATION_DISMISS_UPCOMING_ALARM));
    }

    void setDragging(DragListView dragLv, boolean v) {
        dragLv.setCanDragVertically(v);
        dragLv.setCanDragHorizontally(v);
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
        dragLvActiveAlarms.setAdapter(new ActiveAlarmsAdapter(this), true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        AlarmController.scheduleAlarm(this, null, System.currentTimeMillis() + Constants.SECOND * 20);

        // reload alarms of all types from prefs
        countdownAlarms.clear();
        regularAlarms.clear();
        countdownAlarms.addAll(AlarmsLoader.load(this, Constants.COUNTDOWN_ALARMS, AlarmType.COUNTDOWN));
        regularAlarms.addAll(AlarmsLoader.load(this, Constants.REGULAR_ALARMS, AlarmType.REGULAR));

        // notify update of the other datasets
        dragLvCountdownAlarms.getAdapter().notifyDataSetChanged();
        dragLvRegularAlarms.getAdapter().notifyDataSetChanged();

        // update new alarm text view
        updateActiveAlarms();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save all alarm types to prefs
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
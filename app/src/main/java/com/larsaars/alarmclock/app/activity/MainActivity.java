package com.larsaars.alarmclock.app.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.widget.GridView;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

public class MainActivity extends RootActivity {

    GridView gridLayoutCooldownButtons;
    AppCompatTextView tvNextAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // initialize views
        tvNextAlarm = findViewById(R.id.mainTextViewNextAlarm);
        gridLayoutCooldownButtons = findViewById(R.id.mainGridViewCooldownAlarms);

    }

    // sets next alarm on text view on top of the app
    void updateNextAlarmTV() {
        Alarm next = AlarmController.getNextAlarm(this);
        tvNextAlarm.setText(next == null ? getString(R.string.no_active_alarms) : DateUtils.formatTimeLong(next.triggerTime));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // update new alarm text view
        updateNextAlarmTV();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
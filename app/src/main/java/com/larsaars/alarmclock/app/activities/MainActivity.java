package com.larsaars.alarmclock.app.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.widget.GridLayout;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

public class MainActivity extends AppCompatActivity {

    AlarmController alarmController;

    GridLayout gridLayoutCooldownButtons;
    AppCompatTextView tvNextAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // init variables
        alarmController = new AlarmController(this);

        // initialize views
        tvNextAlarm = findViewById(R.id.mainTextViewNextAlarm);
        gridLayoutCooldownButtons = findViewById(R.id.mainGridLayoutCooldownAlarms);


        // init values
        updateNextAlarmTV();

    }

    void updateNextAlarmTV() {
        Alarm next = alarmController.getNextAlarm();
        tvNextAlarm.setText(next == null ? getString(R.string.no_active_alarms) : Utils.formatTimeLong(next.triggerTime));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save all alarms on application pause
        alarmController.save();
    }
}
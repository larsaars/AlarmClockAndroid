package com.larsaars.alarmclock.app.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.SupportActionModeWrapper;

import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.utils.alarm.AlarmController;

public class MainActivity extends AppCompatActivity {

    AlarmController alarmController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        alarmController = new AlarmController(this);

    }
}
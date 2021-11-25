package com.larsaars.alarmclock.app.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ClickableImageView;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.woxthebox.draglistview.DragListView;

public class MainActivity extends RootActivity {

    DragListView dragLvActiveAlarms, dragLvCooldownAlarms, dragLvRegularAlarms;
    AppCompatTextView tvNextAlarm;
    ClickableImageView ivAbout, ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // initialize views
        tvNextAlarm = findViewById(R.id.mainTextViewNextAlarm);
        dragLvCooldownAlarms = findViewById(R.id.mainGridViewCooldownAlarms);
        dragLvRegularAlarms = findViewById(R.id.mainGridViewRegularAlarms);
        dragLvActiveAlarms = findViewById(R.id.mainGridViewActiveAlarms);
        ivAbout = findViewById(R.id.mainClickableIvAbout);
        ivSettings = findViewById(R.id.mainClickableIvSettings);


        // start corresponding activities on iv click
        ivAbout.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AboutActivity.class)));
        ivSettings.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), SettingsActivity.class)));
    }

    // setup drag list view for item drag and dropping
    void setupDragLv(DragListView dragLv) {
        // 3 columns
        dragLv.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
        // set can drag
        dragLv.setCanDragHorizontally(true);
        dragLv.setCanDragVertically(true);
        dragLv.setCustomDragItem(null);
    }

    // sets next alarm on text view on top of the app
    void updateNextAlarmTV() {
        Alarm next = AlarmController.getNextAlarm(this);
        tvNextAlarm.setText(next == null ? getString(R.string.no_active_alarms) : DateUtils.formatTimeLong(next.time));
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
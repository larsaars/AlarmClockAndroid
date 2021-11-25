package com.larsaars.alarmclock.app.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.adapter.draglv.ActiveAlarmsAdapter;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.Logg;
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

        // init the drag list views
        setupDragLv(dragLvActiveAlarms, dragLvCooldownAlarms, dragLvRegularAlarms);
        // and corresponding adapters


        // start corresponding activities on iv click
        ivAbout.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), AboutActivity.class)));
        ivSettings.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), SettingsActivity.class)));
    }

    // setup drag list view for item drag and dropping
    void setupDragLv(DragListView... dragLvs) {
        for(DragListView dragLv : dragLvs) {
            // 3 columns
            dragLv.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));
            // set can drag
            dragLv.setCanDragHorizontally(true);
            dragLv.setCanDragVertically(true);
            dragLv.setCustomDragItem(null);
        }
    }

    void updateActiveAlarms() {
        // sets next alarm on text view on top of the app
        Alarm next = AlarmController.getNextAlarm(this);
        tvNextAlarm.setText(next == null ? getString(R.string.no_active_alarms) : DateUtils.formatTimePretty(next.time));

        // reload the whole adapter on active alarms
        dragLvActiveAlarms.setAdapter(new ActiveAlarmsAdapter(getBaseContext()), false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmController.scheduleAlarm(this, null, System.currentTimeMillis() + Constants.SECOND * 20);

        // update new alarm text view
        updateActiveAlarms();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
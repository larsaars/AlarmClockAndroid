/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 23.11.21, 18:07
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.theme.ThemeUtils;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Utils;
import com.larsaars.alarmclock.utils.alarm.AlarmsLoader;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends RootActivity {

    Settings settings;

    AudioManager audioManager;

    LinearLayoutCompat llTheme, llRingtone, llRingtoneReset, llCustomizeIntervalAlarms;
    AppCompatTextView tvTheme;
    AppCompatSeekBar sbVolume;
    SwitchCompat switchVibrate;

    int themeSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // init necessary classes
        settings = SettingsLoader.load(this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // init views
        llTheme = findViewById(R.id.settingsClickableThemeLL);
        tvTheme = findViewById(R.id.settingsTvCurrentTheme);
        sbVolume = findViewById(R.id.settingsSeekBarAlarmVolume);
        switchVibrate = findViewById(R.id.settingsCBVibrateOnAlarm);
        llRingtone = findViewById(R.id.settingsClickableChangeRingtoneLL);
        llRingtoneReset = findViewById(R.id.settingsClickableResetRingtoneLL);
        llCustomizeIntervalAlarms = findViewById(R.id.settingsClickableCustomizeAlarmSoundsLL);

        // set initial values
        tvTheme.setText(getStringArray(R.array.theme_options)[getCurrentTheme()]);
        sbVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        sbVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        switchVibrate.setChecked(settings.vibrationOn);

        // and on click listeners
        llTheme.setOnClickListener(v -> showChangeThemeDialog());
        llRingtone.setOnClickListener(v -> changeRingtoneWithPermissionCheck(this, Constants.DEFAULT_RINGTONE_FILE(this), () -> ToastMaker.make(this, R.string.ringtone_changed)));
        llRingtoneReset.setOnClickListener(v -> AlarmsLoader.resetAlarmSoundToSystemStandard(this));
        llCustomizeIntervalAlarms.setOnClickListener(v -> startActivity(new Intent(this, CustomizeAlarmSoundsActivity.class)));

        // listeners for changing values
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, seekBar.getProgress(), 0);
            }
        });

        switchVibrate.setOnCheckedChangeListener((buttonView, isChecked) -> settings.vibrationOn = isChecked);
    }

    public static void changeRingtoneWithPermissionCheck(RootActivity context, File file, @Nullable Runnable result) {
        RequestPermissionActivity.checkPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                () -> {
                    changeRingtone(context, file);
                    if(result != null)
                        result.run();
                }
        );
    }

    static void changeRingtone(RootActivity context, File file) {
        // create file chooser intent
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("audio/*");
        Intent chooser = Intent.createChooser(chooseFile, context.getString(R.string.choose_a_file));

        // launch for activity result
        context.activityLauncher.launch(chooser, result -> {
            if (result.getResultCode() == RESULT_OK) {
                assert result.getData() != null;
                // copy to file
                try {
                    Utils.inputStreamToFile(
                            context.getContentResolver().openInputStream(result.getData().getData()),
                            file
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    int getCurrentTheme() {
        // bright, dark, auto
        int theme = 2;
        if (ThemeUtils.isToggleEnabled(this))
            theme = ThemeUtils.isDarkMode(this) ? 1 : 0;
        return theme;
    }

    void showChangeThemeDialog() {
        CDialog.alertDialog(this)
                .setTitle(R.string.setting_title_theme)
                .setSingleChoiceItems(getStringArray(R.array.theme_options), getCurrentTheme(), (dialog, item) -> {
                    themeSelection = item;
                }).setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // set night mode settings and recreate activity
                    // in order to change theme
                    ThemeUtils.setIsToggleEnabled(getApplicationContext(), themeSelection != 2);

                    if (themeSelection != 2)
                        ThemeUtils.setIsNightModeEnabled(getApplicationContext(), themeSelection == 1);

                    ThemeUtils.ensureAutomaticThemeIsSet(this);
                    recreate();
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SettingsLoader.save(this, settings);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        settings = SettingsLoader.load(this);
    }
}
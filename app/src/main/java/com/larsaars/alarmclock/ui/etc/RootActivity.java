/*
 *  Created by Lurzapps
 *  Copyright (c) 2020. All rights reserved.
 *  last modified by larsl on 20.08.20 12:08
 *  project NHIE in module app
 */

package com.larsaars.alarmclock.ui.etc;

import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.theme.ThemeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RootActivity extends AppCompatActivity {
    public boolean finish = true;
    private int onStartCount = 0;
    private final ArrayList<OnDestroyListener> onDestroyListeners = new ArrayList<>();

    public interface OnDestroyListener {
        void onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.animation_enter_activity,
                    R.anim.animation_leave_activity);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }

        // ensure app theme (dark or bright mode)
        AppCompatDelegate.setDefaultNightMode(
                ThemeUtils.isNightModeEnabled(this) ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.animation_enter_activity,
                    R.anim.animation_leave_activity);

        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

    public String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    public double randomProb() {
        return Math.random();
    }

    public void finish(boolean force) {
        if (force)
            super.finish();
        else
            finish();
    }

    public void addOnDestroyListener(OnDestroyListener listener) {
        if (listener != null)
            onDestroyListeners.add(listener);
    }

    public int getColor_(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    public String getQuantityString(@PluralsRes int resID, int count, Object... format) {
        return getResources().getQuantityString(resID, count, format);
    }

    @Override
    public void finish() {
        if (!isFinishing() && finish)
            super.finish();
    }

    @Override
    protected void onDestroy() {
        //alert all listeners
        for (OnDestroyListener listener : onDestroyListeners)
            listener.onDestroy();
        //alert super class
        super.onDestroy();
    }

    RootActivity getActivity() {
        return this;
    }
}

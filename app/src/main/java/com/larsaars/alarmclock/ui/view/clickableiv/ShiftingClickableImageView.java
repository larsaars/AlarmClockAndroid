/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 23.11.21, 18:05
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.ui.view.clickableiv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class ShiftingClickableImageView extends AppCompatImageView {

    OnClickListener onClickListener;
    Animation rotateSelf;

    public ShiftingClickableImageView(@NonNull Context context) {
        super(context);
        init();
    }

    public ShiftingClickableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShiftingClickableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // on initializing set on click listener
        // on click play animation on self
        // after anim finished execute on click action
        super.setOnClickListener(v -> {
            YoYo.with(Techniques.Wobble)
                    .duration(150)
                    .playOn(v);

            if(onClickListener != null) onClickListener.onClick(ShiftingClickableImageView.this);
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        onClickListener = listener;
    }
}

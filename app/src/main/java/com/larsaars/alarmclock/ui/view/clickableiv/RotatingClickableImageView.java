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

public class RotatingClickableImageView extends AppCompatImageView {

    OnClickListener onClickListener;
    Animation rotateSelf;

    public RotatingClickableImageView(@NonNull Context context) {
        super(context);
        init();
    }

    public RotatingClickableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotatingClickableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // on initializing set on click listener
        // on click play animation on self
        // after anim finished execute on click action
        rotateSelf = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateSelf.setDuration(320);

        rotateSelf.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (onClickListener != null)
                    onClickListener.onClick(RotatingClickableImageView.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        super.setOnClickListener(v -> v.startAnimation(rotateSelf));
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        onClickListener = listener;
    }
}

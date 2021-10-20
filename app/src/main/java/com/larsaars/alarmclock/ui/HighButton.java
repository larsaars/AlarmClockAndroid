package com.larsaars.alarmclock.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.larsaars.alarmclock.R;

/**
 * TODO: document your custom view class.
 */
public class HighButton extends AppCompatButton {
    public HighButton(@NonNull Context context) {
        super(context);
    }

    public HighButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HighButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
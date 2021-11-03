/*
 *  Created by Lurzapps
 *  Copyright (c) 2020. All rights reserved.
 *  last modified by larsl on 30.09.20 01:19
 *  project NHIE in module app
 */

package com.larsaars.alarmclock.ui.view;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;


//from: https://stackoverflow.com/questions/6612316/how-set-spannable-object-font-with-custom-font
public class CustomFontSpan extends TypefaceSpan {

    private final Typeface newType;

    public CustomFontSpan(Typeface type) {
        super("");
        newType = type;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}

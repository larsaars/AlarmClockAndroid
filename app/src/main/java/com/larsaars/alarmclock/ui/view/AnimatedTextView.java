/*
 *  Created by Lurzapps
 *  Copyright (c) 2020. All rights reserved.
 *  last modified by larsl on 20.08.20 18:31
 *  project NHIE in module app
 */

package com.larsaars.alarmclock.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.CustomFontSpan;

public class AnimatedTextView extends AppCompatTextView {

    public boolean slideOnChange = true;

    private Animation slideIn /*in*/, slideOut /*out*/;
    private String newText;
    private boolean animRunning = false, autoCutOffText = false;

    private Typeface bold, italic;

    private static final String TAG = AnimatedTextView.class.getSimpleName();

    public AnimatedTextView(Context context) {
        super(context);
        init(context, null);
    }

    public AnimatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("WrongConstant")
    private void init(Context context, AttributeSet attrs) {
        slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_left);
        slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_right);

        //listener with actions for the slide out animation
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //anim not running anymore
                animRunning = false;
                //set the new text
                setText(prepareText());
                //slide out has ended, start slide in
                startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //load the typefaces
        bold = ResourcesCompat.getFont(context, R.font.bold);
        italic = ResourcesCompat.getFont(context, R.font.italic);
        //get the attrs
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnimatedTextView);
        autoCutOffText = ta.getBoolean(R.styleable.AnimatedTextView_autoCutOffText, false);
        ta.recycle();
        //set simple break strategy
        if (autoCutOffText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE);
                } else {
                    setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
                }
                setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
            }
        }
    }

    private Spanned prepareText() {
        //log the new text
        Log.d(TAG, newText);
        //look out that every line of the newText string fits in the line, else cut off and finish with _points
        if (autoCutOffText) {
            //the result string
            StringBuilder result = new StringBuilder();
            //the view width and measure paint
            int width = getWidth();
            Paint measurePaint = getPaint();
            //loop through each line
            String[] lines = newText.split("\n");
            for (int i = 0; i < lines.length; i++) {
                //append the line again
                result.append(i == 0 ? "" : "\n");
                //get the element
                String line = lines[i];
                //measure text width
                float textWidth = measurePaint.measureText(line);
                int MINUS = 10;
                if (textWidth > width && line.length() > (MINUS + 4)) {
                    //only if the measured text width is longer than the view width, there has to be done sth
                    //measure how many characters would fit in the line
                    int numChars;
                    for (numChars = 1; numChars <= line.length(); numChars++) {
                        if (measurePaint.measureText(line, 0, numChars) > width) {
                            break;
                        }
                    }

                    //now that we got the num of chars, cut of those which are too many
                    String _points = "…";
                    result.append(line.substring(0, numChars - MINUS)).append(_points);
                } else {
                    result.append(line);
                }
            }
            //set newText to result
            newText = result.toString();
        }
        //span the html text and replace line breaks with html tags
        Spannable spanned = new SpannableString(HtmlCompat.fromHtml(newText.replace("\n", "<br>"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        //replace the bold and italic tags with the right fonts
        // get all spans in this range
        Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
        for (Object obj : spans) {
            //now get the type of the spans.
            if (obj instanceof StyleSpan) {
                //to style span
                StyleSpan span = (StyleSpan) obj;
                //if they are bold or italic, replace
                boolean isBold = span.getStyle() == Typeface.BOLD;
                if (isBold || span.getStyle() == Typeface.ITALIC) {
                    int start = spanned.getSpanStart(obj), end = spanned.getSpanEnd(obj);
                    spanned.removeSpan(span);
                    spanned.setSpan(new CustomFontSpan(isBold ? bold : italic), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
        }


        return spanned;
    }

    public void set(@NonNull String text) {
        // if shall not slide on change, check if new text is same as old
        if (!slideOnChange && text.equals(getText().toString()))
            return;

        //if the anim is still running, end it and set the right text to slide out
        if (animRunning) {
            clearAnimation();
            setText(newText);
        }

        //set the new text
        newText = text;
        //and start the slide out anim
        startAnimation(slideOut);
        //anim is running
        animRunning = true;
    }

    public void clear() {
        set("");
    }

    public void set(@StringRes int resId, Object... format) {
        set(getContext().getString(resId, format));
    }

    public void set(@PluralsRes int resId, int quantity, Object... format) {
        set(String.format(getContext().getResources().getQuantityString(resId, quantity), format));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @SuppressLint("WrongConstant")
    public void setAutoCutOffText(boolean autoCutOffText0) {
        autoCutOffText = autoCutOffText0;

        if (autoCutOffText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE);
                } else {
                    setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
                }
                setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
            }
        }
    }

    public boolean getAutoCutOffText() {
        return autoCutOffText;
    }
}

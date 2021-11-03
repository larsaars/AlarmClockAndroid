package com.larsaars.alarmclock.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.larsaars.alarmclock.R;

import info.hoang8f.widget.FButton;

public class HighlightableButton extends FButton {
    boolean highlighted = false;
    Runnable onHighlightChangeListener;

    public HighlightableButton(@NonNull Context context) {
        super(context);
        init();
    }

    public HighlightableButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HighlightableButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        setHighlighted(true);

        setOnClickListener(view -> setHighlighted(!highlighted));
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;

        setButtonColor(ContextCompat.getColor(getContext(), highlighted ? R.color.colorButtonActivated : R.color.colorButtonNormal));
        setTextColor(ContextCompat.getColor(getContext(), highlighted ? R.color.white : R.color.black));

        if (onHighlightChangeListener != null) onHighlightChangeListener.run();
    }

    public void setOnHighlightChangeListener(Runnable listener) {
        onHighlightChangeListener = listener;
    }
}

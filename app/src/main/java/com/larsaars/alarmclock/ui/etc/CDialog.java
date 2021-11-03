/*
 *  Created by Lurzapps
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 17.09.21, 01:12
 *  project NHIE in module NHIE.app
 */

package com.larsaars.alarmclock.ui.etc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.larsaars.alarmclock.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

public class CDialog extends Dialog implements
        KeyboardVisibilityEventListener,
        RootActivity.OnDestroyListener {

    private InputMethodManager inputMethodManager;
    private boolean isInitialized = false, keyboardVisible = false;
    private Unregistrar unregistrar;
    private OutsideClickListener outsideClickListener;
    private boolean cancelable = true;

    public interface OutsideClickListener {
        void onOutsideClick();
    }

    public CDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected CDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    @NonNull
    public static AlertDialog.Builder alertDialog(Context context) {
        return new AlertDialog.Builder(context, R.style.DialogSlideAnim);
    }

    @NonNull
    public static CDialog dialog(Context context) {
        return new CDialog(context, R.style.DialogSlideAnim);
    }

    @NonNull
    public static AlertDialog.Builder fullScreenAlertDialog(Context context) {
        return new AlertDialog.Builder(context, R.style.FullScreenDialog);
    }

    @NonNull
    public static CDialog fullScreenDialog(Context context) {
        return new CDialog(context, R.style.FullScreenDialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void cancel() {
        super.dismiss();
    }

    private void init(Context context) {
        if (isInitialized)
            return;
        else
            isInitialized = true;

        //set imm
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //handle outside touch in this class
        setCanceledOnTouchOutside(false);
    }

    public void setOutsideTouchListener(RootActivity activity, OutsideClickListener listener) {
        unregistrar = KeyboardVisibilityEvent.INSTANCE.registerEventListener(activity, this);
        activity.addOnDestroyListener(this);
        outsideClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        //get outside of dialog touch
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //determine if touch was inside dialog window or outside
            assert getWindow() != null;
            Rect outRect = new Rect();
            getWindow().getDecorView().getHitRect(outRect);
            boolean outside = !outRect.contains((int) event.getX(), (int) event.getY());
            if (outside) {
                //outside touch
                //if keyboard is open, cancel keyboard
                if (keyboardVisible) {
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } else {
                    if (outsideClickListener == null) {
                        if(cancelable)
                            cancel();
                    }else
                        outsideClickListener.onOutsideClick();
                }
            }
        }
        //return to superclass
        return super.onTouchEvent(event);
    }

    @Override
    public void onDestroy() {
        if (unregistrar != null)
            unregistrar.unregister();
    }

    @Override
    public void onVisibilityChanged(boolean b) {
        keyboardVisible = b;
    }

    @Override
    public void setCancelable(boolean cancelable0) {
        cancelable = cancelable0;
        super.setCancelable(cancelable0);
    }

    public boolean isCancelable() {
        return cancelable;
    }
}
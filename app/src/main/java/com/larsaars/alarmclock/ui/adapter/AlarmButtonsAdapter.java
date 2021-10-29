package com.larsaars.alarmclock.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.view.HighlightableButton;

public class AlarmButtonsAdapter extends BaseAdapter {
    Context context;
    int logos[];
    LayoutInflater inflater;

    public void TimeButtonAdapter(Context applicationContext, int[] logos) {
        this.context = applicationContext;
        this.logos = logos;

        inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return logos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View rootView, ViewGroup viewGroup) {
        if(rootView == null)
            rootView = inflater.inflate(R.layout.grid_view_item_alarm_button, null); // inflate the layout (only once)

        HighlightableButton highlightableButton = rootView.findViewById(R.id.gridViewItemAlarmButtonHighlightableButton); // get reference from the rootView
        highlightableButton.setText();

        return rootView;
    }
}

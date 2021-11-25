package com.larsaars.alarmclock.ui.adapter.draglv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmType;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

public class CountdownsAdapter extends DragItemAdapter<Alarm, CountdownsAdapter.ViewHolder> {

    Settings settings;

    public CountdownsAdapter(Context context, List<Alarm> list) {
        settings = SettingsLoader.load(context);

        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Alarm alarm = mItemList.get(position);

        if(alarm.type != AlarmType.PSEUDO)
            holder.tv.set(alarm.formatToText());
        else {
            // since this is the add item,
            // show the add view and hide the normal item view
            // did not find a better way to do this
            holder.itemView.findViewById(R.id.itemAddAlarmLL).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.itemAlarmDefaultItemLL).setVisibility(View.GONE);
        }

        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).id;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {

        AnimatedTextView tv;
        ShiftingClickableImageView minus, plus, delete;

        ViewHolder(final View itemView) {
            super(itemView, R.id.itemActiveAlarmText, false);

            // init views and disable animating again every reload of view
            tv = itemView.findViewById(R.id.itemActiveAlarmText);
            tv.slideOnChange = false;

            minus = itemView.findViewById(R.id.itemActiveAlarmMinus);
            plus = itemView.findViewById(R.id.itemActiveAlarmsPlus);
            delete = itemView.findViewById(R.id.itemActiveAlarmsDelete);

            // place on click listeners
            // on each of these actions the whole adapter has to be reloaded afterwards
            // because the active alarms change
            minus.setOnClickListener(v -> {
                int index = mItemList.indexOf(v.getTag());
                mItemList.get(index).time -= settings.rescheduleTime;
                CountdownsAdapter.this.notifyItemChanged(index);
            });

            plus.setOnClickListener(v -> {
                int index = mItemList.indexOf(v.getTag());
                mItemList.get(index).time += settings.rescheduleTime;
                CountdownsAdapter.this.notifyItemChanged(index);
            });

            // on delete play animation, then notify of removing item
            delete.setOnClickListener(v -> {
                int index = mItemList.indexOf(v.getTag());
                mItemList.remove(index);

                YoYo.with(Techniques.Wave)
                        .duration(150)
                        .onEnd(animator -> CountdownsAdapter.this.notifyItemRemoved(index))
                        .playOn(itemView);
            });
        }
    }
}
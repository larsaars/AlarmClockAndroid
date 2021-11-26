package com.larsaars.alarmclock.ui.adapter.draglv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.MainActivity;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

public class RegularAndCountdownAdapter extends DragItemAdapter<Alarm, RegularAndCountdownAdapter.ViewHolder> {

    Settings settings;
    MainActivity mainActivity;

    public RegularAndCountdownAdapter(MainActivity mainActivity, List<Alarm> list) {
        this.mainActivity = mainActivity;
        settings = SettingsLoader.load(mainActivity);

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
        holder.tv.set(alarm.formatToText());
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
                Alarm alarm = (Alarm) v.getTag();
                int index = mItemList.indexOf(alarm);

                alarm.time = Math.max(0, alarm.time - settings.rescheduleTime);

                RegularAndCountdownAdapter.this.notifyItemChanged(index);
            });

            plus.setOnClickListener(v -> {
                Alarm alarm = (Alarm) v.getTag();
                int index = mItemList.indexOf(alarm);

                alarm.time = Math.min(Constants.HOUR * 24, alarm.time + settings.rescheduleTime);

                RegularAndCountdownAdapter.this.notifyItemChanged(index);
            });

            // on delete play animation, then notify of removing item
            delete.setOnClickListener(v -> {
                Alarm alarm = (Alarm) v.getTag();
                int index = mItemList.indexOf(alarm);

                mItemList.remove(index);

                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .onEnd(animator -> RegularAndCountdownAdapter.this.notifyItemRemoved(index))
                        .playOn(itemView);
            });

            // on text view click schedule alarm
            tv.setOnClickListener(v -> {
                // animate
                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .playOn(tv);
                // schedule, conversion to active alarm happens automatically in schedule method
                AlarmController.scheduleAlarm(v.getContext(), (Alarm) v.getTag(), -1);
                // tell main activity that active alarms have been updated
                mainActivity.updateActiveAlarms();
            });
        }
    }
}
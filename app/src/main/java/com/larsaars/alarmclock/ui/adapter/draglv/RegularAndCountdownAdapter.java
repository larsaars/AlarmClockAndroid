package com.larsaars.alarmclock.ui.adapter.draglv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.MainActivity;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Alarm alarm = mItemList.get(position);
        holder.tv.setText(alarm.formatToText(mainActivity));
        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).id;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {

        AppCompatTextView tv;
        ShiftingClickableImageView minus, plus, delete;
        LinearLayoutCompat dragCorpus;

        ViewHolder(final View itemView) {
            super(itemView, R.id.itemAlarmDragCorpus, false);

            // init views
            tv = itemView.findViewById(R.id.itemAlarmText);

            minus = itemView.findViewById(R.id.itemAlarmMinus);
            plus = itemView.findViewById(R.id.itemAlarmPlus);
            delete = itemView.findViewById(R.id.itemAlarmDelete);

            // place on click listeners
            // on each of these actions the whole adapter has to be reloaded afterwards
            // because the active alarms change
            minus.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = mItemList.indexOf(alarm);

                alarm.time = Math.max(0, alarm.time - settings.rescheduleTime);

                RegularAndCountdownAdapter.this.notifyItemChanged(index);
            });

            plus.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = mItemList.indexOf(alarm);

                alarm.time = Math.min(Constants.HOUR * 24, alarm.time + settings.rescheduleTime);

                RegularAndCountdownAdapter.this.notifyItemChanged(index);
            });

            // on delete play animation, then notify of removing item
            delete.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = mItemList.indexOf(alarm);

                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .onEnd(animator -> RegularAndCountdownAdapter.this.removeItem(index))
                        .playOn(itemView);
            });

            // on text view click schedule alarm
            tv.setOnClickListener(v -> {
                // animate
                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .playOn(tv);
                // schedule, conversion to active alarm happens automatically in schedule method
                AlarmController.scheduleAlarm(v.getContext(), (Alarm) itemView.getTag(), -1, true);
                // tell main activity that active alarms have been updated
                mainActivity.updateActiveAlarms();
            });
        }
    }
}